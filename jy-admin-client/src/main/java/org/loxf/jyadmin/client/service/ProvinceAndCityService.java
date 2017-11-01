package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.client.dto.AreaDto;
import org.loxf.jyadmin.client.dto.CityDto;
import org.loxf.jyadmin.client.dto.ProvinceDto;

import java.util.List;

public interface ProvinceAndCityService {
    BaseResult<List<ProvinceDto>> queryProvince(ProvinceDto provinceDto);
    BaseResult<List<CityDto>> queryCity(CityDto cityDto);
    BaseResult<List<AreaDto>> queryArea(AreaDto areaDto);
}
