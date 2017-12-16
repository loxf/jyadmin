package org.loxf.jyadmin.base.util.weixin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConfig;
import com.github.wxpay.sdk.WXPayUtil;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.util.HttpUtil;
import org.loxf.jyadmin.base.util.HttpsUtil;
import org.loxf.jyadmin.base.util.weixin.bean.AccessToken;
import org.loxf.jyadmin.base.util.weixin.bean.JsTicket;
import org.loxf.jyadmin.base.util.weixin.bean.UserAccessToken;
import org.loxf.jyadmin.base.util.weixin.bean.WXUserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WeixinUtil {
    private static Logger logger = LoggerFactory.getLogger(WeixinUtil.class);
    public static void main(String [] args){
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=wxf45ae0ff3e1bd94e&secret=8a51e801fcc89fc9aab671737560b4ed&code=081ayVm61J7B3S1A23p61t2Bm61ayVmN&grant_type=authorization_code";
        //System.out.println(url);
        System.out.println(commonGet(url));
    }
    public static AccessToken queryAccessToken(String appId, String wxAppSecret){
        return (AccessToken)commonGet(String.format(BaseConstant.ACCESS_TOKEN_URL, appId, wxAppSecret), AccessToken.class);
    }

    public static String getLoginUrl(String appId, String url, String code){
        String redirectUrl = "";
        try {
            redirectUrl = String.format(BaseConstant.LOGIN_URL, URLEncoder.encode(url, "UTF-8"));
            redirectUrl = URLEncoder.encode(redirectUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("Url decode failed." , e);
        }
        return String.format(BaseConstant.USER_AUTH_CODE_URL, appId, redirectUrl, code);
    }

    public static String validUserAccessToken(String accessToken, String openid){
        return commonGet(String.format(BaseConstant.CHECK_USER_ACCESS_TOKEN_URL, accessToken, openid));
    }

    public static UserAccessToken queryUserAccessToken(String appId, String wxAppSecret, String code){
        return (UserAccessToken)commonGet(String.format(BaseConstant.QUERY_USER_TOKEN_BY_CODE_URL, appId, wxAppSecret, code), UserAccessToken.class);
    }

    public static UserAccessToken refreshUserAccessToken(String appId, String refreshToken){
        return (UserAccessToken)commonGet(String.format(BaseConstant.REFRESH_USER_TOKEN_BY_CODE_URL, appId, refreshToken), UserAccessToken.class);
    }

    public static WXUserInfo queryUserInfo(String access_token, String openId){
        return (WXUserInfo)commonGet(String.format(BaseConstant.USER_INFO_URL, access_token, openId), WXUserInfo.class);
    }

    public static JsTicket queryJsTicket(String access_token){
        return (JsTicket)commonGet(String.format(BaseConstant.JS_TICKET_URL, access_token), JsTicket.class);
    }

    public static Map<String, String> signJsTicket(String jsapi_ticket, String url) {
        Map<String, String> ret = new HashMap<String, String>();
        String nonce_str = create_nonce_str();
        String timestamp = System.currentTimeMillis() + "";
        String string1;
        String signature = "";

        //注意这里参数名必须全部小写，且必须有序
        string1 = "jsapi_ticket=" + jsapi_ticket +
                "&noncestr=" + nonce_str +
                "&timestamp=" + timestamp +
                "&url=" + url;
        // System.out.println(string1);
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(string1.getBytes("UTF-8"));
            signature = byteToHex(crypt.digest());
        } catch (NoSuchAlgorithmException e) {
            logger.error("js_ticket sign failed", e);
        } catch (UnsupportedEncodingException e) {
            logger.error("js_ticket sign failed", e);
        }
        ret.put("url", url);
        ret.put("jsapiTicket", jsapi_ticket);
        ret.put("nonceStr", nonce_str);
        ret.put("timestamp", timestamp);
        ret.put("signature", signature);
        return ret;
    }

    public static String create_nonce_str() {
        return UUID.randomUUID().toString();
    }
    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
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
                JSONObject json = JSON.parseObject(result);
                if(json.getIntValue("errcode")==0){
                    return JSON.parseObject(result, clazz);
                } else {
                    logger.error("微信接口返回错误: {}", result);
                }
            } else {
                return JSON.parseObject(result, clazz);
            }
        } catch (Exception e) {
            logger.error("微信接口异常", e);
        }
        return null;
    }

}
