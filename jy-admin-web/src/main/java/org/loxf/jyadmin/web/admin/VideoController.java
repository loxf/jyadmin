package org.loxf.jyadmin.web.admin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
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

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;

@Controller
@RequestMapping("/admin/video")
public class VideoController {
    private static Logger logger = LoggerFactory.getLogger(VideoController.class);

    public static String USER_UNIQUE = "tcznqlkk60";
    public static String SECRET_KEY = "bb8585d8a83cf41f6138e7764c916364";
    public static String PLAY_UNIQUE = "ffffffffff";
    public static LetvCloudV1 cloudInit = new LetvCloudV1(USER_UNIQUE, SECRET_KEY);

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
    public String addVideo(HttpServletRequest request, String video_name, Integer file_size, Integer uploadtype, String token){
        try {
            if (token == null) {
                //视频上传初始化（Web方式）
                //获取客户端的公网ip
                String client_ip = IPUtil.getIpAddr(request);
                video_name = URLDecoder.decode(video_name, "UTF-8");
                video_name = video_name.substring(0, video_name.lastIndexOf("."));
                String result = cloudInit.videoUploadInit(video_name, client_ip, file_size, uploadtype);
                logger.info("上传返回内容：{}", result);
                JSONObject resultJSON = JSON.parseObject(result);
                if (resultJSON.containsKey("code") && resultJSON.getIntValue("code") == 0) {
                    JSONObject json = JSON.parseObject(result);
                    JSONObject data = json.getJSONObject("data");
                    VideoConfigDto dto = new VideoConfigDto();
                    dto.setVideoName(video_name);
                    dto.setVideoOutId(data.getString("video_id"));
                    dto.setVideoUnique(data.getString("video_unique"));
                    // 下面的属性不需要
                    data.remove("upload_url");
                    data.remove("upload_https_url");
                    data.remove("token");
                    data.remove("progress_url");
                    dto.setMetaData(data.toJSONString());
                    BaseResult<String> baseResult = videoConfigService.addVideo(dto);
                    resultJSON.put("jy_video_id", baseResult.getData());
                }
                return resultJSON.toJSONString();
            } else {
                //视频上传续传
                String result = cloudInit.videoUploadResume(token, uploadtype);
                logger.info("上传返回内容：{}", result);
                return result;
            }
        } catch (Exception e){
            logger.error("新增视频失败：", e);
            return "{code:1, msg:'新增视频失败：" + e.getMessage() + "'}";
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
                String url = cloudInit.videoGetPlayinterface(USER_UNIQUE, videoConfigDtoBaseResult.getData().getVideoUnique(),
                        "URL", PLAY_UNIQUE, 1, 640, 360);
                url = url.replace("http:", "");
                return new BaseResult(url);
            } else {
                return new BaseResult(BaseConstant.FAILED, "视频未上传完成");
            }
        }
        return videoConfigDtoBaseResult;
    }



}
