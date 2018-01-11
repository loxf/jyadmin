package org.loxf.jyadmin.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.exception.BizException;
import org.loxf.jyadmin.base.util.HttpsUtil;
import org.loxf.jyadmin.base.util.IdGenerator;
import org.loxf.jyadmin.base.util.encryption.Base64Util;
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
import java.util.Random;

public class TencentVideoV2 {
    private static Logger logger = LoggerFactory.getLogger(TencentVideoV2.class);

    private static final String HMAC_ALGORITHM = "HmacSHA1";
    private static String cloudApiUrl = "https://vod.api.qcloud.com/v2/index.php";
    private static String URL_PATH = "vod.api.qcloud.com/v2/index.php";
    private static int SUCCESS = 0;

    private static String DeleteVodFile = "DeleteVodFile";
    private static String ModifyVodInfo = "ModifyVodInfo";

    public static JSONObject delVideo(String fileId){
        HashMap params = getCommonParam("DeleteVodFile", "gz");
        params.put("fileId", fileId);
        params.put("priority", 0);
        params.put("isFlushCdn", 1);
        return dealGetUrl(params);
    }

    public static JSONObject modifyVideoInfo(String fileId, String fileName){
        HashMap params = getCommonParam("ModifyVodInfo", "gz");
        params.put("fileId", fileId);
        params.put("fileName", fileName);
        return dealGetUrl(params);
    }

    /**
     * @param fileId
     * @param infoFilters 备选项：basicInfo（基础信息）、元信息（metaData）、加密信息（drm）、transcodeInfo（转码结果信息）、imageSpriteInfo（雪碧图信息）、snapshotByTimeOffsetInfo（指定时间点截图信息）、sampleSnapshotInfo（采样截图信息）。
     * @return
     */
    public static JSONObject queryVideoInfo(String fileId, String[] infoFilters){
        HashMap params = getCommonParam("GetVideoInfo", "gz");
        params.put("fileId", fileId);
        if(infoFilters!=null&&infoFilters.length>0) {
            for(int i=0; i<infoFilters.length; i++) {
                params.put("infoFilter."+i, infoFilters[i]);
            }
        }
        return dealGetUrl(params);
    }

    private static JSONObject dealGetUrl(HashMap params){
        try {
            String reqUrl = cloudApiUrl ;
            reqUrl += "?" + generateSign(params, "GET", true);
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

    public static String getUploadSign(String contextStr) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException {
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        SecretKeySpec secretKeySpec = new SecretKeySpec(TencentVideoV2.getSecretKey().getBytes("UTF-8"), mac.getAlgorithm());
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(contextStr.getBytes("UTF-8"));
        byte[] sigBuf = byteMerger(hash, contextStr.getBytes("utf8"));
        String strSign = Base64Util.encode(sigBuf);
        // strSign = strSign.replace(" ", "").replace("\n", "").replace("\r", "");
        return strSign;
    }

    public static String getSign(String contextStr) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException {
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        SecretKeySpec secretKeySpec = new SecretKeySpec(TencentVideoV2.getSecretKey().getBytes("UTF-8"), mac.getAlgorithm());
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(contextStr.getBytes("UTF-8"));
        String strSign = Base64Util.encode(hash);
        return strSign;
    }

    /**
     * 构造云视频Sign
     *
     * @param params 参数
     * @param method POST/GET
     * @param needParams 是否需要其他参数拼接？ true：需要  false：只要签名
     * @return string
     * @throws Exception
     */
    public static String generateSign(HashMap<Object, Object> params, String method, boolean needParams) throws Exception {
        Object[] array = params.keySet().toArray();
        java.util.Arrays.sort(array);// 字典序
        String keyStr = "";
        String keyStrEncode = "";
        for (int i = 0; i < array.length; i++) {
            String key = array[i].toString();
            String value = String.valueOf(params.get(key));// 1、“参数值” 为原始值而非url编码后的值。
            key = key.replaceAll("_", ".");// 2、若输入参数的Key中包含下划线，则需要将其转换为“.”
            // 然后将格式化后的各个参数用"&"拼接在一起
            if(StringUtils.isNotBlank(keyStr)){
                keyStr += "&" + key + "=" + value;
                keyStrEncode += "&" + key + "=" + URLEncoder.encode(value, "utf-8");
            } else {
                keyStr += key + "=" + value;
                keyStrEncode += "&" + key + "=" + URLEncoder.encode(value, "utf-8");
            }
        }
        /*签名原文串的拼接规则为:
        请求方法 + 请求主机 +请求路径 + ? + 请求字符串*/
        String origin = method + URL_PATH + "?" + keyStr;
        String sign = getSign(origin);
        if(!needParams) {
            return sign;
        } else {
            // 生成的签名串并不能直接作为请求参数，需要对其进行 URL 编码。
            // 如果用户的请求方法是GET，则对所有请求参数的参数值均需要做URL编码；此外，部分语言库会自动对URL进行编码，重复编码会导致签名校验失败。
            return keyStrEncode + "&Signature=" + URLEncoder.encode(sign, "utf-8");
        }
    }

    /**
     * 构造云视频Sign
     *
     * @return string
     * @throws Exception
     */
    public static String generateSign(HashMap<Object, Object> params) throws Exception {
        return generateSign(params, "GET", false);
    }

    private static HashMap getCommonParam(String action, String region){
        HashMap params = new HashMap();
        params.put("Action", action);
        params.put("Region", region);
        params.put("SecretId", getSecretId());
        params.put("Timestamp", System.currentTimeMillis()/1000);
        params.put("Nonce", new Random().nextInt(java.lang.Integer.MAX_VALUE));
        return params;
    }

    static byte[] byteMerger(byte[] byte1, byte[] byte2) {
        byte[] byte3 = new byte[byte1.length + byte2.length];
        System.arraycopy(byte1, 0, byte3, 0, byte1.length);
        System.arraycopy(byte2, 0, byte3, byte1.length, byte2.length);
        return byte3;
    }

}
