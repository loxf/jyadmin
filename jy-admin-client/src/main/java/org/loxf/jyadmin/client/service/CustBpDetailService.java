package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.client.dto.CustBpDetailDto;

public interface CustBpDetailService {
    BaseResult<Boolean> insert(CustBpDetailDto custBpDetailDto);
    PageResult<CustBpDetailDto> pager(CustBpDetailDto custBpDetailDto);
}
