package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.client.dto.CustScoreDto;

import java.util.List;

public interface CustScoreService {
    BaseResult addScore(CustScoreDto custScoreDto);
    BaseResult<Boolean> allPass(List<String> offerList);
    PageResult<CustScoreDto> pager(CustScoreDto custScoreDto);
    BaseResult<String[]> getMinMaxScore(String offerId);
    BaseResult selectByScoreId(String scoreId);
}
