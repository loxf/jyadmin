package org.loxf.jyadmin.client.service;

import com.alibaba.fastjson.JSONObject;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.client.dto.CustDto;

import java.math.BigDecimal;

public interface AccountService {
    /**
     * 获取余额列表
     * @param custDto
     * @return
     */
    PageResult<JSONObject> queryBalanceList(CustDto custDto);

    /**
     * 获取余额
     * @param custId
     * @return
     */
    BaseResult<BigDecimal> queryBalance(String custId);

    /**
     * 获取账户
     * @param custId
     * @return
     */
    BaseResult<JSONObject> queryAccount(String custId);

    /**
     * 获取基本信息
     * @param custId
     * @return
     */
    BaseResult<JSONObject> queryBasicInfo(String custId);

    /**
     * 管理员支付--不用密码，只是用于记录消费信息
     * @param custId
     * @param money
     * @param bp
     * @param orderId
     * @param detailName
     * @return
     */
    BaseResult<Boolean> payByAdmin(String custId, BigDecimal money, BigDecimal bp, String orderId, String detailName);

    /**
     * 减款
     * @param custId
     * @param password
     * @param money
     * @param bp
     * @param orderId
     * @param detailName
     * @return
     */
    BaseResult<Boolean> reduce(String custId, String password, BigDecimal money, BigDecimal bp, String orderId, String detailName);

    /**
     * 增加金额
     * @param custId
     * @param money
     * @param bp
     * @param orderId
     * @param detailName
     * @param sourceCustId
     * @return
     */
    BaseResult<Boolean> increase(String custId, BigDecimal money, BigDecimal bp, String orderId, String detailName, String sourceCustId);

    /**
     * 第三方减款（微信支付后，调用此方法完成账单明细等记录，如果有积分抵扣也走这儿扣）
     * @param custId
     * @param money 第三方支付金额
     * @param bp 准备扣减的积分
     * @param orderId
     * @param detailName
     * @return
     */
    BaseResult<Boolean> reduceByThird(String custId, BigDecimal money, BigDecimal bp, String orderId, String detailName);

    /**
     * 设置支付密码
     * @param custId
     * @param email
     * @param phone
     * @param isChinese
     * @param password
     * @param verifyCode
     * @return
     */
    BaseResult setPayPassword(String custId, String email, String phone, int isChinese, String password, String verifyCode);

    /**
     * 获取积分排名
     * @param custId
     * @return
     */
    BaseResult<JSONObject> queryBpRankingList(String custId);

    BaseResult<String> delAccount(String custId);
}
