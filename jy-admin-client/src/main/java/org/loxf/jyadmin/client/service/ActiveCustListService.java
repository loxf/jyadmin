package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.client.dto.ActiveCustListDto;

import java.util.List;

public interface ActiveCustListService {
    BaseResult<List<ActiveCustListDto>> queryList(String activeId);

    Boolean hasJoin(String activeId, String custId);

    BaseResult addCustByActive(ActiveCustListDto activeCustListDto);

}
