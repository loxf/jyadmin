package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;

public interface ShareService {
    public BaseResult<String> createQR(String nickName, String custId);
    BaseResult shareInfo(String custId, String detailName, String shareObj, String type);
}
