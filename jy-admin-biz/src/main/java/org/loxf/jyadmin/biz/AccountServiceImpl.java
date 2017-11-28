package org.loxf.jyadmin.biz;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.client.service.AccountService;
import org.loxf.jyadmin.client.service.VerifyCodeService;
import org.loxf.jyadmin.dal.dao.AccountDetailMapper;
import org.loxf.jyadmin.dal.dao.AccountMapper;
import org.loxf.jyadmin.dal.po.Account;
import org.loxf.jyadmin.dal.po.AccountDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service("accountService")
public class AccountServiceImpl implements AccountService {
    private static Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private AccountDetailMapper accountDetailMapper;
    @Autowired
    private VerifyCodeService verifyCodeService;

    @Override
    public BaseResult<JSONObject> queryAccount(String custId) {
        Account account = accountMapper.selectAccount(custId);
        if(account==null){
            return new BaseResult<>(BaseConstant.FAILED, "客户不存在");
        }
        JSONObject result = new JSONObject();
        result.put("balance", account.getBalance().toPlainString());
        result.put("bp", account.getBp().toPlainString());
        // 是否设置支付密码
        result.put("hasPassword", StringUtils.isNotBlank(account.getPassword()));
        return new BaseResult<>(result);
    }

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

    @Override
    @Transactional
    public BaseResult<Boolean> reduce(String custId, String password, BigDecimal money, String orderId, String detailName) {
        // 账户锁定
        if(accountMapper.lockAccount(custId)<=0) {
            return new BaseResult<>(BaseConstant.FAILED, "账户锁定失败");
        }
        // 获取账户信息
        Account account = accountMapper.selectAccount(custId);
        if(StringUtils.isBlank(password)||account.getPassword()==null || !password.equals(account.getPassword())){
            return new BaseResult<>(BaseConstant.FAILED, "支付密码错误");
        }
        if(money.compareTo(account.getBalance())>0){
            return new BaseResult<>(BaseConstant.FAILED, "余额不足");
        }
        // 扣钱
        Account newAccountInfo = new Account();
        newAccountInfo.setBalance(account.getBalance().subtract(money));
        newAccountInfo.setCustId(custId);
        // 记录账户明细
        if(accountDetailMapper.insert(createAccountDetail(custId, newAccountInfo.getBalance(), money, orderId, detailName, 3))>0){
            if(accountMapper.updateBalanceOrBp(newAccountInfo)<=0){
                throw new RuntimeException("扣减账户余额失败");
            }
        } else {
            return new BaseResult<>(BaseConstant.FAILED, "记录账户明细失败");
        }
        return new BaseResult(true);
    }

    @Override
    @Transactional
    public BaseResult<Boolean> increase(String custId, BigDecimal money, String orderId, String detailName) {
        // 账户锁定
        if(accountMapper.lockAccount(custId)<=0) {
            return new BaseResult<>(BaseConstant.FAILED, "账户锁定失败");
        }
        // 获取账户信息
        Account account = accountMapper.selectAccount(custId);
        // 加钱
        Account newAccountInfo = new Account();
        newAccountInfo.setBalance(account.getBalance().add(money));
        newAccountInfo.setCustId(custId);
        // 记录账户明细
        if(accountDetailMapper.insert(createAccountDetail(custId, newAccountInfo.getBalance(), money, orderId, detailName, 1))>0){
            if(accountMapper.updateBalanceOrBp(newAccountInfo)<=0){
                throw new RuntimeException("增加余额失败");
            }
        } else {
            return new BaseResult<>(BaseConstant.FAILED, "记录账户明细失败");
        }
        return new BaseResult(true);
    }

    @Override
    public BaseResult setPayPassword(String custId, String email, String phone, int isChinese, String password, String verifyCode) {
        String target = phone;
        if(isChinese==2){
            target = email;
        }
        BaseResult baseResult = verifyCodeService.verify(custId, target, verifyCode);
        if(baseResult.getCode()==BaseConstant.FAILED){
            return baseResult;
        }
        accountMapper.setPayPassword(custId, password);
        return new BaseResult();
    }

    private AccountDetail createAccountDetail(String custId, BigDecimal balance, BigDecimal changeMoney,
                                              String detailName, String orderId, Integer type){
        AccountDetail accountDetail = new AccountDetail();
        accountDetail.setCustId(custId);
        accountDetail.setBalance(balance);
        accountDetail.setChangeBalance(changeMoney);
        accountDetail.setDetailName(detailName);
        accountDetail.setOrderId(orderId);
        accountDetail.setType(type);
        return accountDetail;
    }
}
