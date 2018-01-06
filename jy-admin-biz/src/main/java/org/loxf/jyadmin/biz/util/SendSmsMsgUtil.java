package org.loxf.jyadmin.biz.util;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;

import org.loxf.jyadmin.base.exception.BizException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by hutingting on 2017/7/20.
 */
public class SendSmsMsgUtil {
    static Logger logger = LoggerFactory.getLogger(SendSmsMsgUtil.class);

    static final String SETPAYPASSWORD = "SMS_114070317";// 设置支付密码模板
    static final String BINDUSER = "SMS_114065333";// 绑定用户模板
    static final String signName = "静怡雅学馆";// 短信签名

    public static boolean sendMsgByType(Integer codeType, String code, String phoneNumbers) {
        String templateParam;
        switch (codeType) {
            case 1://设置支付密码
                templateParam = "{\"code\":\"" + code + "\"}";
                if (sendMsg(phoneNumbers, SETPAYPASSWORD, templateParam)) {
                    return true;
                }
                break;
            case 2://绑定用户
                templateParam = "{\"code\":\"" + code + "\"}";
                if (sendMsg(phoneNumbers, BINDUSER, templateParam)) {
                    return true;
                }
                break;
            default:
                break;
        }
        return false;
    }

    private static boolean sendMsg(String phoneNumbers, String templateCode, String templateParam) {
        SendSmsRequest request = getSmsRequestBody(phoneNumbers, signName, templateCode, templateParam);
        try {
            SendSmsResponse sendSmsResponse = AcsClientSingleton.getInstance().getAcsClient().getAcsResponse(request);
            if (sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {//请求成功
                return true;
            }
            logger.error("短信发送失败：{}，原因：{}", phoneNumbers, sendSmsResponse.getMessage());
            throw new BizException("短信发送失败：" + sendSmsResponse.getMessage());
        } catch (Exception e) {//请求失败这里会抛ClientException异常
            logger.error("短信发送异常:", e);
            throw new BizException("短信发送失败：", e);
        }
    }

    private static SendSmsRequest getSmsRequestBody(String phoneNumbers, String signName, String templateCode, String templateParam) {
        //组装请求对象
        SendSmsRequest request = new SendSmsRequest();
        //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为20个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
        request.setPhoneNumbers(phoneNumbers);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName(signName);
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(templateCode);
        //尊敬的用户，欢迎注册骑驴指标。短信验证码：${code}。
        request.setTemplateParam(templateParam);

        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        //request.setTemplateParam("{\"name\":\"Tom\", \"code\":\"123\"}");
        //可选-上行短信扩展码(无特殊需求用户请忽略此字段)
        //request.setSmsUpExtendCode("90997");
        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        //request.setOutId(outId);
        return request;
    }
}
