package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.client.dto.CompanyIncomeDto;
import org.loxf.jyadmin.client.tmp.IncomeUpload;

import java.util.List;

public interface CompanyIncomeService {
    BaseResult initCompanyIncome(List<IncomeUpload> list);
    BaseResult initScholarshipIncome(List<IncomeUpload> list);
    PageResult<CompanyIncomeDto> pager(CompanyIncomeDto companyIncomeDto);
    BaseResult queryCompanyIncome();
    BaseResult queryScholarship();
}
