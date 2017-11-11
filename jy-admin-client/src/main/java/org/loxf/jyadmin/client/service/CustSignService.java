package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;

public interface CustSignService {
    BaseResult<Boolean> sign(String custId, String signDate);
    BaseResult<Boolean> hasSign(String custId, String signDate);
}
