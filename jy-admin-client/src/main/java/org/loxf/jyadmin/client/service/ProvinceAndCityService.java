package org.loxf.jyadmin.client.service;

import com.alibaba.fastjson.JSONArray;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.client.dto.AreaDto;
import org.loxf.jyadmin.client.dto.CityDto;
import org.loxf.jyadmin.client.dto.ProvinceDto;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.List;

public interface ProvinceAndCityService {
    /**
     * @param type P:省 C:市 A:区
     * @param id
     * @return
     */
    BaseResult<String> query(String type, String id);
    BaseResult<List<ProvinceDto>> queryProvince(ProvinceDto provinceDto);
    BaseResult<List<CityDto>> queryCity(CityDto cityDto);
    BaseResult<List<AreaDto>> queryArea(AreaDto areaDto);

    /**
     * type 1 省 2 到市 3 到区
     * @return
     */
    BaseResult<JSONArray> queryAreaByTree(Integer type);
}
