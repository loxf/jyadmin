package org.loxf.jyadmin.biz;

import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.exception.BizException;
import org.loxf.jyadmin.base.util.DateUtils;
import org.loxf.jyadmin.base.util.JedisUtil;
import org.loxf.jyadmin.biz.util.ConfigUtil;
import org.loxf.jyadmin.biz.util.RandomUtils;
import org.loxf.jyadmin.client.service.AccountService;
import org.loxf.jyadmin.client.service.TradeService;
import org.loxf.jyadmin.dal.dao.*;
import org.loxf.jyadmin.dal.po.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service("tradeService")
public class TradeServiceImpl implements TradeService {
    private Logger logger = LoggerFactory.getLogger(TradeServiceImpl.class);
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private TradeMapper tradeMapper;
    @Autowired
    private PurchasedInfoMapper purchasedInfoMapper;
    @Autowired
    private VipInfoMapper vipInfoMapper;
    @Autowired
    private ActiveCustListMapper activeCustListMapper;
    @Autowired
    private OrderAttrMapper orderAttrMapper;
    @Autowired
    private CustMapper custMapper;
    @Autowired
    private JedisUtil jedisUtil;
    @Autowired
    private AccountService accountService;
    @Autowired
    private CompanyIncomeMapper companyIncomeMapper;

    @Override
    public BaseResult<String> completeTrade(String orderId, Integer status, String msg) {
        if (StringUtils.isBlank(orderId)) {
            return new BaseResult<>(BaseConstant.FAILED, "订单号为空");
        }
        if (status == null || (status.intValue() != 3 && status.intValue() != -3)) {
            return new BaseResult<>(BaseConstant.FAILED, "状态不正确");
        }
        String key = "COMPLETE_TRADE_" + orderId;
        if (jedisUtil.setnx(key, "true", 60) > 0) {
            try {
                Order orderAgain = orderMapper.selectByOrderId(orderId);
                if (orderAgain == null) {
                    return new BaseResult<>(BaseConstant.FAILED, "订单不存在");
                }
                if (orderAgain.getStatus().intValue() != 3) {
                    return new BaseResult<>(BaseConstant.FAILED, "当前订单状态不正确");
                }
                Trade trade = tradeMapper.selectByOrderId(orderId);
                if (trade == null) {
                    return new BaseResult<>(BaseConstant.FAILED, "交易不存在");
                }
                if (trade.getState().intValue() != 1) {
                    return new BaseResult<>(BaseConstant.FAILED, "当前交易状态不正确");
                }
                // 查询客户信息
                Cust cust = custMapper.selectByCustId(orderAgain.getCustId());
                dealTrade(cust, cust.getRecommend(), orderId, status, msg, orderAgain);
                return new BaseResult<>();
            } catch (Exception e) {
                logger.error("交易失败", e);
                return new BaseResult(BaseConstant.FAILED, "交易失败");
            } finally {
                jedisUtil.del(key);
            }
        } else {
            return new BaseResult<>(BaseConstant.FAILED, "当前订单正在处理");
        }
    }

