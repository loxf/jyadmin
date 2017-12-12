package org.loxf.jyadmin.biz;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.biz.util.ConfigUtil;
import org.loxf.jyadmin.client.service.AccountService;
import org.loxf.jyadmin.client.service.CustSignService;
import org.loxf.jyadmin.dal.dao.CustSignMapper;
import org.loxf.jyadmin.dal.po.CustBpDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service("custSignService")
public class CustSignServiceImpl implements CustSignService {
    @Autowired
    private CustSignMapper custSignMapper;
    @Autowired
    private AccountService accountService;

    @Override
    public BaseResult<Boolean> sign(String custId, String signDate) {
        if(custSignMapper.selectByCustAndSignDate(custId, signDate)>0){
            return new BaseResult<>(true);
        }
        String bp = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "BP_SIGN", "1").getConfigValue();
        BaseResult baseResult = accountService.increase(custId, null, new BigDecimal(bp), null, "签到得积分", null);
        if(baseResult.getCode()== BaseConstant.SUCCESS) {
            return new BaseResult<>(custSignMapper.insert(custId, signDate) > 0);
        } else {
            return new BaseResult<>(BaseConstant.FAILED, baseResult.getMsg());
        }
    }

    @Override
    public BaseResult<Boolean> hasSign(String custId, String signDate) {
        return new BaseResult<>(custSignMapper.selectByCustAndSignDate(custId, signDate)>0);
    }
}
