package org.loxf.jyadmin.biz;

import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.util.DateUtils;
import org.loxf.jyadmin.base.util.JedisUtil;
import org.loxf.jyadmin.biz.util.ConfigUtil;
import org.loxf.jyadmin.biz.util.RandomUtils;
import org.loxf.jyadmin.client.service.TradeService;
import org.loxf.jyadmin.dal.dao.*;
import org.loxf.jyadmin.dal.po.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service("tradeService")
public class TradeServiceImpl implements TradeService {
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
    private JedisUtil jedisUtil;

    @Override
    public BaseResult<String> completeTrade(String orderId, Integer status, String msg) {
        if(StringUtils.isBlank(orderId)){
            return new BaseResult<>(BaseConstant.FAILED, "订单号为空");
        }
        if(status==null||(status.intValue()!=3&&status.intValue()!=-3)){
            return new BaseResult<>(BaseConstant.FAILED, "状态不正确");
        }
        String key = "COMPLETE_TRADE_" + orderId;
        if(jedisUtil.setnx(key, "true", 60)>0){
            Order orderAgain = orderMapper.selectByOrderId(orderId);
            if (orderAgain == null) {
                return new BaseResult<>(BaseConstant.FAILED, "订单不存在");
            }
            if (orderAgain.getStatus().intValue() != 3) {
                return new BaseResult<>(BaseConstant.FAILED, "当前订单状态不正确");
            }
            Trade trade = tradeMapper.selectByOrderId(orderId);
            if(trade==null){
                return new BaseResult<>(BaseConstant.FAILED, "交易不存在");
            }
            if (trade.getState().intValue() != 1) {
                return new BaseResult<>(BaseConstant.FAILED, "当前交易状态不正确");
            }
            dealTrade(orderId, status, msg, orderAgain);
            return new BaseResult<>();
        } else {
            return new BaseResult<>(BaseConstant.FAILED, "当前订单正在处理");
        }
    }

    @Transactional
    public void dealTrade(String orderId, int status, String msg, Order order){
        if(status==3) {
            // 增加购买记录
            PurchasedInfo purchasedInfo = new PurchasedInfo();
            purchasedInfo.setCustId(order.getCustId());
            purchasedInfo.setNickName(order.getCustName());
            purchasedInfo.setOfferId(order.getObjId());
            purchasedInfo.setType(order.getOrderType());
            purchasedInfo.setOrderId(orderId);
            purchasedInfoMapper.insert(purchasedInfo);
            // 支付成功,订单完成，开始交易
            if (order.getOrderType() == 3) {
                dealVip(order.getCustId(), order.getObjId().equals("OFFER001")?"VIP":"SVIP");
            } else if (order.getOrderType() == 5) {
                // 增加活动名单信息
                dealActive(order);
            }
            // TODO 分成计算
        }
        tradeMapper.updateByOrderId(orderId, status, msg);
    }

    private void dealActive(Order order){
        ActiveCustList activeCustList = new ActiveCustList();
        activeCustList.setActiveTicketNo(DateUtils.format(new Date(), "yyMMddHHmmss") + RandomUtils.getRandomStr(4));
        activeCustList.setActiveId(order.getObjId());
        activeCustList.setCustId(order.getCustId());
        activeCustList.setActiveName(order.getOrderName());
        List<OrderAttr> orderAttrs = orderAttrMapper.selectByOrderId(order.getOrderId());
        for(OrderAttr attr : orderAttrs){
            if(attr.getAttrCode().equals("CUST_NAME")){
                activeCustList.setName(attr.getAttrValue());
            } else if(attr.getAttrCode().equals("CUST_PHONE")){
                activeCustList.setPhone(attr.getAttrValue());
            }
        }
        activeCustList.setStatus(1);// 已付款
        activeCustListMapper.insert(activeCustList);
    }

    private void dealVip(String custId, String vipType){
        VipInfo vipInfo = vipInfoMapper.selectByCustId(custId);
        if(vipInfo==null){
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
            if(vipInfo.getStatus()==3){
                updateVip.setStatus(1);
                Date now = new Date();
                updateVip.setEffDate(now);
                Date end = DateUtils.getAddTime(now, Calendar.DATE, getValidDay(vipType));
                updateVip.setExpDate(DateUtils.getEndDate(end));
                vipInfoMapper.updateByCustId(updateVip);
            } else {
                if(vipInfo.getType().equals("VIP")&&vipType.equals("SVIP")){
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
    }

    private int getValidDay(String vipType){
        int validDay = 0;
        if(vipType.equals("VIP")) {
            validDay = Integer.valueOf(ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_PAY,
                    "VIP_VALID_DAY", "365").getConfigValue());
        } else {
            validDay = Integer.valueOf(ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_PAY,
                    "SVIP_VALID_DAY", "365").getConfigValue());
        }
        return validDay;
    }

}