    @Transactional
    public void dealTrade(Cust cust ,String recommend, String orderId, int status, String msg, Order order) {
        Cust custFirst = null;
        Cust custSecond = null;
        if (StringUtils.isNotBlank(recommend)) {
            custFirst = custMapper.selectByCustId(recommend);
            if (custFirst != null) {
                if (StringUtils.isNotBlank(custFirst.getRecommend())) {
                    custSecond = custMapper.selectByCustId(recommend);
                }
            }
        }
        if (status == 3) {
            String userName = (StringUtils.isNotBlank(cust.getRealName())? cust.getRealName() : cust.getNickName()) ;
            // 增加购买记录
            PurchasedInfo purchasedInfo = new PurchasedInfo();
            purchasedInfo.setCustId(order.getCustId());
            purchasedInfo.setNickName(order.getCustName());
            purchasedInfo.setOfferId(order.getObjId());
            purchasedInfo.setType(order.getOrderType());
            purchasedInfo.setOrderId(orderId);
            purchasedInfoMapper.insert(purchasedInfo);
            // 支付成功,订单完成，开始交易
            String firstScholarships = "";// 一级奖学金
            String secondScholarships = "";// 二级奖学金
            String detailName = "";
            if (order.getOrderType() == 3) {
                String vipType = order.getObjId().equals("OFFER001") ? "VIP" : "SVIP";
                detailName += "升级" + vipType;
                dealVip(order.getCustId(), vipType);
                if (custFirst != null) {
                    firstScholarships = queryScholarshipsRate(custFirst, "STUDENT", 1);
                }
                if(custSecond!=null){
                    secondScholarships = queryScholarshipsRate(custSecond, "STUDENT", 2);
                }
            } else if (order.getOrderType() == 5) {
                detailName += "购买活动";
                // 增加活动名单信息
                dealActive(order);
                if (custFirst != null) {
                    firstScholarships = queryScholarshipsRate(custFirst, "ACTIVE", 1);
                }
                if(custSecond!=null){
                    secondScholarships = queryScholarshipsRate(custSecond, "ACTIVE", 2);
                }
            } else {
                detailName += "购买课程/套餐";
                if (custFirst != null) {
                    firstScholarships = queryScholarshipsRate(custFirst, "OFFER", 1);
                }
                if(custSecond!=null){
                    secondScholarships = queryScholarshipsRate(custSecond, "OFFER", 2);
                }
            }
            // 分成计算
            BigDecimal companyAmount = order.getOrderMoney();
            if(StringUtils.isNotBlank(firstScholarships)) {
                BigDecimal first = order.getOrderMoney().multiply(new BigDecimal(firstScholarships)).
                        divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
                BaseResult<Boolean> baseResult = accountService.increase(custFirst.getCustId(), first, null, orderId,
                        "奖学金：直接会员" + userName + "，" + detailName, cust.getCustId());
                if(baseResult.getCode()==BaseConstant.FAILED){
                    throw new BizException(baseResult.getMsg());
                }
                companyAmount = companyAmount.subtract(first);
            }
            if(StringUtils.isNotBlank(secondScholarships)) {
                BigDecimal second = order.getOrderMoney().multiply(new BigDecimal(secondScholarships)).
                        divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
                BaseResult<Boolean> baseResult = accountService.increase(custSecond.getCustId(), second, null, orderId,
                        "间接会员" + detailName + "，获得奖学金", cust.getCustId());
                if(baseResult.getCode()==BaseConstant.FAILED){
                    throw new BizException(baseResult.getMsg());
                }
                companyAmount = companyAmount.subtract(second);
            }
            // 计算公司收入
            companyIncomeMapper.insert(createCompanyIncome(cust.getCustId(), userName, detailName,
                    companyAmount, orderId, order.getOrderType()));
        }
        tradeMapper.updateByOrderId(orderId, status, msg);
    }

    private CompanyIncome createCompanyIncome(String custId, String custName, String detailName, BigDecimal companyAmount, String source, int type ){
        CompanyIncome companyIncome = new CompanyIncome();
        companyIncome.setCustId(custId);
        companyIncome.setCustName(custName);
        companyIncome.setAmount(companyAmount);
        companyIncome.setDetailName(detailName);
        companyIncome.setSource(source);
        companyIncome.setType(type);
        return companyIncome;
    }

