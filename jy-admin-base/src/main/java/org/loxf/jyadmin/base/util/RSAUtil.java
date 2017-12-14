package org.loxf.jyadmin.base.util;

import org.bouncycastle.util.encoders.Base64;
import org.loxf.jyadmin.base.util.encryption.Base64Util;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;

public class RSAUtil {
    // TODO 文件路径
    // public static final String PKCS8_PUBLIC_FILE_PATH = "/home/jingyiweb/weixin_cert/weixin.pkcs8.public.pem";
    public static final String PKCS8_PUBLIC_FILE_PATH = "C:\\Users\\lenovo\\Desktop\\weixin.pkcs8.public.pem";

    // 非对称加密密钥算法
    public static final String KEY_ALGORITHM = "RSA";

    public static final String KEY_PADDING = "RSA/ECB/OAEPWITHSHA-1ANDMGF1PADDING";

    public static void main(String[] args) throws Exception {
        String bankNo = "测试人员";
        byte[] encoder = encryptByPublicKey(bankNo.getBytes());

        System.out.println(new String(Base64.encode(encoder)));
    }


    public static byte[] encryptByPublicKey(byte[] data) throws Exception {
        try {
            BufferedReader br = new BufferedReader(new FileReader(PKCS8_PUBLIC_FILE_PATH));
            String readLine = null;
            StringBuilder sb = new StringBuilder();
            readLine = br.readLine();
            while (readLine != null) {
                if(readLine.charAt(0)!='-') {
                    sb.append(readLine);
                }
                readLine = br.readLine();
            }
            br.close();

            byte[] keyBytes = Base64.decode(sb.toString());
            return encryptByPublicKey(data, keyBytes);
        } catch (IOException e) {
            throw new Exception("公钥数据流读取错误");
        } catch (NullPointerException e) {
            throw new Exception("公钥输入流为空");
        }
    }

    public static byte[] encryptByPublicKey(byte[] data, byte[] keyBytes) throws Exception {
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicK = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(KEY_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        return cipher.doFinal(data);
    }

}
