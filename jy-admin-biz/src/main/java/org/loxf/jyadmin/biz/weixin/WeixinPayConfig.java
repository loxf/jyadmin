package org.loxf.jyadmin.biz.weixin;

import com.github.wxpay.sdk.WXPayConfig;
import org.apache.http.ssl.SSLContexts;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.biz.util.ConfigUtil;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

public class WeixinPayConfig implements WXPayConfig {
    private static SSLContext sslContextWeixin;
    private String weixinPkcs8PublicFilePath ;
    /**
     * 环境：微信公众号WX 小程序XCX
     */
    private String env;
    public WeixinPayConfig(String env) throws Exception {
        if(!env.equals("WX")&&!env.equals("XCX")){
            throw new RuntimeException("微信支付控件初始化失败:" + env);
        }
        this.env = env;
        initCert();
    }

    public WeixinPayConfig()throws Exception {
        // 默认微信
        env = "WX";
        initCert();
    }

    public void initCert() throws Exception {
        String WEIXIN_APICLIENT_CERT = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "WEIXIN_APICLIENT_CERT").getConfigValue();
        File file = new File(WEIXIN_APICLIENT_CERT);
        InputStream certStream = new FileInputStream(file);
        this.certData = new byte[(int) file.length()];
        certStream.read(this.certData);
        certStream.close();
    }

    public static SSLContext queryWeixinSSL() throws Exception {
        if (sslContextWeixin == null) {
            String WX_MCHID = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "WX_MCHID").getConfigValue();
            String WEIXIN_APICLIENT_CERT = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "WEIXIN_APICLIENT_CERT").getConfigValue();
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            FileInputStream instream = new FileInputStream(new File(WEIXIN_APICLIENT_CERT));
            keyStore.load(instream, WX_MCHID.toCharArray());
            instream.close();
            // Trust own CA and all self-signed certs
            sslContextWeixin = SSLContexts.custom()
                    .loadKeyMaterial(keyStore, WX_MCHID.toCharArray())
                    .build();
        }
        return sslContextWeixin;
    }

    private byte[] certData;

    @Override
    public String getAppID() {
        if(env.equals("WX")){
            return ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "WX_APPID").getConfigValue();
        } else {
            return ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "WX_XCX_APPID").getConfigValue();
        }
    }

    @Override
    public String getMchID() {
        String WX_MCHID = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "WX_MCHID").getConfigValue();
        return WX_MCHID;
    }

    @Override
    public String getKey() {
        String WX_MCH_KEY = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "WX_MCH_KEY").getConfigValue();
        return WX_MCH_KEY;
    }

    @Override
    public InputStream getCertStream() {
        return null;
    }

    @Override
    public int getHttpConnectTimeoutMs() {
        return 0;
    }

    @Override
    public int getHttpReadTimeoutMs() {
        return 0;
    }

    public String getWeixinPkcs8PublicFilePath() {
        return  ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "WEIXIN_PKCS8_PUBLIC_FILE_PATH").getConfigValue();
    }

}
