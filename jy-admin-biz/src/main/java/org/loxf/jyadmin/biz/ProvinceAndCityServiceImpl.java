package org.loxf.jyadmin.biz;

import org.apache.commons.collections.CollectionUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.client.dto.AreaDto;
import org.loxf.jyadmin.client.dto.CityDto;
import org.loxf.jyadmin.client.dto.ProvinceDto;
import org.loxf.jyadmin.client.service.ProvinceAndCityService;
import org.loxf.jyadmin.dal.dao.AreaMapper;
import org.loxf.jyadmin.dal.dao.CityMapper;
import org.loxf.jyadmin.dal.dao.ProvinceMapper;
import org.loxf.jyadmin.dal.po.Area;
import org.loxf.jyadmin.dal.po.City;
import org.loxf.jyadmin.dal.po.Province;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("provinceAndCityService")
public class ProvinceAndCityServiceImpl implements ProvinceAndCityService {
    @Autowired
    private ProvinceMapper provinceMapper;
    @Autowired
    private CityMapper cityMapper;
    @Autowired
    private AreaMapper areaMapper;

    @Override
    public BaseResult<List<ProvinceDto>> queryProvince(ProvinceDto provinceDto) {
        Province province = new Province();
        if(provinceDto!=null){
            BeanUtils.copyProperties(provinceDto, province);
        }
        List<Province> list = provinceMapper.selectList(province);
        if(CollectionUtils.isNotEmpty(list)){
            List<ProvinceDto> result = new ArrayList<>();
            for (Province prov : list){
                ProvinceDto dto = new ProvinceDto();
                BeanUtils.copyProperties(prov, dto);
                result.add(dto);
            }
            return new BaseResult<>(result);
        }
        return new BaseResult<>();
    }

    @Override
    public BaseResult<List<CityDto>> queryCity(CityDto cityDto) {
        City city = new City();
        if(cityDto!=null){
            BeanUtils.copyProperties(cityDto, city);
        }
        List<City> list = cityMapper.selectList(city);
        if(CollectionUtils.isNotEmpty(list)){
            List<CityDto> result = new ArrayList<>();
            for (City prov : list){
                CityDto dto = new CityDto();
                BeanUtils.copyProperties(prov, dto);
                result.add(dto);
            }
            return new BaseResult<>(result);
        }
        return new BaseResult<>();
    }

    @Override
    public BaseResult<List<AreaDto>> queryArea(AreaDto areaDto) {
        Area area = new Area();
        if(area!=null){
            BeanUtils.copyProperties(areaDto, area);
        }
        List<Area> list = areaMapper.selectList(area);
        if(CollectionUtils.isNotEmpty(list)){
            List<AreaDto> result = new ArrayList<>();
            for (Area prov : list){
                AreaDto dto = new AreaDto();
                BeanUtils.copyProperties(prov, dto);
                result.add(dto);
            }
            return new BaseResult<>(result);
        }
        return new BaseResult<>();
    }
}
