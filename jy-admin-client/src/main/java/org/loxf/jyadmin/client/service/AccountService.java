package org.loxf.jyadmin.client.service;

import com.alibaba.fastjson.JSONObject;
import org.loxf.jyadmin.base.bean.BaseResult;

import java.math.BigDecimal;

public interface AccountService {
    BaseResult<JSONObject> queryAccount(String custId);
    BaseResult<JSONObject> queryBasicInfo(String custId);
    BaseResult<Boolean> reduce(String custId, String password, BigDecimal money, BigDecimal bp, String orderId, String detailName);
    BaseResult<Boolean> increase(String custId, BigDecimal money, BigDecimal bp, String orderId, String detailName);
    BaseResult setPayPassword(String custId, String email, String phone, int isChinese, String password, String verifyCode);
    BaseResult<JSONObject> queryBpRankingList(String custId);
}
