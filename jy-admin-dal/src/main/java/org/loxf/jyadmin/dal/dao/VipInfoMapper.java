package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.VipInfo;

public interface VipInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(VipInfo record);

    int insertSelective(VipInfo record);

    VipInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(VipInfo record);

    int updateByPrimaryKey(VipInfo record);
}