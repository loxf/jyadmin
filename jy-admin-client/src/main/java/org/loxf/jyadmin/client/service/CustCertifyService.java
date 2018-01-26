package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.client.dto.CustCertifyDto;

public interface CustCertifyService {
    PageResult<CustCertifyDto> pager(CustCertifyDto custScoreDto);
    BaseResult addCertify(CustCertifyDto custScoreDto);
}
