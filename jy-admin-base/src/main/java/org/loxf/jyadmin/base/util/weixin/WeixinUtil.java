package org.loxf.jyadmin.base.util.weixin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.util.HttpUtil;
import org.loxf.jyadmin.base.util.HttpsUtil;
import org.loxf.jyadmin.base.util.weixin.bean.AccessToken;
import org.loxf.jyadmin.base.util.weixin.bean.UserAccessToken;
import org.loxf.jyadmin.base.util.weixin.bean.WXUserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class WeixinUtil {
    private static Logger logger = LoggerFactory.getLogger(WeixinUtil.class);
    public static void main(String [] args){
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=wxf45ae0ff3e1bd94e&secret=8a51e801fcc89fc9aab671737560b4ed&code=081ayVm61J7B3S1A23p61t2Bm61ayVmN&grant_type=authorization_code";
        //System.out.println(url);
        System.out.println(commonGet(url));
    }
    public static AccessToken queryAccessToken(){
        return (AccessToken)commonGet(String.format(BaseConstant.ACCESS_TOKEN_URL, BaseConstant.WX_APPID, BaseConstant.WX_APPSECRET), AccessToken.class);
    }

    public static String getLoginUrl(String url, String code){
        String redirectUrl = String.format(BaseConstant.LOGIN_URL, url);
        try {
            redirectUrl = URLEncoder.encode(redirectUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("Url decode failed." , e);
        }
        return String.format(BaseConstant.USER_AUTH_CODE_URL, BaseConstant.WX_APPID, redirectUrl, code);
    }

    public static String validUserAccessToken(String accessToken, String openid){
        return commonGet(String.format(BaseConstant.CHECK_USER_ACCESS_TOKEN_URL, accessToken, openid));
    }

    public static UserAccessToken queryUserAccessToken(String code){
        return (UserAccessToken)commonGet(String.format(BaseConstant.QUERY_USER_TOKEN_BY_CODE_URL, BaseConstant.WX_APPID, BaseConstant.WX_APPSECRET, code), UserAccessToken.class);
    }

    public static UserAccessToken refreshUserAccessToken(String refreshToken){
        return (UserAccessToken)commonGet(String.format(BaseConstant.REFRESH_USER_TOKEN_BY_CODE_URL, BaseConstant.WX_APPID, refreshToken), UserAccessToken.class);
    }

    public static WXUserInfo queryUserInfo(String access_token, String openId){
        return (WXUserInfo)commonGet(String.format(BaseConstant.USER_INFO_URL, access_token, openId), WXUserInfo.class);
    }

    private static String commonGet(String url){
        try {
            String result = HttpsUtil.doHttpsGet(url, null, null);
            if(result.indexOf("errcode")>-1) {
                JSONObject json = JSON.parseObject(result);
                if(json.getIntValue("errcode")==0){
                    return result;
                } else {
                    logger.error("微信接口返回错误: {}", result);
                }
            } else {
                return result;
            }
        } catch (Exception e) {
            logger.error("微信接口异常", e);
        }
        return null;
    }

    private static Object commonGet(String url, Class clazz){
        try {
            String result = HttpUtil.handleGet(url);
            if(result.indexOf("errcode")>-1) {
                logger.error("微信接口返回错误: {}", result);
            } else {
                return JSON.parseObject(result, clazz);
            }
        } catch (Exception e) {
            logger.error("微信接口异常", e);
        }
        return null;
    }
}
