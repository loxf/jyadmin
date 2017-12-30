package org.loxf.jyadmin.web.admin;

import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

@Controller
@RequestMapping("/upload")
public class FileUploadController {
    private static Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @Value("#{configProperties['IMAGE.SERVER.PATH']}")
    private String IMG_SERVER_PATH;

    @RequestMapping("/img")
    @ResponseBody
    public BaseResult uploadImg(MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        if (file.isEmpty()) {
            return new BaseResult(BaseConstant.FAILED, "上传文件为空");
        }
        //生成uuid作为文件名称
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        //获得文件类型（可以判断如果不是图片，禁止上传）
        String contentType = file.getContentType();
        String type = request.getParameter("type");
        if (StringUtils.isBlank(type)) {
            type = "COMMON";
        }
        //获得文件后缀名称
        String imageName = contentType.substring(contentType.indexOf("/") + 1);
        if (imageName.equalsIgnoreCase("jpg") || imageName.equalsIgnoreCase("jpeg") ||
                imageName.equalsIgnoreCase("png") ||
                imageName.equalsIgnoreCase("icon") || imageName.equalsIgnoreCase("bmp")) {
            String pathRoot = IMG_SERVER_PATH;// 绝对路径
            return deal(pathRoot, type, uuid + "." + imageName, file, request);
        } else {
            return new BaseResult(BaseConstant.FAILED, "上传格式非图片");
        }
    }

    public static BaseResult deal(String pathRoot, String type, String fileName, MultipartFile file, HttpServletRequest request) {
        try {
            String path = "";
            if (StringUtils.isBlank(pathRoot)) {
                //获得物理路径webapp所在路径
                pathRoot = request.getSession().getServletContext().getRealPath("");
                path = "/static/upload/" + type;
            } else {
                path = "/" + type;
            }
            if (!new File(pathRoot + File.separator + path).exists()) {
                new File(pathRoot + File.separator + path).mkdir();
            }
            // 文件地址
            String filePath = pathRoot + path + File.separator + fileName;
            file.transferTo(new File(filePath));
            String src = path + "/" + fileName;
            return new BaseResult(new HashMap() {{
                put("src", src);put("title", fileName);
            }});
        } catch (IOException e) {
            logger.error("上传失败", e);
            return new BaseResult(BaseConstant.FAILED, "上传失败");
        }
    }

    public static String dealHtml(String pathRoot, String path, String fileName, String html, HttpServletRequest request) {
        try {
            if (StringUtils.isBlank(pathRoot)) {
                //获得物理路径webapp所在路径
                pathRoot = request.getSession().getServletContext().getRealPath("");
            }
            if (!new File(pathRoot + "/static/upload/" + File.separator + path).exists()) {
                new File(pathRoot + "/static/upload/" + File.separator + path).mkdir();
            }
            // 文件地址
            String filePath = pathRoot + path + File.separator + fileName;
            FileOutputStream o = null;
            o = new FileOutputStream(filePath);
            o.write(html.getBytes("UTF-8"));
            o.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path + File.separator + fileName;
    }

}
