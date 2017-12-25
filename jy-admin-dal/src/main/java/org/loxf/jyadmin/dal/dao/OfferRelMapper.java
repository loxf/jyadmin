package org.loxf.jyadmin.dal.dao;

import org.apache.ibatis.annotations.Param;
import org.loxf.jyadmin.dal.po.Offer;
import org.loxf.jyadmin.dal.po.OfferRel;

import java.util.List;

public interface OfferRelMapper {
    int deleteByOfferIdAndRelType(@Param("offerId") String offerId, @Param("relType") String relType);

    int insert(OfferRel record);

    List<String> queryOfferIdByRelOfferId(String relOfferId);

}