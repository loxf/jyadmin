package org.loxf.jyadmin.biz;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.exception.BizException;
import org.loxf.jyadmin.base.util.IdGenerator;
import org.loxf.jyadmin.base.util.MD5;
import org.loxf.jyadmin.biz.util.ConfigUtil;
import org.loxf.jyadmin.client.dto.ConfigDto;
import org.loxf.jyadmin.client.dto.CustCashDto;
import org.loxf.jyadmin.client.service.AccountService;
import org.loxf.jyadmin.client.service.CustCashService;
import org.loxf.jyadmin.dal.dao.CustCashMapper;
import org.loxf.jyadmin.dal.po.CustCash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service("custCashService")
public class CustCashServiceImpl implements CustCashService {
    private static Logger logger = LoggerFactory.getLogger(CustCashServiceImpl.class);
    @Autowired
    private CustCashMapper custCashMapper;
    @Autowired
    private AccountService accountService;

    @Override
    public PageResult<CustCashDto> queryCustCash(CustCashDto custCashDto) {
        if (custCashDto == null) {
            return new PageResult<>(BaseConstant.FAILED, "参数为空");
        }
        CustCash custCash = new CustCash();
        BeanUtils.copyProperties(custCashDto, custCash);
        int total = custCashMapper.count(custCash);
        List<CustCashDto> result = new ArrayList<>();
        if (total > 0) {
            List<CustCash> list = custCashMapper.pager(custCash);
            if (CollectionUtils.isNotEmpty(list)) {
                for (CustCash tmp : list) {
                    CustCashDto dto = new CustCashDto();
                    BeanUtils.copyProperties(tmp, dto);
                    result.add(dto);
                }
            }
        }
        int totalPage = total / custCashDto.getPager().getSize() + (total % custCashDto.getPager().getSize() == 0 ? 0 : 1);
        return new PageResult(totalPage, custCashDto.getPager().getPage(), total, result);
    }

