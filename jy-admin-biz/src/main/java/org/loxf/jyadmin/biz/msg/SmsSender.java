package org.loxf.jyadmin.biz.msg;

import com.alibaba.fastjson.JSON;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.biz.util.SendMailUtil;
import org.loxf.jyadmin.biz.util.SendSmsMsgUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class SmsSender implements ISender {
    private static Logger logger = LoggerFactory.getLogger(SmsSender.class);
    @Override
    public BaseResult send(Map params, String target) {
        int codeType = (int)params.get("codeType");
        String code = (String) params.get("verifyCode");

        try {
            if(SendSmsMsgUtil.sendMsgByType(codeType, code, target)){
                return new BaseResult();
            } else {
                return new BaseResult(BaseConstant.FAILED, "发送失败，无原因");
            }
        } catch (Exception e){
            logger.error("发送短息消息失败：target:" + params + ", param : " + JSON.toJSONString(params), e);
            return new BaseResult(BaseConstant.FAILED, e.getMessage());
        }
    }
}
