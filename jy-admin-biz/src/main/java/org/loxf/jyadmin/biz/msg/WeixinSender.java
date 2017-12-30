package org.loxf.jyadmin.biz.msg;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.util.JedisUtil;
import org.loxf.jyadmin.base.util.SpringApplicationContextUtil;
import org.loxf.jyadmin.base.util.weixin.WeixinUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeixinSender implements ISender {
    private static Logger logger = LoggerFactory.getLogger(WeixinSender.class);
    @Override
    public boolean send(Map params, String target) {
        String touser = (String)params.get("touser");
        if(StringUtils.isBlank(touser)||!touser.equals(target)){
            logger.error("发送参数错误：target={}, params={}", target, JSON.toJSONString(params));
            return false;
        }
        String accessToken = jedisUtil().get(BaseConstant.WX_ACCESS_TOKEN);
        if(StringUtils.isNotBlank(accessToken)) {
            try {
                String result = WeixinUtil.sendTemplateMsg(JSON.toJSONString(params), accessToken).getData();
                JSONObject resultJson = JSON.parseObject(result);
                if(resultJson.getIntValue("errcode")!=0){
                    logger.error("发送微信消息失败：result={}, params=", result, JSON.toJSONString(params));
                    return false;
                }
            } catch (Exception e) {
                logger.error("发送微信消息异常", e);
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * @param templateId 模板ID
     * @param openId 接收人
     * @param data 格式<String, Object>：{"User":{ "value":"黄先生", "color":"#173177"}}
     * @param detailUrl 详情地址，可以为空
     * @return
     */
    public static Map createWxMsgMap(String templateId, String openId, Map data, String detailUrl){
        Map result = new HashMap();
        result.put("touser", openId);
        result.put("template_id", templateId);
        result.put("url", detailUrl);
        result.put("topcolor", "#00868B");
        result.put("data", data);
        return result ;
    }

    public static Map createWXKeyWord(String value, String color){
        Map map = new HashMap();
        map.put("value", value);
        if(StringUtils.isNotBlank(color)){
            color = "#000000";
        }
        map.put("color", color);
        return map;
    }
    private JedisUtil jedisUtil(){
        return SpringApplicationContextUtil.getBean(JedisUtil.class);
    }
}
