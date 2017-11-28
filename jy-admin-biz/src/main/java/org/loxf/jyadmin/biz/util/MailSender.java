package org.loxf.jyadmin.biz.util;

import com.alibaba.druid.filter.config.ConfigTools;
import org.apache.velocity.app.VelocityEngine;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.util.SpringApplicationContextUtil;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by luohj on 2017/8/24.
 */
public class MailSender {
    private static MailSender mailSender ;
    //从配置文件中读取相应的邮件配置属性
    private String emailHost;
    private String userName;
    private String password;
    private String mailAuth = "true";
    private JavaMailSenderImpl instance = null;
    private VelocityEngine velocityEngine = null;

    public static void main(String[] args){
        /*try {
            String pw = ConfigTools.encrypt(privateKey,"111111");
            System.out.println(pw);
            pw = ConfigTools.decrypt("MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIHdgtEcTccvoTwDsDkYkyJ0f+hi5uFA3b1HoNiM5Si+KRWeYWI4JTR5G69FyMrke9eGd+38AAPJstUotoVv7PMCAwEAAQ==", pw);
            System.out.println(pw);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private MailSender(){
        emailHost = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "EMAIL_HOST").getConfigValue();
        userName = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "EMAIL_USERNAME").getConfigValue();
        password = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "EMAIL_PASSWORD").getConfigValue();
        getMailInstance();
        getVelocityEngineInstance();
    }

    public static MailSender getInstance(){
        if(mailSender==null){
            synchronized (MailSender.class){
                if(mailSender==null){
                    mailSender = new MailSender();
                }
            }
        }
        return mailSender;
    }

    private JavaMailSender getMailInstance() {
        instance = new JavaMailSenderImpl();
        instance.setHost(emailHost);
        instance.setPort(465);
        instance.setUsername(userName);
        instance.setPassword(password);
        Properties properties = new Properties();
        properties.setProperty("mail.host", emailHost);
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.auth", mailAuth);
        //使用gmail发送邮件是必须设置如下参数的 主要是port不一样
        properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.setProperty("mail.smtp.socketFactory.fallback", "false");
        properties.setProperty("mail.smtp.port", "465");
        properties.setProperty("mail.smtp.socketFactory.port", "465");
        instance.setJavaMailProperties(properties);
        return instance;
    }

    private VelocityEngine getVelocityEngineInstance() {
        Map<String, Object> proMap = new HashMap<String, Object>();
        proMap.put("resource.loader", "class");
        proMap.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        velocityEngine = new VelocityEngine();
        for (Map.Entry<String, Object> entry : proMap.entrySet()) {
            velocityEngine.setProperty(entry.getKey(), entry.getValue());
        }
        return velocityEngine;
    }

    /**
     * @param model 存放的是要替换模板中字段的值
     * @param subject 邮件主题
     * @param vmfile 模板文件
     * @param mailTo 接收方的email地址
     * @param files 附件
     */
    public void sendEmail(final Map<String,Object> model,final String subject,final String vmfile,
                                 final String[] mailTo,final String [] files)
    {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            //注意MimeMessagePreparator接口只有这一个回调函数
            public void prepare(MimeMessage mimeMessage) throws Exception
            {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage,true,"UTF-8");
                //这是一个生成Mime邮件简单工具，如果不使用GBK这个，中文会出现乱码
                //如果您使用的都是英文，那么可以使用MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setTo(mailTo);//设置接收方的email地址
                message.setSubject(subject);//设置邮件主题
                message.setFrom(userName);//设置发送方地址
                String text = VelocityEngineUtils.mergeTemplateIntoString(
                        velocityEngine, vmfile, "UTF-8", model);
                //从模板中加载要发送的内容，vmfile就是模板文件的名字
                //注意模板中有中文要加GBK，model中存放的是要替换模板中字段的值
                message.setText(text, true);
                //将发送的内容赋值给MimeMessageHelper,后面的true表示内容解析成html
                //如果您不想解析文本内容，可以使用false或者不添加这项
                if(files!=null) {
                    FileSystemResource file;
                    for (String s : files)//添加附件
                    {
                        file = new FileSystemResource(new File(s));//读取附件
                        message.addAttachment(s, file);//向email中添加附件
                    }
                }
            }
        };
        instance.send(preparator);//发送邮件
    }

}
