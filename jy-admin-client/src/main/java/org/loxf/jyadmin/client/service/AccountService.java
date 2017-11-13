package org.loxf.jyadmin.client.service;

import com.alibaba.fastjson.JSONObject;
import org.loxf.jyadmin.base.bean.BaseResult;

import java.math.BigDecimal;

public interface AccountService {
    BaseResult<JSONObject> queryBasicInfo(String custId);
    BaseResult<Boolean> reduce(String custId, String password, BigDecimal money, String orderId, String detailName);
    BaseResult<Boolean> increase(String custId, BigDecimal money, String orderId, String detailName);
}
