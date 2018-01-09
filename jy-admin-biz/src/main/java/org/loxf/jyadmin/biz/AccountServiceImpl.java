package org.loxf.jyadmin.biz;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.biz.util.SendWeixinMsgUtil;
import org.loxf.jyadmin.client.dto.ActiveCustListDto;
import org.loxf.jyadmin.client.dto.CustDto;
import org.loxf.jyadmin.client.service.AccountService;
import org.loxf.jyadmin.client.service.CustService;
import org.loxf.jyadmin.client.service.VerifyCodeService;
import org.loxf.jyadmin.dal.dao.AccountDetailMapper;
import org.loxf.jyadmin.dal.dao.AccountMapper;
import org.loxf.jyadmin.dal.dao.CustBpDetailMapper;
import org.loxf.jyadmin.dal.dao.CustMapper;
import org.loxf.jyadmin.dal.po.Account;
import org.loxf.jyadmin.dal.po.AccountDetail;
import org.loxf.jyadmin.dal.po.Cust;
import org.loxf.jyadmin.dal.po.CustBpDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service("accountService")
public class AccountServiceImpl implements AccountService {
    private static Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private AccountDetailMapper accountDetailMapper;
    @Autowired
    private CustBpDetailMapper custBpDetailMapper;
    @Autowired
    private VerifyCodeService verifyCodeService;
    @Autowired
    private CustMapper custMapper;
    @Value("#{configProperties['JYZX.INDEX.URL']}")
    private String JYZX_INDEX_URL;


    @Override
    public PageResult<JSONObject> queryBalanceList(CustDto custDto) {
        Cust cust = new Cust();
        BeanUtils.copyProperties(custDto, cust);
        int total = accountMapper.queryBalanceListCount(cust);
        List<JSONObject> result = new ArrayList<>();
        if(total>0){
            result = accountMapper.queryBalanceList(cust);
        }
        int totalPage = total/custDto.getPager().getSize() + (total%custDto.getPager().getSize()==0?0:1);
        return new PageResult<JSONObject>(totalPage, custDto.getPager().getPage(), total, result);
    }

    @Override
    public BaseResult<BigDecimal> queryBalance(String custId) {
        Account account = accountMapper.selectAccount(custId);
        if(account==null){
            return new BaseResult<>(BaseConstant.FAILED, "客户不存在");
        }
        return new BaseResult<>(account.getBalance());
    }

