package org.loxf.jyadmin.biz;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.client.service.AccountService;
import org.loxf.jyadmin.dal.dao.AccountDetailMapper;
import org.loxf.jyadmin.dal.dao.AccountMapper;
import org.loxf.jyadmin.dal.po.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("accountService")
public class AccountServiceImpl implements AccountService {
    private static Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private AccountDetailMapper accountDetailMapper;

    @Override
    public BaseResult<JSONObject> queryBasicInfo(String custId) {
        Account account = accountMapper.selectAccount(custId);
        if(account==null){
            return new BaseResult<>(BaseConstant.FAILED, "客户不存在");
        }
        JSONObject result = new JSONObject();
        result.put("balance", account.getBalance().toPlainString());
        result.put("bp", account.getBp().toPlainString());
        // 是否设置支付密码
        result.put("hasPassword", StringUtils.isNotBlank(account.getPassword()));
        // 获取最近收入
        int totalIncome = accountDetailMapper.sumByType(custId, null, 1);
        int todayIncome = 0, recent7DIncome = 0;
        if(totalIncome>0){
            recent7DIncome = accountDetailMapper.sumByType(custId, 6, 1);
            if(recent7DIncome>0){
                todayIncome = accountDetailMapper.sumByType(custId, 0, 1);
            }
        }
        result.put("todayIncome", todayIncome);
        result.put("recent7DIncome", recent7DIncome);
        result.put("totalIncome", totalIncome);
        return new BaseResult<>(result);
    }
}
