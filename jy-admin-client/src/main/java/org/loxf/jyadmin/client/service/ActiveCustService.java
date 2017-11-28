package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.client.dto.ActiveCustListDto;

public interface ActiveCustService {
    PageResult<ActiveCustListDto> pager(String custId, int page, int size);
}
