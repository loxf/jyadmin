package org.loxf.jyadmin.biz.msg;

import com.alibaba.fastjson.JSON;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.biz.util.SendMailUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class EmailSender implements ISender {
    private static final String verifyMail = "verifyMail.vm";
    private static Logger logger = LoggerFactory.getLogger(EmailSender.class);
    @Override
    public BaseResult send(Map params, String target) {
        String [] toMail = new String[]{target};
        try {
            SendMailUtil.getInstance().sendEmail(params, "静怡雅学馆验证码", verifyMail, toMail , null);
            return new BaseResult();
        } catch (Exception e){
            logger.error("发送邮件消息失败：target:" + params + ", param : " + JSON.toJSONString(params), e);
            return new BaseResult(BaseConstant.FAILED, e.getMessage());
        }
    }
}
