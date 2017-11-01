package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.CustBpDetail;

public interface CustBpDetailMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CustBpDetail record);

    int insertSelective(CustBpDetail record);

    CustBpDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CustBpDetail record);

    int updateByPrimaryKey(CustBpDetail record);
}