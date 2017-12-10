package org.loxf.jyadmin.biz.weixin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.exception.BizException;
import org.loxf.jyadmin.base.util.DateUtils;
import org.loxf.jyadmin.base.util.HttpsUtil;
import org.loxf.jyadmin.base.util.weixin.WeixinPayConfig;
import org.loxf.jyadmin.base.util.weixin.WeixinUtil;
import org.loxf.jyadmin.biz.util.ConfigUtil;
import org.loxf.jyadmin.client.dto.OrderDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.security.KeyStore;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WeixinPayUtil {
    private static Logger logger = LoggerFactory.getLogger(WeixinPayUtil.class);

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
        data.put("body", BaseConstant.WX_BODYPREFIX + "-" + orderDto.getOrderName());// 线上电商，商家名称必须为实际销售商品的商家
        data.put("out_trade_no", orderDto.getOrderId());
        data.put("device_info", "WX");// 微信支付
        data.put("fee_type", "CNY");
        data.put("total_fee", orderDto.getOrderMoney().multiply(new BigDecimal(100)).longValue()+"");
        data.put("spbill_create_ip", ip);
        data.put("notify_url", BaseConstant.WX_PAY_CALLBACK);
        data.put("trade_type", "JSAPI");  // JSAPI--公众号支付、NATIVE--原生扫码支付、APP--app支付，统一下单接口trade_type的传参可参考这里
        data.put("product_id", orderDto.getObjId());
        data.put("time_start", DateUtils.format(new Date(), "yyyyMMddHHmmss"));
        Date expire = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(expire);
        calendar.add(Calendar.MINUTE, 15);
        data.put("time_expire", DateUtils.format(calendar.getTime(), "yyyyMMddHHmmss"));
        JSONObject attachJson = null;
        if(StringUtils.isNotBlank(attach)){
            data.put("attach", attach);
        }

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
                logger.error("微信下单失败：" + resp.get("return_msg"));
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
            logger.error("获取微信订单失败", e);
        }
    }

    /**
     * 支付到微信
     */
    public static void payForWeixin(String openid, String orderId, long amount){
        Map<String, String> params = new HashMap();
        params.put("mch_appid", BaseConstant.WX_APPID);
        params.put("mchid", BaseConstant.WX_MCHID);
        params.put("nonce_str", WeixinUtil.create_nonce_str());
        params.put("partner_trade_no", orderId);
        params.put("openid", openid);
        params.put("check_name", "NO_CHECK");// NO_CHECK：不校验真实姓名 FORCE_CHECK：强校验真实姓名
        params.put("amount", amount + "");// 企业付款金额，单位为分
        params.put("desc", "用户提现");// 企业付款操作说明信息。必填。
        params.put("spbill_create_ip", "118.31.18.166");//调用接口的机器Ip地址
        //
        try {
            String sign = WXPayUtil.generateSignature(params, BaseConstant.WX_EncodingAESKey);
            params.put("sign", sign);
            String result = HttpsUtil.handlePostWithSSL(BaseConstant.WEIXIN_PAY_OPENID, JSONObject.toJSONString(params),
                    null,WeixinPayConfig.queryWeixinSSL());
            JSONObject resultJson = JSON.parseObject(result);
            if(resultJson.getString("return_code").equals("FAIL")){
                logger.error("付款到微信失败：" + resultJson.getString("return_msg"));
                throw new BizException(resultJson.getString("return_msg"));
            }
            String result_code = resultJson.getString("result_code");
            if(result_code.equals("SUCCESS")) {
                // TODO 对提现结果进行一些校验
                resultJson.getString("mch_appid");
                resultJson.getString("mchid");
                resultJson.getString("nonce_str");
                resultJson.getString("partner_trade_no");// 商户订单号
                resultJson.getString("payment_no");// 微信订单号
                resultJson.getString("payment_time");// 微信支付成功时间
            } else {
                resultJson.getString("err_code");
                resultJson.getString("err_code_des");
            }
        } catch (Exception e) {
            logger.error("付款到微信异常", e);
            throw new RuntimeException("付款到微信异常" , e);
        }
    }


    public static void payForBank(String orderId, String bankNo, String username, String bankCode, long amount){
        Map<String, String> params = new HashMap();
        params.put("mchid", BaseConstant.WX_MCHID);
        params.put("partner_trade_no", orderId);
        params.put("nonce_str", WeixinUtil.create_nonce_str());
        params.put("enc_bank_no", bankNo); // 收款方银行卡号
        params.put("enc_true_name", username);// TODO 收款方用户名 采用标准RSA算法，公钥由微信侧提供
        params.put("bank_code", bankCode);// 收款方开户行 银行卡所在开户行编号
        params.put("amount", amount + "");// 企业付款金额，单位为分
        params.put("desc", "用户提现");// 企业付款操作说明信息。必填。
        try {
            String sign = WXPayUtil.generateSignature(params, BaseConstant.WX_EncodingAESKey);
            params.put("sign", sign);
            String result = HttpsUtil.handlePostWithSSL(BaseConstant.WEIXIN_PAY_BANK, JSONObject.toJSONString(params),
                    null, WeixinPayConfig.queryWeixinSSL());
            JSONObject resultJson = JSON.parseObject(result);
            if(resultJson.getString("return_code").equals("FAIL")){
                logger.error("付款到银行异常：" + resultJson.getString("return_msg"));
                throw new BizException(resultJson.getString("return_msg"));
            }
            String result_code = resultJson.getString("result_code");
            if(result_code.equals("SUCCESS")) {
                resultJson.getString("mchid");
                resultJson.getString("partner_trade_no");// 商户订单号
                resultJson.getString("amount");
                resultJson.getString("nonce_str");
                resultJson.getString("payment_no");// 微信订单号
                resultJson.getString("cmms_amt");// 微信支付成功时间
                resultJson.getString("sign");
            } else {
                resultJson.getString("err_code");
                resultJson.getString("err_code_des");
            }
        } catch (Exception e) {
            logger.error("付款到银行异常", e);
            throw new RuntimeException("付款到银行异常" , e);
        }
    }


}
