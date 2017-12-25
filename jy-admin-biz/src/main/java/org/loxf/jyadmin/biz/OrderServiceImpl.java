package org.loxf.jyadmin.biz;

import com.alibaba.fastjson.JSONObject;
import com.github.wxpay.sdk.WXPayUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.exception.BizException;
import org.loxf.jyadmin.base.util.DateUtils;
import org.loxf.jyadmin.base.util.IdGenerator;
import org.loxf.jyadmin.base.util.JedisUtil;
import org.loxf.jyadmin.base.util.weixin.WeixinUtil;
import org.loxf.jyadmin.biz.util.BizUtil;
import org.loxf.jyadmin.biz.util.ConfigUtil;
import org.loxf.jyadmin.biz.weixin.WeixinPayUtil;
import org.loxf.jyadmin.client.dto.OrderAttrDto;
import org.loxf.jyadmin.client.dto.OrderDto;
import org.loxf.jyadmin.client.dto.VipInfoDto;
import org.loxf.jyadmin.client.service.OrderService;
import org.loxf.jyadmin.client.service.VipInfoService;
import org.loxf.jyadmin.client.tmp.OrderInfoUpload;
import org.loxf.jyadmin.dal.dao.*;
import org.loxf.jyadmin.dal.po.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service("orderService")
public class OrderServiceImpl implements OrderService {
    private static Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    private static String prefix = "ORD";
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OfferMapper offerMapper;
    @Autowired
    private ActiveMapper activeMapper;
    @Autowired
    private CustMapper custMapper;
    @Autowired
    private OrderAttrMapper orderAttrMapper;
    @Autowired
    private TradeMapper tradeMapper;
    @Autowired
    private PurchasedInfoMapper purchasedInfoMapper;
    @Autowired
    private VipInfoService vipInfoService;
    @Autowired
    private AccountDetailMapper accountDetailMapper;
    @Autowired
    private JedisUtil jedisUtil;

