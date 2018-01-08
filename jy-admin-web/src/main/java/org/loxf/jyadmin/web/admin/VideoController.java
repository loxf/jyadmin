package org.loxf.jyadmin.web.admin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import java.util.UUID;

@Controller
@RequestMapping("/admin/video")
public class VideoController {
    private static Logger logger = LoggerFactory.getLogger(VideoController.class);

    private static final String HMAC_ALGORITHM = "HmacSHA1";

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
        String sign = getUploadSignature();
        BaseResult baseResult = videoConfigService.addVideo(dto);
        if(baseResult.getCode()==BaseConstant.SUCCESS) {
            JSONObject result = new JSONObject();
            result.put("videoId", baseResult.getData());
            result.put("sign", sign);
            return new BaseResult(result);
        }
        return baseResult;
    }

    String getUploadSignature() {
        String strSign = "";
        long signValidDuration = 2*60*60;
        long currentTime = System.currentTimeMillis()/1000;
        long endTime = (currentTime + signValidDuration);
        String secretId = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "TC_VIDEO_SECRET_ID").getConfigValue();
        String secretKey = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "TC_VIDEO_SECRET_KEY").getConfigValue();

        try {
            String contextStr = "";
            contextStr += "secretId=" + java.net.URLEncoder.encode(secretId, "UTF-8");
            contextStr += "&currentTimeStamp=" + currentTime;
            contextStr += "&expireTime=" + endTime;
            contextStr += "&random=" + IdGenerator.generate("VD");
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes("UTF-8"), mac.getAlgorithm());
            mac.init(secretKeySpec);

            byte[] hash = mac.doFinal(contextStr.getBytes("UTF-8"));
            byte[] sigBuf = byteMerger(hash, contextStr.getBytes("utf8"));
            strSign = new String(new BASE64Encoder().encode(sigBuf).getBytes());
            strSign = strSign.replace(" ", "").replace("\n", "").replace("\r", "");
        } catch (Exception e) {
            logger.error("获取视频上传签名失败", e);
        }
        return strSign;
    }

    static byte[] byteMerger(byte[] byte1, byte[] byte2) {
        byte[] byte3 = new byte[byte1.length + byte2.length];
        System.arraycopy(byte1, 0, byte3, 0, byte1.length);
        System.arraycopy(byte2, 0, byte3, byte1.length, byte2.length);
        return byte3;
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

    String getCommonParam(String action, String secretId, String region, String timestamp, String nonce, String sign){
        String url = "Action=%s&SecretId=%s&Region=%s&Timestamp=%s&Nonce=%s&Signature=%s";
        return String.format(url, action, secretId, region, timestamp, nonce, sign);
    }
}
