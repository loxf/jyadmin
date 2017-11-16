package org.loxf.jyadmin.dal.dao;

import org.apache.ibatis.annotations.Param;
import org.loxf.jyadmin.dal.po.CustBank;

import java.util.List;

public interface CustBankMapper {
    int count(CustBank record);

    List<CustBank> pager(CustBank record);

    int insert(CustBank record);

    int update(CustBank record);

    int unbind(@Param("custId") String custId, @Param("id") Long id);
}