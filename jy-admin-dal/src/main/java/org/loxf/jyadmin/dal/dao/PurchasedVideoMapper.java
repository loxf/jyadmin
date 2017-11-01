package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.PurchasedVideo;

public interface PurchasedVideoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PurchasedVideo record);

    int insertSelective(PurchasedVideo record);

    PurchasedVideo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PurchasedVideo record);

    int updateByPrimaryKey(PurchasedVideo record);
}