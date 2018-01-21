package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.CustScore;

public interface CustScoreMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CustScore record);

    int insertSelective(CustScore record);

    CustScore selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CustScore record);

    int updateByPrimaryKey(CustScore record);
}