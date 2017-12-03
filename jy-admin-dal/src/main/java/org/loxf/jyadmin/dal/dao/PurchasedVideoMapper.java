package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.PurchasedVideo;

import java.util.List;

public interface PurchasedVideoMapper {
    Integer count(PurchasedVideo record);

    List<PurchasedVideo> list(PurchasedVideo record);

    int insert(PurchasedVideo record);

}