    /**
     * @param cust
     * @param type STUDENT（VIP），OFFER（课程，套餐），ACTIVE（活动）
     * @param first 1:直接 2:间接
     * @return
     */
    private String queryScholarshipsRate(Cust cust, String type, int first){
        String firstAndSecondScholarships = null;
        if (cust.getIsAgent() != null && cust.getIsAgent() > 0) {
            if (cust.getIsAgent() == 1) {
                // 代理商
                firstAndSecondScholarships = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_PAY,
                        "AGENT_FIRST_" + type, "30,5").getConfigValue();
            } else if (cust.getIsAgent() == 2) {
                // 合伙人
                firstAndSecondScholarships = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_PAY,
                        "PARTEN_FIRST_" + type, "30,5").getConfigValue();
            } else if (cust.getIsAgent() == 3) {
                // 分公司
                firstAndSecondScholarships = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_PAY,
                        "COMPANY_FIRST_" + type, "30,5").getConfigValue();
            }
        } else if (StringUtils.isNotBlank(cust.getUserLevel())) {
            if (cust.getUserLevel().equals("NONE")) {
                // 非会员
                firstAndSecondScholarships = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_PAY,
                        "NONE_FIRST_" + type, "30,5").getConfigValue();
            } else {
                // 会员
                firstAndSecondScholarships = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_PAY,
                        "VIP_FIRST_" + type, "30,5").getConfigValue();
            }
        }
        if(StringUtils.isNotBlank(firstAndSecondScholarships)){
            String[] tmp = firstAndSecondScholarships.split(",");
            if(first<tmp.length){
                return "";
            }
            return tmp[first];
        }
        return "";
    }
    public void dealActive(Order order) {
        ActiveCustList activeCustList = new ActiveCustList();
        activeCustList.setActiveTicketNo(DateUtils.format(new Date(), "yyMMddHHmmss") + RandomUtils.getRandomStr(4));
        activeCustList.setActiveId(order.getObjId());
        activeCustList.setCustId(order.getCustId());
        activeCustList.setActiveName(order.getOrderName());
        List<OrderAttr> orderAttrs = orderAttrMapper.selectByOrderId(order.getOrderId());
        for (OrderAttr attr : orderAttrs) {
            if (attr.getAttrCode().equals("CUST_NAME")) {
                activeCustList.setName(attr.getAttrValue());
            } else if (attr.getAttrCode().equals("CUST_PHONE")) {
                activeCustList.setPhone(attr.getAttrValue());
            }
        }
        activeCustList.setStatus(1);// 已付款
        activeCustListMapper.insert(activeCustList);
    }

    public void dealVip(String custId, String vipType) {
        VipInfo vipInfo = vipInfoMapper.selectByCustId(custId);
        if (vipInfo == null) {
            // 增加/修改VIP INFO，延长VIP期限
            vipInfo = new VipInfo();
            vipInfo.setCustId(custId);
            vipInfo.setStatus(1);
            vipInfo.setMetaData("");// VIP元数据
            vipInfo.setType(vipType);
            Date now = new Date();
            vipInfo.setEffDate(now);
            Date end = DateUtils.getAddTime(now, Calendar.DATE, getValidDay(vipType));
            vipInfo.setExpDate(DateUtils.getEndDate(end));
            vipInfoMapper.insert(vipInfo);
        } else {
            // 修改VIP INFO，延长VIP期限，刷新VIP
            VipInfo updateVip = new VipInfo();
            updateVip.setCustId(custId);
            if (vipInfo.getStatus() == 3) {
                updateVip.setStatus(1);
                Date now = new Date();
                updateVip.setEffDate(now);
                Date end = DateUtils.getAddTime(now, Calendar.DATE, getValidDay(vipType));
                updateVip.setExpDate(DateUtils.getEndDate(end));
                vipInfoMapper.updateByCustId(updateVip);
            } else {
                if (vipInfo.getType().equals("VIP") && vipType.equals("SVIP")) {
                    // VIP升级SVIP,从升级当天计算
                    Date end = DateUtils.getAddTime(new Date(), Calendar.DATE, getValidDay(vipType));
                    updateVip.setExpDate(DateUtils.getEndDate(end));
                    updateVip.setType(vipType);
                    vipInfoMapper.updateByCustId(updateVip);
                } else {
                    Date end = DateUtils.getAddTime(vipInfo.getExpDate(), Calendar.DATE, getValidDay(vipType));
                    updateVip.setExpDate(DateUtils.getEndDate(end));
                    vipInfoMapper.updateByCustId(updateVip);
                }
            }
        }
        // 更新user_info
        Cust cust = new Cust();
        cust.setUserLevel(vipType);
        custMapper.updateByCustIdOrOpenid(cust);
    }

    public int getValidDay(String vipType) {
        int validDay = 0;
        if (vipType.equals("VIP")) {
            validDay = Integer.valueOf(ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_PAY,
                    "VIP_VALID_DAY", "365").getConfigValue());
        } else {
            validDay = Integer.valueOf(ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_PAY,
                    "SVIP_VALID_DAY", "365").getConfigValue());
        }
        return validDay;
    }

}
