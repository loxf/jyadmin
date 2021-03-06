package org.loxf.jyadmin.biz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.exception.BizException;
import org.loxf.jyadmin.base.util.DateUtils;
import org.loxf.jyadmin.base.util.JedisUtil;
import org.loxf.jyadmin.biz.util.ConfigUtil;
import org.loxf.jyadmin.biz.util.RandomUtils;
import org.loxf.jyadmin.biz.util.SendWeixinMsgUtil;
import org.loxf.jyadmin.client.service.AccountService;
import org.loxf.jyadmin.client.service.NoticeService;
import org.loxf.jyadmin.client.service.TradeService;
import org.loxf.jyadmin.dal.dao.*;
import org.loxf.jyadmin.dal.po.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

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
    private OfferMapper offerMapper;
    @Autowired
    private ActiveMapper activeMapper;
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
    @Autowired
    private AgentInfoMapper agentInfoMapper;
    @Value("#{configProperties['JYZX.INDEX.URL']}")
    private String JYZX_INDEX_URL;

    @Override
    @Transactional
    public BaseResult<String> completeTrade(String orderId, Integer status, String msg) {
        if (StringUtils.isBlank(orderId)) {
            return new BaseResult<>(BaseConstant.FAILED, "订单号为空");
        }
        if (status == null || (status.intValue() != 3 && status.intValue() != -3)) {
            return new BaseResult<>(BaseConstant.FAILED, "状态不正确");
        }
        String key = "COMPLETE_TRADE_" + orderId;
        if (jedisUtil.setnx(key, System.currentTimeMillis() + "", 60) > 0) {
            try {
                Order orderAgain = orderMapper.selectByOrderId(orderId);
                if (orderAgain == null) {
                    return new BaseResult<>(BaseConstant.FAILED, "订单不存在");
                }
                if (orderAgain.getStatus() != 3) {
                    return new BaseResult<>(BaseConstant.FAILED, "当前订单状态不正确");
                }
                Trade trade = tradeMapper.selectByOrderId(orderId);
                if (trade == null) {
                    return new BaseResult<>(BaseConstant.FAILED, "交易不存在");
                }
                if (trade.getState() != 1) {
                    return new BaseResult<>(BaseConstant.FAILED, "当前交易状态不正确");
                }
                // 查询客户信息
                Cust cust = custMapper.selectByCustId(orderAgain.getCustId());
                dealTrade(cust, cust.getRecommend(), orderId, status, msg, orderAgain);
                return new BaseResult<>();
            } catch (Exception e) {
                logger.error("交易失败" + orderId, e);
                throw new BizException(e);
            } finally {
                jedisUtil.del(key);
            }
        } else {
            return new BaseResult<>(BaseConstant.FAILED, "当前订单正在处理");
        }
    }

    public void dealTrade(Cust cust, String recommend, String orderId, int status, String msg, Order order) {
        Cust custFirst = null;
        Cust custSecond = null;
        if (StringUtils.isNotBlank(recommend)) {
            custFirst = custMapper.selectByCustId(recommend);
            if (custFirst != null) {
                if (StringUtils.isNotBlank(custFirst.getRecommend())) {
                    custSecond = custMapper.selectByCustId(custFirst.getRecommend());
                }
            }
        }
        if (status == 3) {
            String userName = cust.getNickName();
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
                if (custFirst != null && custFirst.getIsAgent() != null && custFirst.getIsAgent() > 0) {
                    // 推荐注册 如果是代理商及以上身份，检查是否有免费名额
                    firstScholarships = queryFirstCustAgentScholarship(custFirst.getCustId(), vipType);
                }
                if (StringUtils.isBlank(firstScholarships)) {
                    // 非代理免费名额走正常逻辑
                    if (custFirst != null) {
                        firstScholarships = queryScholarshipsRate(custFirst, "STUDENT", 1);
                    }
                    if (custSecond != null) {
                        secondScholarships = queryScholarshipsRate(custSecond, "STUDENT", 2);
                    }
                }
                detailName += "升级" + vipType;
                // 处理VIP_INFO CUST_INFO
                dealVip(order.getCustId(), vipType);
                // 发送VIP通知
                SendWeixinMsgUtil.sendBeVipNotice(cust.getOpenid(), cust.getNickName(), vipType, JYZX_INDEX_URL);
            } else if (order.getOrderType() == 5) {
                detailName += "参加活动";
                // 增加活动名单信息
                Active active = activeMapper.selectByActiveId(order.getObjId());
                ActiveCustList activeCustList = dealActive(order.getCustId(), order.getObjId(), order.getOrderName(), orderId);
                // 发送活动报名成功消息
                SendWeixinMsgUtil.sendActiveInNotice(cust.getOpenid(), cust.getNickName(), active.getActiveName(),
                        DateUtils.formatHms(active.getActiveStartTime()) + " ~ " + DateUtils.formatHms(active.getActiveEndTime()),
                        activeCustList.getActiveTicketNo(), active.getAddr(), String.format(JYZX_INDEX_URL + BaseConstant.ACTIVE_DETAIL_URL, active.getActiveId()));
                if (custFirst != null) {
                    firstScholarships = queryScholarshipsRate(custFirst, "ACTIVE", 1);
                }
                if (custSecond != null) {
                    secondScholarships = queryScholarshipsRate(custSecond, "ACTIVE", 2);
                }
            } else {
                detailName += "购买课程";
                Offer offer = offerMapper.selectByOfferId(order.getObjId());
                String url = "";
                if (offer.getOfferType().equals("CLASS")) {
                    url = String.format(JYZX_INDEX_URL + BaseConstant.CLASS_DETAIL_URL, order.getObjId());
                } else if (offer.getOfferType().equals("OFFER")) {
                    url = String.format(JYZX_INDEX_URL + BaseConstant.OFFER_DETAIL_URL, order.getObjId());
                    // 查询套餐是否有特殊分成
                    String metaData = offer.getMetaData();
                    if(StringUtils.isNotBlank(metaData)){
                        JSONObject metaDataJson = JSON.parseObject(metaData);
                        if(metaDataJson.containsKey("COMMISSION")){
                            // {"MAXBP":"1","COMMISSION":{"NONE":"10","VIP":"12"}}
                            JSONObject commissionJson = metaDataJson.getJSONObject("COMMISSION");
                            if (StringUtils.isBlank(firstScholarships) && custFirst != null) {
                                firstScholarships = queryOfferScholarshipRate(true, commissionJson, custFirst.getIsAgent(), custFirst.getUserLevel());
                            } else {
                                firstScholarships = "0";
                            }
                            if (StringUtils.isBlank(secondScholarships) && custSecond != null) {
                                secondScholarships = queryOfferScholarshipRate(false, commissionJson, custSecond.getIsAgent(), custSecond.getUserLevel());
                            } else {
                                secondScholarships = "0";
                            }
                        }
                    }
                    // 处理套餐内的VIP 或者 ACTIVE
                    List<Offer> offerRelList = offerMapper.showOfferByOfferIdAndRelType(order.getObjId(), "OFFER");
                    for (Offer relOffer : offerRelList){
                        if(relOffer.getOfferType().equals("VIP")){
                            String vipType = relOffer.getOfferId().equals("OFFER001") ? "VIP" : "SVIP";
                            dealVip(order.getCustId(), vipType);
                            // 发送VIP通知
                            SendWeixinMsgUtil.sendBeVipNotice(cust.getOpenid(), cust.getNickName(), vipType, JYZX_INDEX_URL);
                        } else if(relOffer.getOfferType().equals("ACTIVE")){
                            Active active = activeMapper.selectByActiveId(relOffer.getOfferId());
                            ActiveCustList activeCustList = dealActive(order.getCustId(), active.getActiveId(), active.getActiveName(), orderId);
                            // 发送活动报名成功消息
                            SendWeixinMsgUtil.sendActiveInNotice(cust.getOpenid(), cust.getNickName(), relOffer.getOfferName(),
                                    DateUtils.formatHms(active.getActiveStartTime()) + " ~ " + DateUtils.formatHms(active.getActiveEndTime()),
                                    activeCustList.getActiveTicketNo(), active.getAddr(), String.format(JYZX_INDEX_URL +
                                            BaseConstant.ACTIVE_DETAIL_URL, active.getActiveId()));
                        }
                    }
                }
                //
                if (StringUtils.isBlank(firstScholarships) && custFirst != null) {
                    firstScholarships = queryScholarshipsRate(custFirst, "OFFER", 1);
                }
                if (StringUtils.isBlank(secondScholarships) && custSecond != null) {
                    secondScholarships = queryScholarshipsRate(custSecond, "OFFER", 2);
                }
                // 购买课程通知
                SendWeixinMsgUtil.sendBuyOfferNotice(cust.getOpenid(), cust.getNickName(), offer.getOfferName(), url);
            }
            // 分成计算 代理商分成 如果是代理商，先检查是否有免费名额，如果有先用免费名额
            BigDecimal companyAmount = order.getOrderMoney();
            BigDecimal scholarship = BigDecimal.ZERO;
            if (StringUtils.isNotBlank(firstScholarships) && custFirst!=null) {
                // 模板消息接口 发送通知
                BigDecimal first = dealScholarship(firstScholarships, order.getOrderMoney(), custFirst.getCustId(), orderId,
                        userName + detailName + "(1级奖)", cust.getCustId());
                if(first.compareTo(BigDecimal.ZERO)>0) {
                    companyAmount = companyAmount.subtract(first);
                    scholarship = scholarship.add(first);
                    SendWeixinMsgUtil.sendScholarshipMsg(custFirst.getOpenid(), first.toPlainString(), custFirst.getNickName(), JYZX_INDEX_URL);
                }
            }
            if (StringUtils.isNotBlank(secondScholarships) && custSecond != null && companyAmount.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal second = dealScholarship(secondScholarships, order.getOrderMoney(), custSecond.getCustId(), orderId,
                        userName + detailName + "(2级奖)", cust.getCustId());
                if(second.compareTo(BigDecimal.ZERO)>0) {
                    companyAmount = companyAmount.subtract(second);
                    scholarship = scholarship.add(second);
                    SendWeixinMsgUtil.sendScholarshipMsg(custSecond.getOpenid(), second.toPlainString(), custSecond.getNickName(), JYZX_INDEX_URL);
                }
            }
            // 计算公司收入
            companyIncomeMapper.insert(createCompanyIncome(cust.getCustId(), userName, detailName + ":" + order.getOrderName(),
                    companyAmount, scholarship, (custFirst == null ? null : custFirst.getCustId()), orderId, order.getOrderType()));
        }
        tradeMapper.updateByOrderId(orderId, status, msg);
    }

    private String queryOfferScholarshipRate(boolean isFirst, JSONObject commissionJson, int agentLv, String userLevel){
        String prefix = isFirst?"FIRST":"SECOND";
        if(agentLv>0){
            String agentLevel = agentLv==1?"AGENT": (agentLv==2?"PARTNER":"COMPANY");
            return commissionJson.getString(prefix + "_" + agentLevel);
        } else {
            return commissionJson.getString(prefix + "_" + userLevel);
        }
    }

    private String queryFirstCustAgentScholarship(String custId, String vipType) {
        // 推荐注册 如果是代理商及以上身份，检查是否有免费名额
        AgentInfo agentInfo = agentInfoMapper.selectByCustId(custId);
        if (agentInfo.getStatus() == 1) {
            String metaDataStr = agentInfo.getMetaData();
            if (StringUtils.isNotBlank(metaDataStr)) {
                JSONObject jsonObject = JSONObject.parseObject(metaDataStr);
                if (jsonObject.containsKey("total" + vipType)) {
                    int totalNbr = jsonObject.getIntValue("total" + vipType);
                    int useNbr = jsonObject.getIntValue("use" + vipType);
                    if (totalNbr - useNbr > 0) {
                        // 使用免费名额，奖学金比例100%
                        jsonObject.put("useVIP", ++useNbr);
                        AgentInfo newAgent = new AgentInfo();
                        newAgent.setMetaData(jsonObject.toJSONString());
                        newAgent.setCustId(custId);
                        agentInfoMapper.updateByCustId(newAgent);
                        return "100";
                    }
                }
            }
        }
        return null;
    }

    private BigDecimal dealScholarship(String scholarships, BigDecimal orderMoney, String custId, String orderId, String detailName, String sourceCustId) {
        BigDecimal first = orderMoney.multiply(new BigDecimal(scholarships)).
                divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
        if(first.compareTo(BigDecimal.ZERO)>0) {
            BaseResult<Boolean> baseResult = accountService.increase(custId, first, null, orderId,
                    detailName, sourceCustId);
            if (baseResult.getCode() == BaseConstant.FAILED) {
                throw new BizException(baseResult.getMsg());
            }
        }
        return first;
    }

    private CompanyIncome createCompanyIncome(String custId, String custName, String detailName, BigDecimal companyAmount,
                                              BigDecimal scholarship, String beneficiary, String source, int type) {
        CompanyIncome companyIncome = new CompanyIncome();
        companyIncome.setCustId(custId);
        companyIncome.setCustName(custName);
        companyIncome.setAmount(companyAmount);
        companyIncome.setScholarship(scholarship);
        companyIncome.setDetailName(detailName);
        companyIncome.setBeneficiary(beneficiary);
        companyIncome.setSource(source);
        companyIncome.setType(type);
        return companyIncome;
    }

    /**
     * @param cust
     * @param type  STUDENT（VIP），OFFER（课程，套餐），ACTIVE（活动）
     * @param first 1:直接 2:间接
     * @return
     */
    private String queryScholarshipsRate(Cust cust, String type, int first) {
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
            firstAndSecondScholarships = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_PAY,
                    cust.getUserLevel() + "_FIRST_" + type, "30,5").getConfigValue();
        }
        if (StringUtils.isNotBlank(firstAndSecondScholarships)) {
            String[] tmp = firstAndSecondScholarships.split(",");
            if (first > tmp.length) {
                return "";
            }
            return tmp[first - 1];
        }
        return "";
    }

    public ActiveCustList dealActive(String custId, String activeId, String activeName, String orderId) {
        ActiveCustList activeCustList = new ActiveCustList();
        activeCustList.setActiveTicketNo(DateUtils.format(new Date(), "yyMMddHHmmss") + RandomUtils.getRandomStr(4));
        activeCustList.setActiveId(activeId);
        activeCustList.setCustId(custId);
        activeCustList.setActiveName(activeName);
        List<OrderAttr> orderAttrs = orderAttrMapper.selectByOrderId(orderId);
        if(CollectionUtils.isNotEmpty(orderAttrs)) {
            for (OrderAttr attr : orderAttrs) {
                if (attr.getAttrCode().equals("ACTIVITY_USER_NAME")) {
                    activeCustList.setName(attr.getAttrValue());
                } else if (attr.getAttrCode().equals("ACTIVITY_CONTACT")) {
                    activeCustList.setPhone(attr.getAttrValue());
                }
            }
        }
        activeCustList.setStatus(1);// 已付款
        activeCustListMapper.insert(activeCustList);
        return activeCustList;
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
                updateVip.setType(vipType);
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
        cust.setCustId(custId);
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
