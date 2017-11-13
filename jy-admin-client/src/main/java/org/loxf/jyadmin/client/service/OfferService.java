package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.client.dto.OfferDto;
import org.loxf.jyadmin.client.dto.OfferRelDto;

import java.util.List;

public interface OfferService {
    /**
     * @param offerDto
     * @param appType 1:后台管理 2:前端
     * @return
     */
    PageResult<OfferDto> pager(OfferDto offerDto, Integer appType);
    OfferDto queryOffer(String offerId);
    BaseResult newOffer(OfferDto offerDto, List<OfferRelDto> offerRelDtos);
    BaseResult updateOffer(OfferDto offerDto, List<OfferRelDto> offerRelDtos);
    BaseResult deleteOffer(String offerId);
    BaseResult onOrOffOffer(String offerId, Integer status);
    BaseResult<List<OfferDto>> showOfferRel(String offerId, String relType);
    BaseResult<List<OfferDto>> pagerOfferAndActive(String name);
    BaseResult sendIndexRecommend(String offerId);
}
