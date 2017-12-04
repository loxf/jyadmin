package org.loxf.jyadmin.biz.weixin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.wxpay.sdk.WXPay;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.exception.BizException;
import org.loxf.jyadmin.base.util.DateUtils;
import org.loxf.jyadmin.base.util.weixin.WeixinPayConfig;
import org.loxf.jyadmin.client.dto.OrderDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WeixinPayUtil {
    private static Logger logger = LoggerFactory.getLogger(WeixinPayUtil.class);
    private static String bodyPrefix = "静怡雅学馆";

    /**
     * @param openid
     * @param ip
     * @param orderDto
     * @param attach  JSON透传
     * @throws Exception
     */
    public static BaseResult<OrderDto> createOrder(String openid, String ip, OrderDto orderDto, String attach) throws Exception {
        WeixinPayConfig config = new WeixinPayConfig();
        WXPay wxpay = new WXPay(config);

        Map<String, String> data = new HashMap<String, String>();
        data.put("openid", openid);
        data.put("body", bodyPrefix + "-" + orderDto.getOrderName());
        data.put("out_trade_no", orderDto.getOrderId());
        data.put("device_info", "WX");// 微信支付
        data.put("fee_type", "CNY");
        data.put("total_fee", orderDto.getOrderMoney().multiply(new BigDecimal(100)).toPlainString());
        data.put("spbill_create_ip", ip);
        data.put("notify_url", "http://www.example.com/wxpay/notify");
        data.put("trade_type", "JSAPI");  // JSAPI--公众号支付、NATIVE--原生扫码支付、APP--app支付，统一下单接口trade_type的传参可参考这里
        data.put("product_id", orderDto.getObjId());
        data.put("time_start", DateUtils.format(orderDto.getCreatedAt(), "yyyyMMddHHmmss"));
        Date expire = orderDto.getCreatedAt();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(expire);
        calendar.add(Calendar.MINUTE, 15);
        data.put("time_expire", DateUtils.format(calendar.getTime()));
        JSONObject attachJson = null;
        if(StringUtils.isBlank(attach)){
            attachJson = new JSONObject();
        } else {
            attachJson = JSON.parseObject(attach);
        }
        attachJson.put("order_type", orderDto.getOrderType());
        data.put("attach", attachJson.toJSONString());

        try {
            Map<String, String> resp = wxpay.unifiedOrder(data);
            logger.info("微信支付下单：{}", JSON.toJSONString(resp));
            if(resp.get("return_code").equals("SUCCESS")){
                if(resp.get("result_code").equals("SUCCESS")){
                    orderDto.setOutTradeNo(resp.get("prepay_id"));
                    return new BaseResult(orderDto);
                } else {
                    logger.error("微信下单失败：" + resp.get("err_code") + "，" + resp.get("err_code_des"));
                    return new BaseResult(BaseConstant.FAILED, resp.get("err_code") + resp.get("err_code_des"));
                }
            } else {
                logger.error("微信下单失败,", resp.get("return_msg"));
                return new BaseResult(BaseConstant.FAILED, resp.get("return_msg"));
            }
        } catch (Exception e) {
            logger.error("微信下单失败：", e);
            throw new BizException("微信下单失败", e);
        }
    }

    public static void queryOrder(String wxOrderId) throws Exception {
        WeixinPayConfig config = new WeixinPayConfig();
        WXPay wxpay = new WXPay(config);

        Map<String, String> data = new HashMap<String, String>();
        data.put("out_trade_no", wxOrderId);

        try {
            Map<String, String> resp = wxpay.orderQuery(data);
            System.out.println(resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
