package org.loxf.jyadmin.web.admin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.util.JedisUtil;
import org.loxf.jyadmin.client.dto.*;
import org.loxf.jyadmin.client.service.*;
import org.loxf.jyadmin.dal.po.Admin;
import org.loxf.jyadmin.util.CookieUtil;
import org.loxf.jyadmin.util.IPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Map;

@Controller
@RequestMapping("/admin/order")
public class OrderController extends BaseControl<OrderDto> {
    private static Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;
    @Autowired
    private CustService custService;
    @Autowired
    private OfferService offerService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private JedisUtil jedisUtil;

    @RequestMapping("/index")
    public String index(Model model){
        return "order/order_list";
    }
    @RequestMapping("/list")
    @ResponseBody
    public PageResult list(OrderDto orderDto){
        initRangeDate(orderDto);
        PageResult<OrderDto> pageResult = orderService.pager(orderDto);
        return pageResult;
    }
    /**
     * 创建管理员订购订单 支付方式，默认5：线下支付 orderType 默认1
     *
     * @param custId
     * @param offerId
     * @return
     */
    @RequestMapping("/createOrder")
    @ResponseBody
    public BaseResult createOrder(HttpServletRequest request, String custId, String offerId) {
        if (StringUtils.isBlank(custId)||StringUtils.isBlank(offerId)) {
            return new BaseResult(BaseConstant.FAILED, "订购商品/客户ID为空");
        }
        CustDto custDto = custService.queryCustByCustId(custId).getData();
        String lv = custDto.getUserLevel();
        // 订单
        OrderDto orderDto = new OrderDto();
        // 商品校验
        OfferDto offerDto = offerService.queryOffer(offerId).getData();
        if (offerDto == null) {
            return new BaseResult(BaseConstant.FAILED, "商品不存在");
        }
        if(offerDto.getStatus()==0){
            return new BaseResult(BaseConstant.FAILED, "商品已下架");
        }
        if(offerDto.getOfferType().equals("VIP")){
            return new BaseResult(BaseConstant.FAILED, "只能购买课程或套餐");
        }
        // 是否购买
        BaseResult<Boolean> hasBuyOrder = orderService.hasBuy(custDto.getCustId(), 1, offerId);
        if (hasBuyOrder.getCode() == BaseConstant.FAILED) {
            return new BaseResult(BaseConstant.FAILED, hasBuyOrder.getMsg());
        }
        orderDto.setCustId(custId);
        orderDto.setOrderName(offerDto.getOfferName());
        orderDto.setOrderType(1);
        orderDto.setPayType(5); // 线下支付
        String privi = offerDto.getBuyPrivi();
        if (StringUtils.isBlank(privi)) {
            return new BaseResult(BaseConstant.FAILED, "当前商品不能直接购买");
        } else {
            JSONObject priviJson = JSONObject.parseObject(privi);
            if (!priviJson.containsKey(lv)) {
                return new BaseResult(BaseConstant.FAILED, "当前用户无权购买此商品");
            } else {
                String price = priviJson.getString(lv);
                orderDto.setOrderMoney(new BigDecimal(price));
                orderDto.setTotalMoney(new BigDecimal(price));
            }
        }
        if (orderDto.getOrderMoney().compareTo(BigDecimal.ZERO) < 0) {
            return new BaseResult(BaseConstant.FAILED, "实际付款金额不能小于0");
        }
        orderDto.setObjId(offerId);
        orderDto.setBp(BigDecimal.ZERO);
        orderDto.setDiscount(10L);
        String ip = IPUtil.getIpAddr(request);
        try {
            BaseResult<Map<String, String>> orderResult = orderService.createOrder(custDto.getOpenid(), ip, orderDto, null);
            if(orderResult.getCode()==BaseConstant.SUCCESS){
                orderResult.getData().put("orderName", orderDto.getOrderName());
                orderResult.getData().put("orderMoney", orderDto.getOrderMoney().toPlainString());
                orderResult.getData().put("nickName", custDto.getNickName());
            }
            return orderResult;
        } catch (Exception e){
            logger.error("创建订单失败", e);
            return new BaseResult(BaseConstant.FAILED, e.getMessage());
        }
    }


    /**
     * 支付订单
     *
     * @param orderId
     * @param password
     * @return
     */
    @RequestMapping("/payOrder")
    @ResponseBody
    public BaseResult payOrder(HttpServletRequest request, String orderId, String custId, String password) {
        String key = "ADMIN_PAY_ORDER_" + orderId;
        AdminDto adminDto = CookieUtil.getAdmin(request);

        BaseResult loginBaseResult = adminService.login(adminDto.getUserName(), password);
        if(loginBaseResult.getCode()==BaseConstant.SUCCESS && loginBaseResult.getData()!=null) {
            if (jedisUtil.setnx(key, "true", 60) > 0) {
                try {
                    BaseResult<OrderDto> baseResult = orderService.queryOrder(orderId);
                    if (baseResult.getCode() == BaseConstant.FAILED) {
                        return baseResult;
                    }
                    OrderDto orderDto = baseResult.getData();
                    if (orderDto == null) {
                        return new BaseResult(BaseConstant.FAILED, "订单不存在");
                    }
                    if (orderDto.getStatus() == 3) {
                        return new BaseResult(BaseConstant.SUCCESS, "订单处理成功");
                    }
                    if (!custId.equals(orderDto.getCustId())) {
                        return new BaseResult(BaseConstant.FAILED, "客户不一致");
                    }
                    BaseResult<Boolean> payBaseResult = accountService.payByAdmin(custId, orderDto.getOrderMoney(),
                            orderDto.getBp(), orderId, orderDto.getOrderName());
                    if (payBaseResult.getCode() == BaseConstant.SUCCESS && payBaseResult.getData()) {
                        // 支付成功
                        BaseResult baseResult1 = orderService.completeOrder(orderId, null, 3, "支付成功");
                        if (baseResult.getCode() == BaseConstant.SUCCESS &&
                                (orderDto.getOrderType() == 3 || orderDto.getOrderType() == 1)) {
                            // 如果购买的是VIP，设置用户信息刷新标志
                            jedisUtil.set("REFRESH_CUST_INFO_" + orderDto.getCustId(), "true", 60);
                        }
                        return baseResult1;
                    } else {
                        return payBaseResult;
                    }
                } catch (Exception e) {
                    logger.error("支付失败：", e);
                    return new BaseResult(BaseConstant.FAILED, "支付异常，请联系管理员");
                } finally {
                    jedisUtil.del(key);
                }
            } else {
                return new BaseResult(BaseConstant.FAILED, "订单处理中");
            }
        } else {
            return new BaseResult(BaseConstant.FAILED, "管理员密码错误");
        }
    }
}