    @Override
    @Transactional
    public BaseResult createOldOrder(List<OrderInfoUpload> orderInfoUploadList) {
        List<Order> orders = new ArrayList<>();
        List<AccountDetail> accountDetails = new ArrayList<>();
        List<PurchasedInfo> purchasedInfos = new ArrayList<>();
        Map<String, String> phoneMapCustId = new HashMap();
        Map<String, String> custIdAndNameMap = new HashMap();
        Map<String, String> nameAndObjIdMap = new HashMap();
        for (OrderInfoUpload orderInfoUpload : orderInfoUploadList) {
            Order order = new Order();
            String orderId = orderInfoUpload.getOrderId().replaceAll("'", "");
            order.setOrderId(orderId);
            order.setBp(new BigDecimal(orderInfoUpload.getBp()).multiply(BigDecimal.TEN));
            // 查询custId
            String custId = "";
            String nickname = "";
            if (phoneMapCustId.containsKey(orderInfoUpload.getPhone())) {
                custId = phoneMapCustId.get(orderInfoUpload.getPhone());
                nickname = custIdAndNameMap.get(custId);
            } else {
                Cust cust = custMapper.selectByPhoneOrEmail(1, orderInfoUpload.getPhone());
                if (cust == null) {
                    continue;
                } else {
                    custId = cust.getCustId();
                    phoneMapCustId.put(orderInfoUpload.getPhone(), custId);
                    nickname = cust.getNickName();
                    custIdAndNameMap.put(custId, nickname);
                }
            }
            order.setCustId(custId);
            order.setCustName(nickname);
            order.setOrderName(orderInfoUpload.getOrderName());
            if (orderInfoUpload.getPayType().equals("微信支付")) {
                order.setPayType(1);
            } else {
                order.setPayType(2);
            }
            if (orderInfoUpload.getOrderType().equalsIgnoreCase("vip")) {
                // VIP
                order.setOrderType(3);
                order.setObjId("OFFER001");
            } else if (orderInfoUpload.getOrderType().equalsIgnoreCase("activity")) {
                // 活动
                order.setOrderType(5);
                if (nameAndObjIdMap.containsKey(orderInfoUpload.getOrderName())) {
                    order.setObjId(nameAndObjIdMap.get(orderInfoUpload.getOrderName()));
                } else {
                    Active active = activeMapper.selectByActiveName(orderInfoUpload.getOrderName());
                    if (active != null) {
                        order.setObjId(active.getActiveId());
                        nameAndObjIdMap.put(orderInfoUpload.getOrderName(), active.getActiveId());
                    }
                }
            } else {
                // 商品
                order.setOrderType(1);
                if (nameAndObjIdMap.containsKey(orderInfoUpload.getOrderName())) {
                    order.setObjId(nameAndObjIdMap.get(orderInfoUpload.getOrderName()));
                } else {
                    Offer offer = offerMapper.selectOfferByName(orderInfoUpload.getOrderName());
                    if (offer != null) {
                        order.setObjId(offer.getOfferId());
                        nameAndObjIdMap.put(orderInfoUpload.getOrderName(), offer.getOfferId());
                    }
                }
            }
            order.setOrderMoney(new BigDecimal(orderInfoUpload.getMoney()));
            order.setTotalMoney(order.getOrderMoney().add(new BigDecimal(orderInfoUpload.getBp())));
            order.setDiscount(10L);
            if (orderInfoUpload.getStatus().equals("已付款")) {
                order.setStatus(3);
            } else if (orderInfoUpload.getStatus().equals("未支付")) {
                order.setStatus(9);
            }
            order.setCreatedAt(DateUtils.toDate(orderInfoUpload.getCreatedTime(), "yyyy-MM-dd HH:mm:ss"));
            orders.add(order);
            if (StringUtils.isNotBlank(order.getObjId())) {
                PurchasedInfo purchasedInfo = new PurchasedInfo();
                purchasedInfo.setOfferId(order.getObjId());
                purchasedInfo.setType(order.getOrderType());
                purchasedInfo.setCustId(order.getCustId());
                purchasedInfo.setOrderId(order.getOrderId());
                purchasedInfo.setNickName(order.getCustName());
                purchasedInfo.setCreatedAt(DateUtils.toDate(orderInfoUpload.getPayTime(), "yyyy-MM-dd HH:mm:ss"));
                purchasedInfos.add(purchasedInfo);
            }
            if (order.getStatus() == 3) {
                AccountDetail accountDetail = new AccountDetail();
                accountDetail.setCustId(order.getCustId());
                accountDetail.setOrderId(order.getOrderId());
                accountDetail.setDetailName(order.getOrderName());
                accountDetail.setType(3);
                accountDetail.setChangeBalance(order.getOrderMoney());
                accountDetail.setCreatedAt(order.getCreatedAt());
                accountDetails.add(accountDetail);
            }
        }
        if (CollectionUtils.isNotEmpty(orders)) {
            orderMapper.insertList(orders);
        }
        if (CollectionUtils.isNotEmpty(accountDetails)) {
            accountDetailMapper.insertList(accountDetails);
        }
        if (CollectionUtils.isNotEmpty(purchasedInfos)) {
            purchasedInfoMapper.insertList(purchasedInfos);
        }
        return new BaseResult();
    }

