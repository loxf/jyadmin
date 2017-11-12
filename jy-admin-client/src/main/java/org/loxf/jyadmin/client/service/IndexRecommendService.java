package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.client.dto.IndexRecommendDto;

import java.util.List;

public interface IndexRecommendService {
    BaseResult<List<IndexRecommendDto>> selectShow();

    BaseResult<Boolean> insert(String type, String objId);

    BaseResult<Boolean> update(String type, String objId);

}
