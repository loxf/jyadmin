package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.CustScore;

import java.util.List;

public interface CustScoreMapper {
    int insert(CustScore record);

    int selectPassCountByOfferList(List<String> offerList);

    int count(CustScore record);

    String getMinMaxScore(String offerId);

    List<CustScore> list(CustScore record);

    CustScore selectByScoreId(String scoreId);

}