    @Override
    @Transactional
    public BaseResult<Map<String, String>> createOrder(String openid, String ip, OrderDto orderDto, List<OrderAttrDto> orderAttrDtoList) {
        if (orderDto == null) {
            return new BaseResult(BaseConstant.FAILED, "参数为空");
        }
        if (StringUtils.isBlank(orderDto.getOrderName()) || orderDto.getOrderType() == null || StringUtils.isBlank(orderDto.getCustId())
                || orderDto.getOrderMoney() == null || StringUtils.isBlank(orderDto.getObjId()) || orderDto.getPayType() == null) {
            return new BaseResult(BaseConstant.FAILED, "关键参数缺失");
        }
        String lockKey = "CREATE_ORDER_" + orderDto.getCustId();
        boolean lock = false;
        if (jedisUtil.setnx(lockKey, System.currentTimeMillis() + "", 60) > 0) {
            lock = true;
        } else {
            String oldTime = jedisUtil.get(lockKey);
            if(System.currentTimeMillis() - Long.valueOf(oldTime)>60*1000){
                lock = true;
            }
        }
        if(lock) {
            orderDto.setOrderId(IdGenerator.generate(prefix));
            Order order = new Order();
            BeanUtils.copyProperties(orderDto, order);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("orderType", orderDto.getOrderType());
            try {
                if (orderDto.getPayType() == 1) {
                    // 微信支付
                    BaseResult<OrderDto> orderDtoBaseResult = WeixinPayUtil.createOrder(openid, ip, orderDto, jsonObject.toJSONString());
                    if (orderDtoBaseResult.getCode() == BaseConstant.SUCCESS) {
                        if (orderMapper.insert(order) > 0) {
                            // 插入订单属性
                            insertAttr(order.getOrderId(), orderAttrDtoList);
                            // 获取微信返回
                            Map result = createWxResult(orderDtoBaseResult.getData().getOutTradeNo());
                            result.put("orderId", order.getOrderId());
                            return new BaseResult<>(result);
                        } else {
                            return new BaseResult<>(BaseConstant.FAILED, "新建订单失败");
                        }
                    } else {
                        return new BaseResult<>(BaseConstant.FAILED, orderDtoBaseResult.getMsg());
                    }
                } else {
                    if (orderMapper.insert(order) > 0) {
                        // 插入订单属性
                        insertAttr(order.getOrderId(), orderAttrDtoList);

                        Map<String, String> result = new HashMap();
                        result.put("orderId", order.getOrderId());
                        return new BaseResult<>(result);
                    } else {
                        return new BaseResult<>(BaseConstant.FAILED, "新建订单失败");
                    }
                }
            } catch (Exception e) {
                logger.error("生成订单失败", e);
                return new BaseResult<>(BaseConstant.FAILED, "生成订单失败");
            } finally {
                jedisUtil.del(lockKey);
            }
        } else {
            return new BaseResult<>(BaseConstant.FAILED, "不能重复提交订单");
        }
    }

    private void insertAttr(String orderId, List<OrderAttrDto> orderAttrDtoList) {
        if (CollectionUtils.isNotEmpty(orderAttrDtoList)) {
            List<OrderAttr> attrList = new ArrayList<>();
            for (OrderAttrDto orderAttrDto : orderAttrDtoList) {
                OrderAttr orderAttr = new OrderAttr();
                BeanUtils.copyProperties(orderAttrDto, orderAttr);
                attrList.add(orderAttr);
            }
            orderAttrMapper.insertList(attrList, orderId);
        }
    }

