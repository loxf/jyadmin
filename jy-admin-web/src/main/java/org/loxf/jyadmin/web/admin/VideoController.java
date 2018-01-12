package org.loxf.jyadmin.web.admin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.biz.util.ConfigUtil;
import org.loxf.jyadmin.client.dto.VideoConfigDto;
import org.loxf.jyadmin.client.service.VideoConfigService;
import org.loxf.jyadmin.biz.util.TencentVideoV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Random;

@Controller
@RequestMapping("/admin/video")
public class VideoController {
    private static Logger logger = LoggerFactory.getLogger(VideoController.class);

    @Autowired
    private VideoConfigService videoConfigService;

    @RequestMapping("/index")
    public String index(Model model){
        return "video/video_list";
    }

    @RequestMapping("/toAddVideo")
    public String toAddVideo(Model model){
        return "main/video/addVideo";
    }

    @RequestMapping("/list")
    @ResponseBody
    public PageResult<VideoConfigDto> list(VideoConfigDto videoConfigDto){
        PageResult<VideoConfigDto> pageResult = videoConfigService.pager(videoConfigDto);
        if(CollectionUtils.isNotEmpty(pageResult.getData())){
            for(VideoConfigDto video : pageResult.getData()) {
                String url = null;
                if (StringUtils.isNotBlank(video.getMetaData())) {
                    JSONObject jsonObject = JSON.parseObject(video.getMetaData());
                    url = jsonObject.getString("m3u8_hd");
                }
                if (StringUtils.isBlank(url)) {
                    BaseResult<String> hdUrlBaseResult = getUrl(video.getVideoId());// 获取高清地址，触发更新metaData
                    if(hdUrlBaseResult.getCode()==BaseConstant.SUCCESS) {
                        url = hdUrlBaseResult.getData();
                    } else {
                        url = hdUrlBaseResult.getMsg();
                    }
                }
                video.setVideoUrl(url);
            }
        }
        return pageResult;
    }

    @RequestMapping("/addVideo")
    @ResponseBody
    public BaseResult addVideo(HttpServletRequest request, String video_name, String token){
        VideoConfigDto dto = new VideoConfigDto();
        try {
            dto.setVideoName(URLDecoder.decode(video_name, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            return new BaseResult(BaseConstant.FAILED, "视频名称编码失败");
        }
        BaseResult baseResult = videoConfigService.addVideo(dto);
        if (baseResult.getCode() == BaseConstant.SUCCESS) {
            JSONObject result = new JSONObject();
            result.put("videoId", baseResult.getData());
            return new BaseResult(result);
        }
        return baseResult;
    }

    @RequestMapping("/getUploadSign")
    @ResponseBody
    public BaseResult<String> getUploadSign(){
        long signValidDuration = 2*60*60;
        long currentTime = System.currentTimeMillis()/1000;
        long endTime = (currentTime + signValidDuration);
        try {
            String contextStr = "secretId=" + java.net.URLEncoder.encode(TencentVideoV2.getSecretId(), "utf8");
            contextStr += "&currentTimeStamp=" + currentTime;
            contextStr += "&expireTime=" + endTime;
            contextStr += "&random=" + new Random().nextInt(java.lang.Integer.MAX_VALUE);
            contextStr += "&procedure=QCVB_SimpleProcessFile(1,0,10,10)";// 预置转码
            String sign = TencentVideoV2.getUploadSign(contextStr);
            return new BaseResult(sign);
        } catch (Exception e) {
            logger.error("获取视频上传签名失败", e);
            return new BaseResult(BaseConstant.FAILED, "获取视频上传签名失败：" + e.getMessage());
        }
    }

    @RequestMapping("/editVideo")
    @ResponseBody
    public BaseResult toAddVideo(String videoId, String videoOutId, String videoName){
        if(StringUtils.isBlank(videoId)||StringUtils.isBlank(videoName)||StringUtils.isBlank(videoOutId)){
            return new BaseResult(BaseConstant.FAILED, "必要参数缺失");
        }
        try {
            if(StringUtils.isNotBlank(videoOutId)) {
                JSONObject rep = TencentVideoV2.modifyVideoInfo(videoOutId, videoName);
                logger.info("更新云视频返回：{}", rep.toJSONString());
                if (rep.getIntValue("code") != 0) {
                    return new BaseResult(BaseConstant.FAILED, rep.getString("msg"));
                }
            }
            VideoConfigDto video = new VideoConfigDto();
            video.setVideoName(videoName);
            video.setVideoId(videoId);
            return videoConfigService.editVideo(video);
        } catch (Exception e){
            logger.error("更新视频信息失败", e);
            return new BaseResult(BaseConstant.FAILED,"更新视频失败");
        }
    }

    @RequestMapping("/delVideo")
    @ResponseBody
    public BaseResult delVideo(String videoId){
        VideoConfigDto dto = videoConfigService.queryVideo(videoId).getData();
        if(dto==null){
            return new BaseResult(BaseConstant.FAILED, "视频不存在");
        } else {
            try {
                String msg = BaseConstant.SUCCESS_MSG;
                // 删除云视频
                if(StringUtils.isNotBlank(dto.getVideoOutId())) {
                    JSONObject rep = TencentVideoV2.delVideo(dto.getVideoOutId());
                    logger.info("删除云视频返回：{}", rep.toJSONString());
                    if (rep.getIntValue("code") != 0) {
                        msg = "本地删除成功，云端删除失败：" + rep.getString("message");
                    }
                }
                BaseResult baseResult = videoConfigService.delVideo(videoId);
                baseResult.setMsg(msg);
                return baseResult;
            } catch (Exception e){
                logger.error("删除视频失败", e);
                return new BaseResult(BaseConstant.FAILED,"删除视频失败");
            }
        }
    }

    @RequestMapping("/updateProgress")
    @ResponseBody
    public BaseResult updateProgress(VideoConfigDto dto){
        return videoConfigService.updateProgress(dto);
    }

    @RequestMapping("/getUrl")
    @ResponseBody
    public BaseResult getUrl(String videoId){
        return videoConfigService.queryUrl(videoId, "m3u8_hd");
    }

    @RequestMapping("/playVideo")
    public String getUrl(Model model, String videoId){
        BaseResult<VideoConfigDto> videoConfigDtoBaseResult = videoConfigService.queryVideo(videoId);
        if(videoConfigDtoBaseResult.getCode()==BaseConstant.SUCCESS &&
                StringUtils.isNotBlank(videoConfigDtoBaseResult.getData().getVideoOutId())) {
            String appId = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "TC_VIDEO_APPID").getConfigValue();
            model.addAttribute("outVideoId", videoConfigDtoBaseResult.getData().getVideoOutId());
            model.addAttribute("appId", appId);
            return "main/video/playVideo";
        } else {
            return "main/error";
        }
    }

}
