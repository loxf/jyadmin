package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;

import java.util.Map;

public interface NoticeService {
    /**
     * @param type
     * @param metaData 参数
     * @return
     */
    BaseResult insert(String type, String obj, Map metaData);
}