    private Map createWxResult(String prepay_id) throws Exception {
        Map<String, String> result = new HashMap();
        String appId = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "WX_APPID").getConfigValue();
        String key = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "WX_MCH_KEY").getConfigValue();
        result.put("appId", appId);
        result.put("nonceStr", WXPayUtil.generateNonceStr());
        result.put("package", "prepay_id=" + prepay_id);
        result.put("timeStamp", (System.currentTimeMillis() / 1000) + "");
        result.put("signType", "MD5");
        String sign = WXPayUtil.generateSignature(result, key);
        result.put("paySign", sign);
        return result;
    }

    /**
     * @param orderId
     * @param status  3:完成订单， -3:订单完成失败
     * @return
     */
    @Override
    public BaseResult<String> completeOrder(String orderId, String partnerOrderId, Integer status, String msg) {
        if (StringUtils.isBlank(orderId)) {
            return new BaseResult<>(BaseConstant.FAILED, "订单号为空");
        }
        if (status == null || (status.intValue() != 3 && status.intValue() != -3)) {
            return new BaseResult<>(BaseConstant.FAILED, "状态不正确");
        }
        String key = "COMPLETE_ORDER_" + orderId;
        boolean lock = false;
        if (jedisUtil.setnx(key, System.currentTimeMillis() + "", 60) > 0) {
            lock = true;
        } else {
            String oldTime = jedisUtil.get(key);
            if(System.currentTimeMillis() - Long.valueOf(oldTime)>60*1000){
                lock = true;
            }
        }
        if(lock){
            try {
                Order orderAgain = orderMapper.selectByOrderId(orderId);
                if (orderAgain == null) {
                    return new BaseResult<>(BaseConstant.FAILED, "订单不存在");
                }
                if (orderAgain.getStatus() != 1) {
                    return new BaseResult<>(BaseConstant.FAILED, "当前订单状态不正确");
                }
                dealCompleteOrder(orderId, partnerOrderId, status, msg);
                return new BaseResult();
            } catch (Exception e) {
                logger.error("完成订单失败：", e);
                throw new BizException("完成订单失败");
            } finally {
                jedisUtil.del(key);
            }
        } else {
            return new BaseResult<>(BaseConstant.FAILED, "当前订单正在处理");
        }
    }

    @Transactional
    public void dealCompleteOrder(String orderId, String partnerOrderId, Integer status, String msg) {
        // 更新订单
        orderMapper.updateByOrderId(orderId, partnerOrderId, status, msg);
        // 如果是成功完成订单，触发交易
        if (status == 3) {
            Trade trade = new Trade();
            trade.setOrderId(orderId);
            trade.setState(1);
            tradeMapper.insert(trade);
        }
    }

    @Override
    public BaseResult<OrderDto> queryOrder(String orderId) {
        Order order = orderMapper.selectByOrderId(orderId);
        if (order == null) {
            return new BaseResult<>(BaseConstant.FAILED, "订单不存在");
        }
        OrderDto orderDto = new OrderDto();
        BeanUtils.copyProperties(order, orderDto);
        return new BaseResult(orderDto);
    }

    @Override
    public PageResult<OrderDto> pager(OrderDto orderDto) {
        if (orderDto == null) {
            return new PageResult(BaseConstant.FAILED, "参数为空");
        }
        // 联系方式处理
        if (StringUtils.isNotBlank(orderDto.getContact())) {
            String emailReg = "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?";
            int isChinese = 1;// 发送方式 SMS
            if (orderDto.getContact().matches(emailReg)) {
                isChinese = 2;// EMAIL
            }
            Cust cust = custMapper.selectByPhoneOrEmail(isChinese, orderDto.getContact());
            if (cust == null) {
                return new PageResult(1, orderDto.getPager().getPage(), 0, null);
            }
            orderDto.setCustId(cust.getCustId());
        }
        Order order = new Order();
        BeanUtils.copyProperties(orderDto, order);

        int count = orderMapper.count(order);
        List<OrderDto> result = null;
        if (count > 0) {
            List<Order> list = orderMapper.list(order);
            if (CollectionUtils.isNotEmpty(list)) {
                result = new ArrayList<>();
                for (Order tmp : list) {
                    OrderDto dto = new OrderDto();
                    BeanUtils.copyProperties(tmp, dto);
                    result.add(dto);
                }
            }
        }
        int page = count / orderDto.getPager().getSize() + (count % orderDto.getPager().getSize() > 0 ? 1 : 0);
        return new PageResult<>(page, orderDto.getPager().getPage(), count, result);
    }

    @Override
    public BaseResult<Boolean> hasBuy(String custId, int type, String obj) {
        if (type == 1 || type == 5) {
            // 查看购买记录
            PurchasedInfo purchasedInfo = new PurchasedInfo();
            purchasedInfo.setCustId(custId);
            purchasedInfo.setType(type);
            purchasedInfo.setOfferId(obj);
            if (purchasedInfoMapper.count(purchasedInfo) > 0) {
                return new BaseResult<>(BaseConstant.FAILED, "已购买商品");
            } else {
                return new BaseResult<>(true);
            }
        } else {
            if (obj.equals("OFFER001")) {
                // 如果是SVIP还在有效期，不能购买VIP。其他都可以
                BaseResult<VipInfoDto> baseResultVip = vipInfoService.queryVipInfo(custId);
                if (baseResultVip.getCode() == BaseConstant.SUCCESS) {
                    VipInfoDto vipInfoDto = baseResultVip.getData();
                    Date now = new Date();
                    if (vipInfoDto.getType().equals("SVIP") && vipInfoDto.getStatus() == 1 &&
                            vipInfoDto.getEffDate().before(now) && vipInfoDto.getExpDate().after(now)) {
                        return new BaseResult<>(BaseConstant.FAILED, "SVIP使用中，不能购买VIP");
                    }
                }
            }
        }
        return new BaseResult(BaseConstant.SUCCESS, "无需校验");
    }

    @Override
    public BaseResult queryOrderIncrease() {
        List<Map> list = orderMapper.queryOrderDistributeByLast7Day();
        return new BaseResult(BizUtil.getDataByDate(list));
    }
}
