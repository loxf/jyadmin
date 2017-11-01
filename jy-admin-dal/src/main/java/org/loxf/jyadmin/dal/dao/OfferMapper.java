package org.loxf.jyadmin.dal.dao;

import org.apache.ibatis.annotations.Param;
import org.loxf.jyadmin.dal.po.Offer;

import java.util.List;

public interface OfferMapper {
    int deleteByOfferId(String id);

    List<Offer> pager(Offer offer);

    int count(Offer offer);

    Offer selectByOfferId(String id);

    int insert(Offer record);

    int updateByOfferId(Offer record);

    int onOrOffOffer(@Param("offerId") String offerId, @Param("status") Integer status);

    List<Offer> pagerOfferAndActive(String name);

    List<Offer> showOfferByOfferIdAndRelType(@Param("offerId") String offerId, @Param("relType") String relType);
}