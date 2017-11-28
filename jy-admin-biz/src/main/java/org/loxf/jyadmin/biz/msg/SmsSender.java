package org.loxf.jyadmin.biz.msg;

import org.loxf.jyadmin.biz.util.SendMsgUtils;

import java.util.Map;

public class SmsSender implements ISender {
    @Override
    public boolean send(Map params, String target) {
        int codeType = (int)params.get("codeType");
        String code = (String) params.get("verifyCode");
        return SendMsgUtils.sendMsgByType(codeType, code, target);
    }
}
