package org.loxf.jyadmin.biz;

import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.util.JedisUtil;
import org.loxf.jyadmin.biz.msg.EmailSender;
import org.loxf.jyadmin.biz.msg.ISender;
import org.loxf.jyadmin.biz.msg.SmsSender;
import org.loxf.jyadmin.biz.util.ConfigUtil;
import org.loxf.jyadmin.client.service.VerifyCodeService;
import org.loxf.jyadmin.dal.dao.VerifyCodeMapper;
import org.loxf.jyadmin.dal.po.VerifyCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service("verifyCodeService")
public class VerifyCodeServiceImpl implements VerifyCodeService {
    private static String prefix = "V_CODE_";
    @Autowired
    private VerifyCodeMapper verifyCodeMapper;
    @Autowired
    private JedisUtil jedisUtil;

    /**
     * @param obj
     * @param codeType 1：设置支付密码， 2：绑定用户
     * @return
     */
    @Override
    @Transactional
    public BaseResult sendVerifyCode(String custId, String obj, Integer codeType) {
        if(StringUtils.isBlank(obj)){
            return new BaseResult(BaseConstant.FAILED, "联系方式为空");
        }
        if(codeType!=1 && codeType!=2){
            return new BaseResult(BaseConstant.FAILED, "短信类型为空");
        }
        String emailReg = "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?";
        int sendType = 1;// 发送方式 SMS
        if(obj.matches(emailReg)){
            sendType = 2;// EMAIL
        }
        // 验证码限流
        int limitSize;
        if(sendType==1){
            limitSize = Integer.valueOf(ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "VERIFY_SMS_CODE", "10").getConfigValue());
        } else {
            limitSize = Integer.valueOf(ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "VERIFY_EMAIL_CODE", "10").getConfigValue());
        }
        int sendNbr = verifyCodeMapper.selectCount(obj, codeType);
        if(sendNbr>=limitSize){
            return new BaseResult(BaseConstant.FAILED, "发送验证码超限");
        }
        // 获取验证码
        String code = getRandNum(1, 9999);
        // 发送验证码
        ISender sender ;
        if(sendType==1){
            sender = new SmsSender();
        } else {
            sender = new EmailSender();
        }
        Map params = new HashMap<>();
        params.put("verifyCode", code);
        params.put("codeType", codeType);
        BaseResult sendResult = sender.send(params, obj);
        if(sendResult.getCode()==BaseConstant.SUCCESS){
            jedisUtil.set(prefix + custId, code, 5*60);
            insertCode(obj, code, codeType, sendType);
            return new BaseResult();
        } else {
            return new BaseResult(BaseConstant.FAILED, "请勿频繁发送短信");
        }
    }

    @Override
    public BaseResult verify(String custId, String code) {
        String currentCode = jedisUtil.get(prefix + custId);
        if(currentCode==null){
            return new BaseResult(BaseConstant.FAILED, "验证码不存在或已过期");
        }
        // 无论怎样 先删除验证码
        jedisUtil.del(prefix + custId);
        if(!currentCode.equals(code)){
            return new BaseResult(BaseConstant.FAILED, "验证码不正确");
        }
        return new BaseResult();
    }

    private String getRandNum(int min, int max) {
        int randNum = min + (int)(Math.random() * ((max - min) + 1));
        String code = randNum + "";
        if(code.length()<4){
            for(int len=code.length(); len<4; len++ ){
                code = "0" + code;
            }
        }
        return code;
    }

    private void insertCode(String obj, String code, int codeType, int sendType){
        VerifyCode verifyCode = new VerifyCode();
        verifyCode.setObj(obj);
        verifyCode.setCode(code);
        verifyCode.setCodeType(codeType);
        verifyCode.setSendType(sendType);
        verifyCodeMapper.insert(verifyCode);
    }
}
