package org.loxf.jyadmin.client.service;

import com.alibaba.fastjson.JSONObject;
import org.loxf.jyadmin.base.bean.BaseResult;

public interface AccountService {
    BaseResult<JSONObject> queryBasicInfo(String custId);
}
