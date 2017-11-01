package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.OfferCatalog;

import java.util.List;

public interface OfferCatalogMapper {
    int deleteByPrimaryKey(String catalogId);

    int insert(OfferCatalog record);

    List<OfferCatalog> list(OfferCatalog offerCatalog);

    int count(OfferCatalog offerCatalog);

    int updateByPrimaryKey(OfferCatalog record);

    OfferCatalog queryById(String catalogId);
}