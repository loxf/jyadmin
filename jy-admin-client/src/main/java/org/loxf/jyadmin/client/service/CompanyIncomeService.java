package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.client.dto.CompanyIncomeDto;

public interface CompanyIncomeService {
    PageResult<CompanyIncomeDto> pager(CompanyIncomeDto companyIncomeDto);
    BaseResult queryCompanyIncome();
    BaseResult queryScholarship();
}
