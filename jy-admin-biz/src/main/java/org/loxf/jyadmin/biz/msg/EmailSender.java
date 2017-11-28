package org.loxf.jyadmin.biz.msg;

import org.loxf.jyadmin.biz.util.MailSender;

import java.util.HashMap;
import java.util.Map;

public class EmailSender implements ISender {
    private static final String verifyMail = "verifyMail.vm";
    @Override
    public boolean send(Map params, String target) {
        String [] toMail = new String[]{target};
        MailSender.getInstance().sendEmail(params, "静怡雅学馆验证码", verifyMail, toMail , null);
        return true;
    }
}