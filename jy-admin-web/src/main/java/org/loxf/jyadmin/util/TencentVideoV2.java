package org.loxf.jyadmin.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.exception.BizException;
import org.loxf.jyadmin.base.util.HttpsUtil;
import org.loxf.jyadmin.base.util.IdGenerator;
import org.loxf.jyadmin.biz.util.ConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Encoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class TencentVideoV2 {
    private static Logger logger = LoggerFactory.getLogger(TencentVideoV2.class);

    private static final String HMAC_ALGORITHM = "HmacSHA1";
    private static String cloudApiUrl = "https://vod.api.qcloud.com/v2/index.php";
    private static int SUCCESS = 0;

    private static String DeleteVodFile = "DeleteVodFile";
    private static String ModifyVodInfo = "ModifyVodInfo";

    public static JSONObject delVideo(String fileId){
        HashMap params = getCommonParam("DeleteVodFile", "");
        params.put("fileId", fileId);
        params.put("priority", 0);
        params.put("isFlushCdn", 1);
        return dealGetUrl(params);
    }

    public static JSONObject modifyVideoInfo(String fileId, String fileName){
        HashMap params = getCommonParam("ModifyVodInfo", "");
        params.put("fileId", fileId);
        params.put("fileName", fileName);
        return dealGetUrl(params);
    }

    private static JSONObject dealGetUrl(HashMap params){
        try {
            String reqUrl = cloudApiUrl ;
            reqUrl += "?" + URLEncoder.encode(generateSign(params, true), "UTF-8");
            String result = HttpsUtil.doHttpsGet(reqUrl, null, null);
            logger.info("腾讯云视频请求链接：{}, 返回结果：{}", reqUrl, result);
            JSONObject jsonObject = JSON.parseObject(result);
            return jsonObject;
        } catch (Exception e) {
            logger.error("腾讯云视频处理失败", e);
            throw new BizException("腾讯云视频处理失败", e);
        }
    }

    public static String getSecretId(){
        return ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "TC_VIDEO_SECRET_ID").getConfigValue();
    }
    public static String getSecretKey(){
        return ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "TC_VIDEO_SECRET_KEY").getConfigValue();
    }

    private static String getSign(String contextStr) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException {
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        SecretKeySpec secretKeySpec = new SecretKeySpec(TencentVideoV2.getSecretKey().getBytes("UTF-8"), mac.getAlgorithm());
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(contextStr.getBytes("UTF-8"));
        byte[] sigBuf = byteMerger(hash, contextStr.getBytes("utf8"));
        String strSign = new String(new BASE64Encoder().encode(sigBuf).getBytes());
        strSign = strSign.replace(" ", "").replace("\n", "").replace("\r", "");
        return strSign;
    }

    /**
     * 构造云视频Sign
     *
     * @param params 参数
     * @param needParams 是否需要其他参数拼接？ true：需要  false：只要签名
     * @return string
     * @throws Exception
     */
    public static String generateSign(HashMap<Object, Object> params, boolean needParams) throws Exception {
        Object[] array = params.keySet().toArray();
        java.util.Arrays.sort(array);// 字典序
        String keyStr = "";
        for (int i = 0; i < array.length; i++) {
            String key = array[i].toString();
            String value = String.valueOf(params.get(key));// 1、“参数值” 为原始值而非url编码后的值。
            key = key.replaceAll("_", ".");// 2、若输入参数的Key中包含下划线，则需要将其转换为“.”
            // 然后将格式化后的各个参数用"&"拼接在一起
            if(StringUtils.isNotBlank(keyStr)){
                keyStr += "&" + key + "=" + value;
            } else {
                keyStr += key + "=" + value;
            }
        }
        if(!needParams) {
            return getSign(keyStr);
        } else {
            return keyStr += "&Signature=" + getSign(keyStr);
        }
    }

    /**
     * 构造云视频Sign
     *
     * @return string
     * @throws Exception
     */
    public static String generateSign(HashMap<Object, Object> params) throws Exception {
        return generateSign(params, false);
    }

    private static HashMap getCommonParam(String action, String region){
        HashMap params = new HashMap();
        params.put("Action", action);
        params.put("Region", region);
        params.put("SecretId", getSecretId());
        params.put("Timestamp", System.currentTimeMillis()/1000);
        params.put("Nonce", IdGenerator.generate("VOD"));
        return params;
    }

    static byte[] byteMerger(byte[] byte1, byte[] byte2) {
        byte[] byte3 = new byte[byte1.length + byte2.length];
        System.arraycopy(byte1, 0, byte3, 0, byte1.length);
        System.arraycopy(byte2, 0, byte3, byte1.length, byte2.length);
        return byte3;
    }

}
