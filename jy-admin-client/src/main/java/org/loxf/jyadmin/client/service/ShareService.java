package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;

public interface ShareService {
    BaseResult shareInfo(String custId, String detailName, String shareObj, String type);
}
