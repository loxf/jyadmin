package org.loxf.jyadmin.biz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.util.IdGenerator;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.biz.util.TencentVideoV2;
import org.loxf.jyadmin.client.dto.VideoConfigDto;
import org.loxf.jyadmin.client.service.VideoConfigService;
import org.loxf.jyadmin.dal.dao.VideoConfigMapper;
import org.loxf.jyadmin.dal.po.VideoConfig;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("videoConfigService")
public class VideoConfigServiceImpl implements VideoConfigService {
    private static String prefix = "VD";
    @Autowired
    private VideoConfigMapper videoConfigMapper ;

    @Override
    @Transactional
    public BaseResult<String> addVideo(VideoConfigDto dto) {
        if(StringUtils.isBlank(dto.getVideoName())){
            return new BaseResult<>(BaseConstant.FAILED, "参数不全");
        }
        VideoConfig config = new VideoConfig();
        BeanUtils.copyProperties(dto, config);
        String videoId = IdGenerator.generate(prefix);
        config.setVideoId(videoId);
        if(videoConfigMapper.insert(config)>0) {
            return new BaseResult<>(videoId);
        } else {
            return new BaseResult<>(BaseConstant.FAILED, "新增失败");
        }
    }

    @Override
    @Transactional
    public BaseResult<String> delVideo(String videoId) {
        videoConfigMapper.deleteByPrimaryKey(videoId);
        return new BaseResult<>(videoId);
    }

    @Override
    @Transactional
    public BaseResult<String> editVideo(VideoConfigDto dto) {
        if(StringUtils.isBlank(dto.getVideoId())){
            return new BaseResult<>(BaseConstant.FAILED, "主键为空");
        }
        VideoConfig config = new VideoConfig();
        BeanUtils.copyProperties(dto, config);
        videoConfigMapper.updateByPrimaryKey(config);
        return new BaseResult<>(dto.getVideoId());
    }

    @Override
    public BaseResult<VideoConfigDto> queryVideo(String videoId) {
        VideoConfig videoConfig = videoConfigMapper.selectByPrimaryKey(videoId);
        if(videoConfig!=null) {
            VideoConfigDto dto = new VideoConfigDto();
            BeanUtils.copyProperties(videoConfig, dto);
            return new BaseResult<>(dto);
        } else {
            return new BaseResult<>(BaseConstant.FAILED, "视频不存在");
        }
    }

    @Override
    public PageResult<VideoConfigDto> pager(VideoConfigDto catalogDto) {
        VideoConfig offerCatalog = new VideoConfig();
        BeanUtils.copyProperties(catalogDto, offerCatalog);
        int total = videoConfigMapper.count(offerCatalog);
        List<VideoConfigDto> ret = new ArrayList<>();
        if(total>0) {
            List<VideoConfig> list = videoConfigMapper.list(offerCatalog);
            if(CollectionUtils.isNotEmpty(list)) {
                for (VideoConfig tmp : list) {
                    VideoConfigDto dto = new VideoConfigDto();
                    BeanUtils.copyProperties(tmp, dto);
                    ret.add(dto);
                }
            }
        }
        int totalPage = total/catalogDto.getPager().getSize() + (total%catalogDto.getPager().getSize()==0?0:1);
        return new PageResult<>(totalPage, catalogDto.getPager().getPage(), total, ret);
    }

    @Override
    @Transactional
    public BaseResult updateProgress(VideoConfigDto dto) {
        if(StringUtils.isBlank(dto.getVideoId())){
            return new BaseResult(BaseConstant.FAILED, "主键为空");
        }
        VideoConfig config = new VideoConfig();
        BeanUtils.copyProperties(dto, config);
        return new BaseResult(videoConfigMapper.updateProgress(config));
    }

    @Override
    public BaseResult<String> queryUrl(String videoId, String type) {
        VideoConfig config = videoConfigMapper.selectByPrimaryKey(videoId);
        if(config==null){
            return new BaseResult<>(BaseConstant.FAILED, "视频不存在");
        } else {
            String url = null;
            String metaData = config.getMetaData();
            if(StringUtils.isNotBlank(metaData)){
                JSONObject metaDataJson = JSON.parseObject(metaData);
                url = metaDataJson.getString(type);
            }
            if(StringUtils.isBlank(url)){
                if(StringUtils.isBlank(config.getVideoOutId())){
                    return new BaseResult<>(BaseConstant.FAILED, "外部视频ID不存在");
                }
                // 调取腾讯云获取转码后的信息。
                String [] infoFilters = {"transcodeInfo"};
                JSONObject jsonObject = TencentVideoV2.queryVideoInfo(config.getVideoOutId(), infoFilters);
                if(jsonObject.getIntValue("code")==TencentVideoV2.SUCCESS){
                    JSONArray transcodeInfos = jsonObject.getJSONObject("transcodeInfo").getJSONArray("transcodeList");
                    if(CollectionUtils.isNotEmpty(transcodeInfos)){
                        JSONObject metaDataJson = new JSONObject();
                        for (Object obj : transcodeInfos) {
                            JSONObject trans = (JSONObject) obj;
                            int definition = trans.getIntValue("definition");
                            switch (definition) {
                                case 0:// 原画
                                    metaDataJson.put("origin", trans.getString("url"));
                                    break;
                                case 10:// 流畅
                                    metaDataJson.put("mp4", trans.getString("url"));
                                    break;
                                case 20:// 标清
                                    metaDataJson.put("mp4_sd", trans.getString("url"));
                                    break;
                                case 30:// 高清
                                    metaDataJson.put("mp4_hd", trans.getString("url"));
                                    break;
                                case 210:// 流畅
                                    metaDataJson.put("m3u8", trans.getString("url"));
                                    break;
                                case 220:// 标清
                                    metaDataJson.put("m3u8_sd", trans.getString("url"));
                                    break;
                                case 230:// 高清
                                    metaDataJson.put("m3u8_hd", trans.getString("url"));
                                    break;
                                default:// 其他
                                    metaDataJson.put("other_" + definition, trans.getString("url"));
                                    break;
                            }
                        }
                        // 更新metaData
                        VideoConfig refreshConfig = new VideoConfig();
                        refreshConfig.setMetaData(metaDataJson.toJSONString());
                        refreshConfig.setVideoId(videoId);
                        videoConfigMapper.updateByPrimaryKey(refreshConfig);
                        url = metaDataJson.getString(type);
                        if(StringUtils.isBlank(url)){
                            return new BaseResult<>(BaseConstant.FAILED, "当前编码格式不存在，请前往腾讯云查询全局设置");
                        }
                    } else {
                        return new BaseResult<>(BaseConstant.FAILED, "转码信息为空");
                    }
                } else {
                    return new BaseResult<>(BaseConstant.FAILED, "获取云视频失败：" + jsonObject.getString("message"));
                }
            }
            return new BaseResult<>(url);
        }
    }
}
