package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;

public interface VerifyCodeService {
    BaseResult sendVerifyCode(String custId, String obj, Integer codeType);
    BaseResult verify(String custId, String obj, String code);
}
