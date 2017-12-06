package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.client.dto.PurchasedInfoDto;

public interface PurchasedInfoService {
    BaseResult<Integer> count(PurchasedInfoDto dto);
    BaseResult<Boolean> insert(PurchasedInfoDto dto);
}
