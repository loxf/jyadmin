package org.loxf.jyadmin.web.admin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.util.HttpsUtil;
import org.loxf.jyadmin.base.util.JedisUtil;
import org.loxf.jyadmin.client.dto.WeixinButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin/wxmenu")
public class WeixinMenuController {
    private static Logger logger = LoggerFactory.getLogger(WeixinMenuController.class);
    @Autowired
    private JedisUtil jedisUtil;

    @RequestMapping("/create")
    @ResponseBody
    public BaseResult create(String weixinButton, HttpServletRequest request){
        String access_token = jedisUtil.get(BaseConstant.WX_ACCESS_TOKEN);
        if(StringUtils.isNotBlank(access_token)) {
            String url = String.format(BaseConstant.WEIXIN_MENU_CREATE, access_token);
            try {
                String result = HttpsUtil.handlePost(url, weixinButton, null);
                logger.info(result);
                return new BaseResult(result);
            } catch (Exception e) {
                logger.error("新增微信菜单失败", e);
                return new BaseResult(BaseConstant.FAILED, "新增微信菜单失败");
            }
        } else {
            return new BaseResult(BaseConstant.FAILED, "access_token为空");
        }
    }

    @RequestMapping("/delete")
    @ResponseBody
    public BaseResult delete(){
        String access_token = jedisUtil.get(BaseConstant.WX_ACCESS_TOKEN);
        if(StringUtils.isNotBlank(access_token)) {
            String url = String.format(BaseConstant.WEIXIN_MENU_DEL, access_token);
            try {
                String result = HttpsUtil.doHttpsGet(url, null, null);
                logger.info(result);
                return new BaseResult(result);
            } catch (Exception e) {
                logger.error("删除微信菜单失败", e);
                return new BaseResult(BaseConstant.FAILED, "删除微信菜单失败");
            }
        } else {
            return new BaseResult(BaseConstant.FAILED, "access_token为空");
        }
    }

    @RequestMapping("/get")
    @ResponseBody
    public BaseResult get(){
        String access_token = jedisUtil.get(BaseConstant.WX_ACCESS_TOKEN);
        if(StringUtils.isNotBlank(access_token)) {
            String url = String.format(BaseConstant.WEIXIN_MENU_GET, access_token);
            try {
                String result = HttpsUtil.doHttpsGet(url, null, null);
                logger.info(result);
                return new BaseResult(result);
            } catch (Exception e) {
                logger.error("自定义菜单查询失败", e);
                return new BaseResult(BaseConstant.FAILED, "自定义菜单查询失败");
            }
        } else {
            return new BaseResult(BaseConstant.FAILED, "access_token为空");
        }
    }

    @RequestMapping("/selfmenu")
    @ResponseBody
    public BaseResult selfmenu(){
        String access_token = jedisUtil.get(BaseConstant.WX_ACCESS_TOKEN);
        if(StringUtils.isNotBlank(access_token)) {
            String url = String.format(BaseConstant.WEIXIN_MENU_SELFMENU, access_token);
            try {
                String result = HttpsUtil.doHttpsGet(url, null, null);
                logger.info(result);
                return new BaseResult(result);
            } catch (Exception e) {
                logger.error("获取自定义菜单配置失败", e);
                return new BaseResult(BaseConstant.FAILED, "获取自定义菜单配置失败");
            }
        } else {
            return new BaseResult(BaseConstant.FAILED, "access_token为空");
        }
    }
}
