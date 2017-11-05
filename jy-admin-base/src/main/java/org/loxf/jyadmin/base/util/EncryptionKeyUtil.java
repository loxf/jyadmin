package org.loxf.jyadmin.base.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.SecureRandom;

public class EncryptionKeyUtil {
    private static String password = "LOXFAESJYYXG!@#";

    //Cipher负责完成加密或解密工作
    public static Cipher encryptCipher;
    public static Cipher decryptCipher;
    //SecretKey负责保存对称密钥
    public static SecretKey deskey;

    /**
     * 初始化Cipher类
     *
     * @throws Exception
     */
    private static synchronized void initCipher() throws Exception {
        deskey = getSecretKey();
        if (null == encryptCipher) {
            encryptCipher = Cipher.getInstance("AES"); //生成Cipher对象，指定其支持AES算法
            encryptCipher.init(Cipher.ENCRYPT_MODE, deskey); //根据密钥，对Cipher对象进行初始化,ENCRYPT_MODE表示加密模式
        }
        if (null == decryptCipher) {
            decryptCipher = Cipher.getInstance("AES"); //生成Cipher对象，指定其支持AES算法
            decryptCipher.init(Cipher.DECRYPT_MODE, deskey); //根据密钥，对Cipher对象进行初始化,ENCRYPT_MODE表示加密模式
        }
    }

    public static synchronized SecretKey getSecretKey() throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128, new SecureRandom(password.getBytes()));
        SecretKey secretKey = kgen.generateKey();
        return secretKey;
    }

    //加密
    public static String encryption(String msg) throws Exception {
        initCipher();
        String src = parseByte2HexStr(msg.getBytes("utf-8"));
        //加密，结果保存进enc
        byte[] enc = encryptCipher.doFinal(parseHexStr2Byte(src));
        return parseByte2HexStr(enc);
    }

    //解密
    public static String decryption(String msg) throws Exception {
        initCipher();
        //解密，结果保存进dec
        byte[] src = parseHexStr2Byte(msg);
        byte[] dec = decryptCipher.doFinal(src);
        return new String(dec);
    }

    //生成密钥
    public static void main(String[] args) {
        String msg = "asdlfkjaslkjglaskjgsfljglasf";
        try {
            String src = encryption(msg);
            System.out.println("加密后" + src);
            String tot = decryption(src);
            System.out.println("解密后" + tot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }
}
