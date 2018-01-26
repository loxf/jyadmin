package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.CustCertify;

import java.util.List;

public interface CustCertifyMapper {
    int insert(CustCertify record);

    int count(CustCertify record);

    List<CustCertify> list(CustCertify record);
}