    @Override
    public BaseResult<JSONObject> queryAccount(String custId) {
        Account account = accountMapper.selectAccount(custId);
        if(account==null){
            return new BaseResult<>(BaseConstant.FAILED, "客户不存在");
        }
        JSONObject result = new JSONObject();
        result.put("balance", account.getBalance().toPlainString());
        result.put("bp", account.getBp().toPlainString());
        // 获取最近累积收入
        int totalIncome = accountDetailMapper.sumByType(custId, null, 1);
        result.put("totalIncome", totalIncome);
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
    public BaseResult<Boolean> payByAdmin(String custId, BigDecimal money, BigDecimal bp, String orderId, String detailName) {
        // 账户锁定
        if(accountMapper.lockAccount(custId)<=0) {
            return new BaseResult<>(BaseConstant.FAILED, "账户锁定失败");
        }
        try {
            // 获取账户信息
            Account account = accountMapper.selectAccount(custId);
            // 扣钱/积分
            if (money != null && money.compareTo(BigDecimal.ZERO) > 0) {
                // 账户明细
                accountDetailMapper.insert(createAccountDetail(custId, account.getBalance(), money,
                        detailName, orderId, 3, null));
            }
            if (bp != null && bp.compareTo(BigDecimal.ZERO) > 0) {
                // 积分明细
                custBpDetailMapper.insert(createBpDetail(custId, account.getBp(), bp, detailName, orderId, 3));
            }
        } catch (Exception e){
            logger.error("支付异常", e);
            throw new RuntimeException(e);
        } finally {
            accountMapper.unlockAccount(custId);
        }
        return new BaseResult(true);
    }

    @Override
    @Transactional
    public BaseResult<Boolean> reduce(String custId, String password, BigDecimal money, BigDecimal bp, String orderId, String detailName) {
        // 账户锁定
        if(accountMapper.lockAccount(custId)<=0) {
            return new BaseResult<>(BaseConstant.FAILED, "账户锁定失败");
        }
        try {
            // 获取账户信息
            Account account = accountMapper.selectAccount(custId);
            if (StringUtils.isBlank(password) || StringUtils.isBlank(account.getPassword()) || !password.equals(account.getPassword())) {
                return new BaseResult<>(BaseConstant.FAILED, "支付密码错误");
            }
            if (money != null && money.compareTo(account.getBalance()) > 0) {
                return new BaseResult<>(BaseConstant.FAILED, "余额不足");
            }
            // 扣钱/积分
            Account newAccountInfo = new Account();
            if (money != null && money.compareTo(BigDecimal.ZERO) > 0) {
                // 账户明细
                newAccountInfo.setBalance(account.getBalance().subtract(money));
                accountDetailMapper.insert(createAccountDetail(custId, newAccountInfo.getBalance(), money,
                        detailName, orderId, 3, null));
            }
            if (bp != null && bp.compareTo(BigDecimal.ZERO) > 0) {
                // 积分明细
                newAccountInfo.setBp(account.getBp().subtract(bp));
                custBpDetailMapper.insert(createBpDetail(custId, newAccountInfo.getBp(), bp, detailName, orderId, 3));
            }
            newAccountInfo.setCustId(custId);
            // 记录账户明细
            accountMapper.updateBalanceOrBp(newAccountInfo);
        } catch (Exception e){
            logger.error("支付异常", e);
            throw new RuntimeException(e);
        } finally {
            accountMapper.unlockAccount(custId);
        }
        return new BaseResult(true);
    }

    @Override
    @Transactional
    public BaseResult<Boolean> increase(String custId, BigDecimal money, BigDecimal bp, String orderId, String detailName, String sourceCustId) {
        // 账户锁定
        if(accountMapper.lockAccount(custId)<=0) {
            return new BaseResult<>(BaseConstant.FAILED, "账户锁定失败");
        }
        try {
            // 获取账户信息
            Account account = accountMapper.selectAccount(custId);
            // 加钱/积分 记录账户明细
            Account newAccountInfo = new Account();
            boolean insertDetail = false;
            if (money != null && money.compareTo(BigDecimal.ZERO) > 0) {
                //账户明细
                newAccountInfo.setBalance(account.getBalance().add(money));
                insertDetail = accountDetailMapper.insert(createAccountDetail(custId, newAccountInfo.getBalance(), money,
                        detailName, orderId, 1, sourceCustId)) > 0;
            }
            if (bp != null && bp.compareTo(BigDecimal.ZERO) > 0) {
                // 积分明细
                newAccountInfo.setBp(account.getBp().add(bp));
                insertDetail = custBpDetailMapper.insert(createBpDetail(custId, newAccountInfo.getBp(), bp, detailName,
                        orderId, 1)) > 0;
                Cust cust = custMapper.selectByCustId(custId);
                SendWeixinMsgUtil.sendGetBpNotice(cust.getOpenid(), cust.getNickName(), detailName,
                        bp.toPlainString(), newAccountInfo.getBp().toPlainString(), JYZX_INDEX_URL );
            }
            newAccountInfo.setCustId(custId);
            if (insertDetail) {
                if (accountMapper.updateBalanceOrBp(newAccountInfo) <= 0) {
                    throw new RuntimeException("增加余额失败");
                }
            }
        } catch (Exception e){
            logger.error("支付异常[custId" + custId, e);
            throw new RuntimeException(e);
        } finally {
            accountMapper.unlockAccount(custId);
        }
        return new BaseResult(true);
    }

    /**
     * 第三方已经扣款 只需要记录账户明细
     * @param custId
     * @param money      第三方支付金额
     * @param bp         准备扣减的积分
     * @param orderId
     * @param detailName
     * @return
     */
    @Override
    @Transactional
    public BaseResult<Boolean> reduceByThird(String custId, BigDecimal money, BigDecimal bp, String orderId, String detailName) {
        // 账户锁定
        if(accountMapper.lockAccount(custId)<=0) {
            return new BaseResult<>(BaseConstant.FAILED, "账户锁定失败");
        }
        try {
            // 获取账户信息
            Account account = accountMapper.selectAccount(custId);
            if (money != null && money.compareTo(BigDecimal.ZERO) > 0) {
                // 账户明细
                accountDetailMapper.insert(createAccountDetail(custId, account.getBalance(), money,
                        detailName, orderId, 3, null));
            }
            if (bp != null && bp.compareTo(BigDecimal.ZERO) > 0) {
                // 扣积分
                Account newAccountInfo = new Account();
                newAccountInfo.setBp(account.getBp().subtract(bp));
                newAccountInfo.setCustId(custId);
                accountMapper.updateBalanceOrBp(newAccountInfo);
                // 记录积分明细
                custBpDetailMapper.insert(createBpDetail(custId, newAccountInfo.getBp(), bp, detailName, orderId, 3));
            }
        } catch (Exception e){
            logger.error("支付异常", e);
            throw new RuntimeException(e);
        } finally {
            accountMapper.unlockAccount(custId);
        }
        return new BaseResult(true);
    }

    @Override
    public BaseResult setPayPassword(String custId, String email, String phone, int isChinese, String password, String verifyCode) {
        BaseResult baseResult = verifyCodeService.verify(custId, verifyCode);
        if(baseResult.getCode()==BaseConstant.FAILED){
            return baseResult;
        }
        accountMapper.setPayPassword(custId, password);
        return new BaseResult();
    }

    @Override
    public BaseResult<JSONObject> queryBpRankingList(String custId) {
        List<HashMap> bpList = accountMapper.selectBpTop10();
        Integer ranking = accountMapper.queryBpRankingByCustId(custId);
        if(ranking==null){
            ranking = 100000;
        }
        JSONObject result = new JSONObject();
        result.put("tenTop", bpList);
        result.put("myRanking", ranking);
        return new BaseResult<>(result);
    }

    @Override
    public BaseResult<String> delAccount(String custId) {
        accountMapper.deleteByCustId(custId);
        return new BaseResult<>();
    }

    private AccountDetail createAccountDetail(String custId, BigDecimal balance, BigDecimal changeMoney,
                                              String detailName, String orderId, Integer type, String sourceCustId){
        AccountDetail accountDetail = new AccountDetail();
        accountDetail.setCustId(custId);
        accountDetail.setBalance(balance);
        accountDetail.setChangeBalance(changeMoney);
        accountDetail.setDetailName(detailName);
        accountDetail.setOrderId(orderId);
        accountDetail.setType(type);
        accountDetail.setSourceCustId(sourceCustId);
        return accountDetail;
    }

    private CustBpDetail createBpDetail(String custId, BigDecimal balance, BigDecimal changeBp,
                                              String detailName, String orderId, Integer type){
        CustBpDetail custBpDetail = new CustBpDetail();
        custBpDetail.setCustId(custId);
        custBpDetail.setBpBalance(balance);
        custBpDetail.setChangeBalance(changeBp);
        custBpDetail.setDetailName(detailName);
        custBpDetail.setOrderId(orderId);
        custBpDetail.setType(type);
        return custBpDetail;
    }
}
