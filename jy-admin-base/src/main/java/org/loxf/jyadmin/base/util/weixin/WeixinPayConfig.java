package org.loxf.jyadmin.base.util.weixin;

import com.github.wxpay.sdk.WXPayConfig;
import org.apache.http.ssl.SSLContexts;
import org.loxf.jyadmin.base.constant.BaseConstant;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

public class WeixinPayConfig implements WXPayConfig {
    private static SSLContext sslContextWeixin;

    public WeixinPayConfig() throws Exception {
        initCert();
    }

    public void initCert() throws Exception {
        String certPath = "/home/jingyiweb/weixin_cert/apiclient_cert.p12";
        File file = new File(certPath);
        InputStream certStream = new FileInputStream(file);
        this.certData = new byte[(int) file.length()];
        certStream.read(this.certData);
        certStream.close();
    }

    public static SSLContext queryWeixinSSL() throws Exception {
        if(sslContextWeixin==null) {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            String certFilePath ="/home/jingyiweb/weixin_cert/apiclient_cert.p12";
            FileInputStream instream = new FileInputStream(new File(certFilePath));
            keyStore.load(instream, BaseConstant.WX_MCHID.toCharArray());
            instream.close();
            // Trust own CA and all self-signed certs
            sslContextWeixin = SSLContexts.custom()
                    .loadKeyMaterial(keyStore, BaseConstant.WX_MCHID.toCharArray())
                    .build();
        }
        return sslContextWeixin;
    }

    private byte[] certData;

    @Override
    public String getAppID() {
        return BaseConstant.WX_APPID;
    }

    @Override
    public String getMchID() {
        return BaseConstant.WX_MCHID;
    }

    @Override
    public String getKey() {
        return BaseConstant.WX_MCH_KEY;
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
}
