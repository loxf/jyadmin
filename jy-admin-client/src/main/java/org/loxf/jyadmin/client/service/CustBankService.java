package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.client.dto.CustBankDto;

public interface CustBankService {
    PageResult<CustBankDto> pager(CustBankDto bankDto);

    BaseResult addBankCard(CustBankDto custBankDto);

    BaseResult unBind(String custId, Long id);

    BaseResult update(CustBankDto custBankDto);
}
