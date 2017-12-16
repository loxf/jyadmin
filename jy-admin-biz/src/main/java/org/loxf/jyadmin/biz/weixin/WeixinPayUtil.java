package org.loxf.jyadmin.biz.weixin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Base64;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.exception.BizException;
import org.loxf.jyadmin.base.util.DateUtils;
import org.loxf.jyadmin.base.util.HttpsUtil;
import org.loxf.jyadmin.base.util.RSAUtil;
import org.loxf.jyadmin.biz.util.ConfigUtil;
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

    public static void main(String[] args) throws Exception {
        System.out.println(payForBank("CASH000002", "6214831202576416", "罗洪佳", "1001",100));
    }

    /**
     * 微信统一下单
     *
     * @param openid
     * @param ip
     * @param orderDto
     * @param attach   JSON透传
     * @throws Exception
     */
    public static BaseResult<OrderDto> createOrder(String openid, String ip, OrderDto orderDto, String attach) throws Exception {
        WeixinPayConfig config = new WeixinPayConfig();
        WXPay wxpay = new WXPay(config);

        Map<String, String> data = new HashMap<String, String>();
        data.put("openid", openid);
        data.put("body", ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "WX_BODYPREFIX").getConfigValue()
                + "-" + orderDto.getOrderName());// 线上电商，商家名称必须为实际销售商品的商家
        data.put("out_trade_no", orderDto.getOrderId());
        data.put("device_info", "WX");// 微信支付
        data.put("fee_type", "CNY");
        data.put("total_fee", orderDto.getOrderMoney().multiply(new BigDecimal(100)).longValue() + "");
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
        if (StringUtils.isNotBlank(attach)) {
            data.put("attach", attach);
        }

        try {
            Map<String, String> resp = wxpay.unifiedOrder(data);
            logger.info("微信支付下单：{}", JSON.toJSONString(resp));
            if (resp.get("return_code").equals("SUCCESS")) {
                if (resp.get("result_code").equals("SUCCESS")) {
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
    public static String payForWeixin(String openid, String orderId, long amount, String serverIp) throws Exception {
        WeixinPayConfig config = new WeixinPayConfig();
        Map<String, String> params = new HashMap();
        params.put("mch_appid", config.getAppID());
        params.put("mchid", config.getMchID());
        params.put("nonce_str", WXPayUtil.generateNonceStr());
        params.put("partner_trade_no", orderId);
        params.put("openid", openid);
        params.put("check_name", "NO_CHECK");// NO_CHECK：不校验真实姓名 FORCE_CHECK：强校验真实姓名
        params.put("amount", amount + "");// 企业付款金额，单位为分
        params.put("desc", "用户提现");// 企业付款操作说明信息。必填。
        params.put("spbill_create_ip", serverIp);//调用接口的机器Ip地址
        //
        String queryXML = WXPayUtil.generateSignedXml(params, config.getKey());
        int retry = 0;
        while (true) {
            String resultStr = HttpsUtil.handlePostWithSSL(BaseConstant.WEIXIN_PAY_OPENID, queryXML,
                    null, WeixinPayConfig.queryWeixinSSL());
            System.out.println(resultStr);
            Map<String, String> resultMap = WXPayUtil.xmlToMap(resultStr);
            if (resultMap.get("return_code").equals("FAIL")) {
                logger.error("付款到微信失败：" + resultMap.get("return_msg"));
                throw new BizException(resultMap.get("return_msg"));
            }
            String result_code = resultMap.get("result_code");
            if (result_code.equals("SUCCESS")) {
                // 对提现结果进行一些校验
                if (resultMap.get("mchid").equals(config.getMchID())
                        && resultMap.get("partner_trade_no").equals(orderId)) {
                    JSONObject result = new JSONObject();
                    result.put("payment_no", resultMap.get("payment_no")); // 微信订单号
                    result.put("payment_time", resultMap.get("payment_time")); // 微信支付成功时间
                    return JSONObject.toJSONString(resultMap);
                } else {
                    throw new BizException("微信返回信息校验失败");
                }
            } else {
                if (resultMap.get("err_code").equals("INVALID_REQUEST") || resultMap.get("err_code").equals("SYSTEMERROR")
                        || resultMap.get("err_code").equals("FREQUENCY_LIMITED")) {
                    retry++;
                    if (retry >= 3) {
                        throw new BizException(resultMap.get("err_code_des"), resultMap.get("err_code"));
                    }
                } else {
                    throw new BizException(resultMap.get("err_code_des"), resultMap.get("err_code"));
                }
                throw new BizException(resultMap.get("err_code_des"), resultMap.get("err_code"));
            }
        }
    }

    /**
     * 付款到银行卡
     *
     * @param orderId
     * @param bankNo
     * @param username
     * @param bankCode
     * @param amount
     */
    public static String payForBank(String orderId, String bankNo, String username, String bankCode, long amount) throws Exception {
        WeixinPayConfig config = new WeixinPayConfig();
        Map<String, String> params = new HashMap();
        params.put("mch_id", config.getMchID());
        params.put("partner_trade_no", orderId);
        params.put("nonce_str", WXPayUtil.generateNonceStr());
        params.put("bank_code", bankCode);// 收款方开户行 银行卡所在开户行编号
        params.put("amount", amount + "");// 企业付款金额，单位为分
        params.put("desc", "用户提现");// 企业付款操作说明信息。必填。
        String bankNoStr = new String(Base64.encode(RSAUtil.encryptByPublicKey(bankNo.getBytes())));
        String realNameStr = new String(Base64.encode(RSAUtil.encryptByPublicKey(username.getBytes())));
        params.put("enc_bank_no", bankNoStr); // 收款方银行卡号 采用标准RSA算法，公钥由微信侧提供
        params.put("enc_true_name", realNameStr);// 收款方用户名 采用标准RSA算法，公钥由微信侧提供
        String queryXml = WXPayUtil.generateSignedXml(params, config.getKey());
        int retry = 0;
        while (true) {
            String resultStr = HttpsUtil.handlePostWithSSL(BaseConstant.WEIXIN_PAY_BANK, queryXml,
                    null, WeixinPayConfig.queryWeixinSSL());
            System.out.println(resultStr);
            Map<String, String> resultMap = WXPayUtil.xmlToMap(resultStr);
            if (resultMap.get("return_code").equals("FAIL")) {
                logger.error("付款到银行异常：" + resultMap.get("return_msg"));
                throw new BizException(resultMap.get("return_msg"));
            }
            String result_code = resultMap.get("result_code");
            if (result_code.equals("SUCCESS")) {
                if (resultMap.get("mch_id").equals(config.getMchID())
                        && resultMap.get("partner_trade_no").equals(orderId)) {
                    JSONObject result = new JSONObject();
                    result.put("payment_no", resultMap.get("payment_no")); // 微信订单号
                    result.put("cmms_amt", resultMap.get("cmms_amt")); // 手续费
                    return JSONObject.toJSONString(result);
                } else {
                    throw new BizException("微信返回信息校验失败");
                }
            } else {
                if (resultMap.get("err_code").equals("INVALID_REQUEST") || resultMap.get("err_code").equals("SYSTEMERROR")
                        || resultMap.get("err_code").equals("FREQUENCY_LIMITED")) {
                    retry++;
                    if (retry >= 3) {
                        throw new BizException(resultMap.get("err_code_des"), resultMap.get("err_code"));
                    }
                } else {
                    throw new BizException(resultMap.get("err_code_des"), resultMap.get("err_code"));
                }
            }
        }
    }

    /**
     * 微信RSA api
     *
     * @throws Exception
     */
    public static BaseResult<String> queryRSA() throws Exception {
        Map<String, String> reqData = new HashMap<>();
        WeixinPayConfig config = new WeixinPayConfig();
        reqData.put("mch_id", config.getMchID());
        reqData.put("nonce_str", WXPayUtil.generateNonceStr());
        reqData.put("sign_type", "MD5");
        String xml = WXPayUtil.generateSignedXml(reqData, config.getKey());
        String resultStr = HttpsUtil.handlePostWithSSL(BaseConstant.WEIXIN_RSA_API, xml,
                null, WeixinPayConfig.queryWeixinSSL());
        System.out.println(resultStr);
        Map<String, String> resultMap = WXPayUtil.xmlToMap(resultStr);
        if (resultMap.get("return_code").equals("SUCCESS")) {
            if (resultMap.get("result_code").equals("SUCCESS")) {
                if (resultMap.get("mch_id").equals(config.getMchID())) {
                    System.out.println(resultMap.get("pub_key"));
                    return new BaseResult<>(resultMap.get("pub_key"));
                } else {
                    return new BaseResult<>(BaseConstant.FAILED, "商户ID不一致");
                }
            } else {
                throw new BizException(resultMap.get("err_code_des"), resultMap.get("err_code"));
            }
        } else {
            throw new BizException(resultMap.get("return_msg"));
        }
    }

}
