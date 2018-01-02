package org.loxf.jyadmin.base.util.encryption;

import com.alibaba.druid.filter.config.ConfigTools;

/**
 */
public class EncryptionTool {
    private static final String DEFAULT_PRIVATE_KEY_STRING = "MIIBVgIBADANBgkqhkiG9w0BAQEFAASCAUAwggE8AgEAAkEAgd2C0RxNxy+hPAOwORiTInR/6GLm4UDdvUeg2IzlKL4pFZ5hYjglNHkbr0XIyuR714Z37fwAA8my1Si2hW/s8wIDAQABAkBEWIrveKDqcoO9rQdClU9iM8PZTqvJ4rSQWDErpsM39FOjqJuB+Qp7v1Zui+CNhUtXwYDdJBandZxx7uIR3ss5AiEAyNUsQH9P+wkEntoCHvaZn2Rb/hUULXi4Fc9q8fMiQx8CIQClidAMTsFUCF0z6RoEPIgDjL56XLACQbSe+lS0yy5PrQIhALT441d7YeckyTUb3q1oKianmP3soLIZBkkhD43XOhihAiEAjcwxBHnprNvFSpQgDwTy1WeYOiRvOF+kIpH4QS3SK7kCIQCBzEQkfpdraqRv7yybTe/4oj5QMh+J/vuSGUHMYxlMGw==";
    public static final String DEFAULT_PUBLIC_KEY_STRING = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIHdgtEcTccvoTwDsDkYkyJ0f+hi5uFA3b1HoNiM5Si+KRWeYWI4JTR5G69FyMrke9eGd+38AAPJstUotoVv7PMCAwEAAQ==";

    public static void main(String[] args){
        try {
          //  System.out.println(EncryptionKeyUtil.encryption("jdbc:mysql://rr-bp1j4qg613l29n902.mysql.rds.aliyuncs.com:3306/daichong?useUnicode=true&amp;characterEncoding=UTF-8"));
            String plainText = "jiingyi123..";
            System.out.println("开始加密："+plainText);
            String enTxt = ConfigTools.encrypt(DEFAULT_PRIVATE_KEY_STRING,plainText);
            System.out.println("加密结果："+enTxt);
            System.out.println("开始解密");
            System.out.println("解密结果："+ConfigTools.decrypt(DEFAULT_PUBLIC_KEY_STRING,enTxt));
        }catch (Exception e){

        }
    }
}
