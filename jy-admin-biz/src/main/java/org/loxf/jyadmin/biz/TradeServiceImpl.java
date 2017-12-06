package org.loxf.jyadmin.biz;

import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.util.JedisUtil;
import org.loxf.jyadmin.client.service.TradeService;
import org.loxf.jyadmin.dal.dao.OrderMapper;
import org.loxf.jyadmin.dal.dao.TradeMapper;
import org.loxf.jyadmin.dal.po.Order;
import org.loxf.jyadmin.dal.po.Trade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("tradeService")
public class TradeServiceImpl implements TradeService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private TradeMapper tradeMapper;
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
            // TODO 支付成功,订单完成，开始交易
            if (orderAgain.getOrderType() == 1) {
                // 增加购买记录
            } else if (orderAgain.getOrderType() == 3) {
                // 增加/修改VIP INFO，延长VIP期限

            } else if (orderAgain.getOrderType() == 5) {
                // 增加活动名单信息
            }
        } else {
            return new BaseResult<>(BaseConstant.FAILED, "当前订单正在处理");
        }
        return null;
    }
}
