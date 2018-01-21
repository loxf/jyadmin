package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.CustCertify;

public interface CustCertifyMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CustCertify record);

    int insertSelective(CustCertify record);

    CustCertify selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CustCertify record);

    int updateByPrimaryKey(CustCertify record);
}