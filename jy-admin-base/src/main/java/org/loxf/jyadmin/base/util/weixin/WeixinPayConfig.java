package org.loxf.jyadmin.base.util.weixin;

import com.github.wxpay.sdk.WXPayConfig;
import org.loxf.jyadmin.base.constant.BaseConstant;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class WeixinPayConfig implements WXPayConfig {
    public WeixinPayConfig() throws Exception {
        /*String certPath = "/path/to/apiclient_cert.p12";
        File file = new File(certPath);
        InputStream certStream = new FileInputStream(file);
        this.certData = new byte[(int) file.length()];
        certStream.read(this.certData);
        certStream.close();*/
    }

    private byte[] certData;

    @Override
    public String getAppID() {
        return BaseConstant.WX_APPID;
    }

    @Override
    public String getMchID() {
        return null;
    }

    @Override
    public String getKey() {
        return BaseConstant.WX_EncodingAESKey;
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
