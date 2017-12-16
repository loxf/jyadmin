package org.loxf.jyadmin.dal.dao;

import org.apache.ibatis.annotations.Param;
import org.loxf.jyadmin.dal.po.CompanyIncome;

import java.util.List;

public interface CompanyIncomeMapper {

    int insert(CompanyIncome record);

    int count(CompanyIncome record);

    List<CompanyIncome> list(CompanyIncome record);

    double queryIncome(@Param("startDay") String startDay);

    double queryScholarship(@Param("startDay") String startDay);

}