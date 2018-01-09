package org.loxf.jyadmin.web.admin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qcloud.vod.VodApi;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.util.IdGenerator;
import org.loxf.jyadmin.biz.util.ConfigUtil;
import org.loxf.jyadmin.client.dto.VideoConfigDto;
import org.loxf.jyadmin.client.service.VideoConfigService;
import org.loxf.jyadmin.util.IPUtil;
import org.loxf.jyadmin.util.LetvCloudV1;
import org.loxf.jyadmin.util.TencentVideoV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.misc.BASE64Encoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
        return videoConfigService.pager(videoConfigDto);
    }

    @RequestMapping("/addVideo")
    @ResponseBody
    public BaseResult addVideo(HttpServletRequest request, String video_name, String token){
        VideoConfigDto dto = new VideoConfigDto();
        dto.setVideoName(video_name);
        // 获取签名
        BaseResult signBaseResult = getUploadSignature();
        if(signBaseResult.getCode()==BaseConstant.SUCCESS) {
            BaseResult baseResult = videoConfigService.addVideo(dto);
            if (baseResult.getCode() == BaseConstant.SUCCESS) {
                JSONObject result = new JSONObject();
                result.put("videoId", baseResult.getData());
                result.put("sign", signBaseResult.getData());
                return new BaseResult(result);
            }
            return baseResult;
        } else {
            return signBaseResult;
        }
    }

    private BaseResult getUploadSignature(){
        long signValidDuration = 2*60*60;
        long currentTime = System.currentTimeMillis()/1000;
        long endTime = (currentTime + signValidDuration);
        try {
            String contextStr = "";
            HashMap params = new HashMap<>();
            params.put("secretId", TencentVideoV2.getSecretId());
            params.put("currentTimeStamp", currentTime);
            params.put("expireTime", endTime);
            params.put("random", IdGenerator.generate("VD"));
            return new BaseResult(TencentVideoV2.generateSign(params));
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
            String response = cloudInit.videoUpdate(Integer.valueOf(videoOutId), videoName);
            logger.info("更新乐视视频返回：{}", response);
            JSONObject rep = JSON.parseObject(response);
            if(rep.getIntValue("code")==0) {
                VideoConfigDto video = new VideoConfigDto();
                video.setVideoName(videoName);
                video.setVideoId(videoId);
                return videoConfigService.editVideo(video);
            } else {
                return new BaseResult(BaseConstant.FAILED, rep.getString("msg"));
            }
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
                // 删除云视频
                String response = cloudInit.videoDel(Integer.valueOf(dto.getVideoOutId()));
                logger.info("删除乐视视频返回：{}", response);
                JSONObject rep = JSON.parseObject(response);
                if(rep.getIntValue("code")==0) {
                    return videoConfigService.delVideo(videoId);
                } else {
                    BaseResult baseResult = videoConfigService.delVideo(videoId);
                    if(baseResult.getCode()==BaseConstant.SUCCESS){
                        baseResult.setMsg("本地删除成功，乐视端删除失败：" + rep.getString("message"));
                    }
                    return baseResult;
                }
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
    public BaseResult getUrl(HttpServletRequest request, String videoId){
        BaseResult<VideoConfigDto> videoConfigDtoBaseResult = videoConfigService.queryVideo(videoId);
        if(videoConfigDtoBaseResult.getCode()== BaseConstant.SUCCESS) {
            if(videoConfigDtoBaseResult.getData().getStatus()==2) {
                if(StringUtils.isNotBlank(videoConfigDtoBaseResult.getData().getVideoUrl())){
                    return new BaseResult(videoConfigDtoBaseResult.getData().getVideoUrl());
                }
                return new BaseResult(BaseConstant.FAILED, "未获取视频链接");
            } else {
                return new BaseResult(BaseConstant.FAILED, "视频未上传完成");
            }
        }
        return videoConfigDtoBaseResult;
    }

}
