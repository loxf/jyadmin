package org.loxf.jyadmin.dal.dao;

import org.apache.ibatis.annotations.Param;
import org.loxf.jyadmin.dal.po.CustCash;

import java.util.List;

public interface CustCashMapper {
    CustCash selectById(Long id);

    int insert(CustCash record);

    int pendingCustCash(@Param("id") Long recordId, @Param("status") Integer status, @Param("remark") String remark);

    List<CustCash> pager(CustCash custCash);

    int count(CustCash custCash);

}