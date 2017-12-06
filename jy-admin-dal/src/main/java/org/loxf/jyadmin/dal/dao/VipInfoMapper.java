package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.VipInfo;

import java.util.List;

public interface VipInfoMapper {
    int insert(VipInfo record);

    VipInfo selectByCustId(String custId);

    int updateByCustId(VipInfo record);

    List<VipInfo> queryExpireInfo();
}