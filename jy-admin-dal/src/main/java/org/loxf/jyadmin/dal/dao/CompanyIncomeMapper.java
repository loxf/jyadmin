package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.CompanyIncome;

import java.util.List;

public interface CompanyIncomeMapper {

    int insert(CompanyIncome record);

    int count(CompanyIncome record);

    List<CompanyIncome> list(CompanyIncome record);

}