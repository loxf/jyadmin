package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.CustCash;

public interface CustCashMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CustCash record);

    int insertSelective(CustCash record);

    CustCash selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CustCash record);

    int updateByPrimaryKey(CustCash record);
}