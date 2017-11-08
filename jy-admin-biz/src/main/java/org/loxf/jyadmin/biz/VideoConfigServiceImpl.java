package org.loxf.jyadmin.biz;

import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.util.IdGenerator;
import org.loxf.jyadmin.base.constant.BaseConstant;
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
        if(StringUtils.isBlank(dto.getVideoName())||StringUtils.isBlank(dto.getVideoOutId())){
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
            for(VideoConfig tmp : list){
                VideoConfigDto dto = new VideoConfigDto();
                BeanUtils.copyProperties(tmp, dto);
                ret.add(dto);
            }
        }
        int tatalPage = total/catalogDto.getPager().getSize() + (total%catalogDto.getPager().getSize()==0?0:1);
        return new PageResult<>(tatalPage, catalogDto.getPager().getPage(), total, ret);
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
}
