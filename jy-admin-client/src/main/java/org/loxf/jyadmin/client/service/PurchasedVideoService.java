package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.client.dto.PurchasedVideoDto;

public interface PurchasedVideoService {
    BaseResult<Integer> count(PurchasedVideoDto dto);
    BaseResult<Boolean> insert(PurchasedVideoDto dto);
}
