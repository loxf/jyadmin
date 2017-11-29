package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.CustBpDetail;

import java.util.List;

public interface CustBpDetailMapper {
    int insert(CustBpDetail record);

    List<CustBpDetail> list(CustBpDetail record);

    int count(CustBpDetail record);
}