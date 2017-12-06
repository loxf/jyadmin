package org.loxf.jyadmin.biz;

import com.alibaba.fastjson.JSONObject;
import com.github.wxpay.sdk.WXPayUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.util.IdGenerator;
import org.loxf.jyadmin.base.util.weixin.WeixinUtil;
import org.loxf.jyadmin.biz.weixin.WeixinPayUtil;
import org.loxf.jyadmin.client.dto.OrderDto;
import org.loxf.jyadmin.client.service.OrderService;
import org.loxf.jyadmin.dal.dao.CustMapper;
import org.loxf.jyadmin.dal.dao.OrderMapper;
import org.loxf.jyadmin.dal.po.Cust;
import org.loxf.jyadmin.dal.po.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("orderService")
public class OrderServiceImpl implements OrderService {
    private static Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    private static String prefix = "ORD";
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private CustMapper custMapper;

    // TODO 超时订单处理

    @Override
    @Transactional
    public BaseResult<Map<String, String>> createOrder(OrderDto orderDto) {
        if (orderDto == null) {
            return new BaseResult(BaseConstant.FAILED, "参数为空");
        }
        if (StringUtils.isBlank(orderDto.getOrderName()) || orderDto.getOrderType() == null || StringUtils.isBlank(orderDto.getCustId())
                || orderDto.getOrderMoney() == null || StringUtils.isBlank(orderDto.getObjId()) || orderDto.getPayType() == null) {
            return new BaseResult(BaseConstant.FAILED, "关键参数缺失");
        }
        Order order = new Order();
        BeanUtils.copyProperties(orderDto, order);
        order.setOrderId(IdGenerator.generate(prefix));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("orderType", orderDto.getOrderType());
        if(orderDto.getPayType()==1) {
            // 微信支付
            try {
                BaseResult<OrderDto> orderDtoBaseResult = WeixinPayUtil.createOrder("", "", orderDto, jsonObject.toJSONString());
                if (orderDtoBaseResult.getCode() == BaseConstant.SUCCESS) {
                    if (orderMapper.insert(order) > 0) {
                        Map<String, String> result = new HashMap();
                        result.put("appId", BaseConstant.WX_APPID);
                        result.put("nonceStr", WeixinUtil.create_nonce_str());
                        result.put("package", "prepay_id=" + orderDtoBaseResult.getData().getOutTradeNo());
                        result.put("timeStamp", System.currentTimeMillis()+"");
                        String sign = WXPayUtil.generateSignature(result, BaseConstant.WX_EncodingAESKey);
                        result.put("paySign", sign);
                        result.put("signType", "MD5");
                        result.put("orderId", order.getOrderId());
                        return new BaseResult<>(result);
                    } else {
                        return new BaseResult<>(BaseConstant.FAILED, "新建订单失败");
                    }
                } else {
                    return new BaseResult<>(BaseConstant.FAILED, orderDtoBaseResult.getMsg());
                }
            } catch (Exception e) {
                logger.error("生成订单失败", e);
                return new BaseResult<>(BaseConstant.FAILED, "生成订单失败");
            }
        } else {
            if (orderMapper.insert(order) > 0) {
                Map<String, String> result = new HashMap();
                result.put("orderId", order.getOrderId());
                return new BaseResult<>(result);
            } else {
                return new BaseResult<>(BaseConstant.FAILED, "新建订单失败");
            }
        }
    }

    /**
     *
     * @param orderDto#payType 必填，支付方式 1：微信支付 3：余额支付
     * @param orderDto#orderType 必填，1 商品 3 VIP 5 活动
     * @return
     */
    @Override
    public BaseResult<String> completeOrder(OrderDto orderDto) {
        // 支付成功,订单完成
        if(orderDto.getOrderType()==1) {
            // 增加购买记录
        } else if(orderDto.getOrderType()==3) {
            // 增加/修改VIP INFO，延长VIP期限

        } else if(orderDto.getOrderType()==5) {
            // 增加活动名单信息
        }
        return null;
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
        if(StringUtils.isNotBlank(orderDto.getContact())){
            String emailReg = "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?";
            int isChinese = 1;// 发送方式 SMS
            if(orderDto.getContact().matches(emailReg)){
                isChinese = 2;// EMAIL
            }
            Cust cust = custMapper.selectByPhoneOrEmail(isChinese, orderDto.getContact());
            if(cust==null){
                return new PageResult(1, orderDto.getPager().getPage(), 0,null);
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
    public BaseResult hasBuy(String custId, int type, String obj) {
        if(type==1) {
            // 查看购买记录
        } else if(type==3) {
            // 查看VIP INFO

        } else if(type==5) {
            // 查看活动名单信息
        }
        return null;
    }
}