    @Override
    public BaseResult<Boolean> addCustCashRecord(CustCashDto custCashDto, String password, String sign) {
        if (custCashDto == null) {
            return new BaseResult<>(BaseConstant.FAILED, "参数为空");
        }
        if (custCashDto.getType() == null || (custCashDto.getType() != 1 && custCashDto.getType() != 2)) {
            return new BaseResult<>(BaseConstant.FAILED, "提现类型必须为微信/银行卡");
        }
        if (StringUtils.isBlank(custCashDto.getObjId())) {
            return new BaseResult<>(BaseConstant.FAILED, "提现对象为空");
        }
        int cash_min_nbr = Integer.valueOf(ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_PAY,
                "CASH_MIN_NBR", "100").getConfigValue());
        if (custCashDto.getBalance().compareTo(new BigDecimal(cash_min_nbr))<0 ) {
            return new BaseResult<>(BaseConstant.FAILED, "最低提现" + cash_min_nbr + "元");
        }
        if (StringUtils.isBlank(sign)) {
            return new BaseResult<>(BaseConstant.FAILED, "签名为空");
        }
        if (StringUtils.isBlank(password)) {
            return new BaseResult<>(BaseConstant.FAILED, "支付密码为空");
        }
        // 前面 MD5加密字符串：type||obj_id||balance
        String str = custCashDto.getType() + "||" + custCashDto.getObjId() + "||" + custCashDto.getBalance().toPlainString();
        String encodeStr = MD5.MD5(str);
        if (!encodeStr.equalsIgnoreCase(sign)) {
            return new BaseResult<>(BaseConstant.FAILED, "签名被篡改");
        }
        try {
            dealCmmsAmt(custCashDto);
            custCashDto.setOrderId(IdGenerator.generate("CASH"));
            return dealTaskCash(custCashDto, password);
        } catch (BizException e) {
            return new BaseResult<>(BaseConstant.FAILED, e.getName());
        }
    }

    private void dealCmmsAmt(CustCashDto custCashDto){
        // 计算手续费  实际提现金额
        BaseResult<BigDecimal> accountBalanceResult = accountService.queryBalance(custCashDto.getCustId());
        if(accountBalanceResult.getCode()==BaseConstant.FAILED){
            throw new BizException("获取账户失败");
        }
        BigDecimal accountBalance = accountBalanceResult.getData();
        String rateStr = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_PAY, "CASH_CMMS_AMT", "0.6").
                getConfigValue();
        // 手续费 费率
        BigDecimal rate = new BigDecimal(rateStr).divide(new BigDecimal(100));
        // 手续费 四舍五入
        BigDecimal cmmsAmt = custCashDto.getBalance().multiply(rate).setScale(2, BigDecimal.ROUND_HALF_UP);
        if(cmmsAmt.add(custCashDto.getBalance()).compareTo(accountBalance)>0){
            // 手续费加提现金额大于余额，余额不足，实际提现： X+X*RATE=accountBalance X=accountBalance/(1+RATE)
            BigDecimal tmp = BigDecimal.ONE.add(rate);
            BigDecimal factBalance = accountBalance.divide(tmp, 2, BigDecimal.ROUND_HALF_UP);
            cmmsAmt = accountBalance.subtract(factBalance);
            custCashDto.setCmmsAmt(cmmsAmt);
            custCashDto.setFactBalance(factBalance);
        } else {
            // 手续费加提现金额小于等于余额，余额充足，实际提现=提现金额
            custCashDto.setCmmsAmt(cmmsAmt);
            custCashDto.setFactBalance(custCashDto.getBalance());
        }
    }

    @Transactional
    public BaseResult<Boolean> dealTaskCash(CustCashDto custCashDto, String password) {
        // 扣减余额
        BaseResult<Boolean> reduce = accountService.reduce(custCashDto.getCustId(),
                password, custCashDto.getCmmsAmt().add(custCashDto.getFactBalance()), null, null, "提现");

        if (reduce.getCode() == BaseConstant.FAILED || !reduce.getData()) {
            return reduce;
        }
        // 插入提现记录
        CustCash custCash = new CustCash();
        BeanUtils.copyProperties(custCashDto, custCash);
        if (custCashMapper.insert(custCash) <= 0) {
            throw new BizException("新增提现申请失败");
        }
        return new BaseResult(true);
    }

    @Override
    @Transactional
    public BaseResult<Boolean> pendingCustCashRecord(String orderId, Integer status, String remark) {
        // 状态：1：未打款 3：已打款 -3:拒绝打款
        if (orderId == null || status == null) {
            return new BaseResult<>(BaseConstant.FAILED, "参数为空");
        }
        if (status != 3 && status != -3) {
            return new BaseResult<>(BaseConstant.FAILED, "审核状态不正确");
        }
        CustCash custCash = custCashMapper.selectById(orderId);
        if (custCash == null) {
            return new BaseResult<>(BaseConstant.FAILED, "提现记录不存在");
        }
        if (custCash.getStatus() == 3 || custCash.getStatus() == -3) {
            return new BaseResult<>(BaseConstant.FAILED, "当前记录已处理");
        }
        // 如果审核未通过，补余额
        if (status == -3) {
            BaseResult<Boolean> increase = accountService.increase(custCash.getCustId(),
                    custCash.getBalance(), null, null, "拒绝提现退款");
            if (increase.getCode() == BaseConstant.FAILED || !increase.getData()) {
                return new BaseResult<>(BaseConstant.FAILED, increase.getMsg());
            }
        }
        return new BaseResult<>(custCashMapper.update(orderId, status, remark) > 0);
    }

    @Override
    @Transactional
    public BaseResult<Boolean> takeCashDeal(CustCashDto custCashDto) {
        // 锁定提现
        if (custCashMapper.lock(custCashDto.getOrderId()) > 0) {
            try {
                if (custCashDto.getType() == 1) {
                    // TODO 微信提现
                    // 微信提现 OBJID是custId
                } else {
                    // 银行卡提现
                }
                custCashMapper.update(custCashDto.getOrderId(), 3, "成功");
                return new BaseResult<>();
            } catch (Exception e) {
                logger.error("提现失败", e);
                custCashMapper.update(custCashDto.getOrderId(), -9, e.getMessage());
                return new BaseResult<>(BaseConstant.FAILED, "提现失败");
            } finally {
                // 解除锁定
                custCashMapper.unlock(custCashDto.getOrderId());
            }
        }
        return new BaseResult<>(BaseConstant.FAILED, "锁定订单失败");
    }
}
