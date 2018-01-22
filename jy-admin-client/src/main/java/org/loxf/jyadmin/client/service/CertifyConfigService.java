package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.client.dto.CertifyConfigDto;

public interface CertifyConfigService {
    PageResult<CertifyConfigDto> pager(CertifyConfigDto certifyConfigDto);
    BaseResult insert(CertifyConfigDto certifyConfigDto);
    BaseResult update(CertifyConfigDto certifyConfigDto);
    BaseResult delete(String certifyId);
    BaseResult<CertifyConfigDto> queryById(String certifyId);
}
