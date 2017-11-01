package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.CustBank;

public interface CustBankMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CustBank record);

    int insertSelective(CustBank record);

    CustBank selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CustBank record);

    int updateByPrimaryKey(CustBank record);
}