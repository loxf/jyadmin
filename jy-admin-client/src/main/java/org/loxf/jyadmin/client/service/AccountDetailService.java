package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.client.dto.AccountDetailDto;

public interface AccountDetailService {
    PageResult<AccountDetailDto> queryDetails(AccountDetailDto accountDetailDto);
}
