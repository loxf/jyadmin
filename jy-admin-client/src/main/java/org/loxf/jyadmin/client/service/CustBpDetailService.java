package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.client.dto.CustBpDetailDto;

public interface CustBpDetailService {
    PageResult<CustBpDetailDto> pager(CustBpDetailDto custBpDetailDto);
}
