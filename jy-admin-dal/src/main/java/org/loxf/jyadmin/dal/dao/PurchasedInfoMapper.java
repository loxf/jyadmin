package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.PurchasedInfo;

import java.util.List;

public interface PurchasedInfoMapper {
    Integer count(PurchasedInfo record);

    List<PurchasedInfo> list(PurchasedInfo record);

    int insertList(List<PurchasedInfo> record);

    int insert(PurchasedInfo record);

}