package org.loxf.jyadmin.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.regexp.internal.RE;
import org.apache.commons.collections.CollectionUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
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
import java.util.HashMap;
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
    public BaseResult<String> query(String type, String id) {
        if(type==null || (!type.equals("P")&&!type.equals("C")&&!type.equals("A"))){
            return new BaseResult(BaseConstant.FAILED, "获取地域的类型不正确");
        }
        if(type.equals("P")){
            Province province = provinceMapper.selectProvince(id);
            if(province!=null)
                return new BaseResult(province.getProvince());
        } else if(type.equals("C")){
            City city = cityMapper.selectCity(id);
            if(city!=null)
                return new BaseResult(city.getCity());
        } else {
            Area area = areaMapper.selectArea(id);
            if(area!=null)
                return new BaseResult(area.getArea());
        }
        return new BaseResult<>();
    }

    @Override
    public BaseResult<List<ProvinceDto>> queryProvince(ProvinceDto provinceDto) {
        Province province = new Province();
        if (provinceDto != null) {
            BeanUtils.copyProperties(provinceDto, province);
        }
        List<Province> list = provinceMapper.selectList(province);
        if (CollectionUtils.isNotEmpty(list)) {
            List<ProvinceDto> result = new ArrayList<>();
            for (Province prov : list) {
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
        if (cityDto != null) {
            BeanUtils.copyProperties(cityDto, city);
        }
        List<City> list = cityMapper.selectList(city);
        if (CollectionUtils.isNotEmpty(list)) {
            List<CityDto> result = new ArrayList<>();
            for (City prov : list) {
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
        if (area != null) {
            BeanUtils.copyProperties(areaDto, area);
        }
        List<Area> list = areaMapper.selectList(area);
        if (CollectionUtils.isNotEmpty(list)) {
            List<AreaDto> result = new ArrayList<>();
            for (Area prov : list) {
                AreaDto dto = new AreaDto();
                BeanUtils.copyProperties(prov, dto);
                result.add(dto);
            }
            return new BaseResult<>(result);
        }
        return new BaseResult<>();
    }

    @Override
    public BaseResult<JSONArray> queryAreaByTree(Integer type) {
        if(type==null || type>3 || type<1){
            type = 1;//默认省份数据
        }
        List<HashMap> list = provinceMapper.queryAreaByTree();
        if (CollectionUtils.isNotEmpty(list)) {
            JSONArray result = new JSONArray();
            HashMap provinceHashMap = new HashMap();
            HashMap cityHashMap = new HashMap();
            for (HashMap tmp : list) {
                String provincelabel = (String) tmp.get("province");
                String provinceid = (String) tmp.get("provinceid");
                String citylabel = (String) tmp.get("city");
                String cityid = (String) tmp.get("cityid");
                String arealabel = (String) tmp.get("area");
                String areaid = (String) tmp.get("areaid");

                if (!provinceHashMap.containsKey(provinceid)) {
                    provinceHashMap.put(provinceid, provincelabel);
                    //增加一个省
                    JSONObject provinceObject = new JSONObject();
                    provinceObject.put("label", provincelabel);
                    provinceObject.put("value", provincelabel + "," + provinceid);
                    if(type>1) {
                        provinceObject.put("children", new JSONArray());
                    }
                    result.add(provinceObject);
                }
                if(type>1) {
                    JSONObject province = result.getJSONObject(result.size() - 1);
                    JSONArray cityOfProvince = province.getJSONArray("children");
                    if (!cityHashMap.containsKey(cityid)) {
                        // 改变了市
                        cityHashMap.put(cityid, citylabel);
                        //增加一个市
                        JSONObject cityObject = new JSONObject();
                        cityObject.put("label", citylabel);
                        cityObject.put("value", citylabel + "," + provinceid + "-" + cityid);
                        if(type>2) {
                            cityObject.put("children", new JSONArray());
                        }
                        cityOfProvince.add(cityObject);
                    }
                    if(type>2) {
                        JSONObject city = cityOfProvince.getJSONObject(cityOfProvince.size() - 1);
                        JSONArray areaOfCity = city.getJSONArray("children");

                        // 新增一个area
                        JSONObject cityObject = new JSONObject();
                        cityObject.put("label", arealabel);
                        cityObject.put("value", arealabel + "," + provinceid + "-" + cityid + "-" + areaid);
                        areaOfCity.add(cityObject);
                    }
                }
            }
            return new BaseResult<>(result);
        } else{
            return new BaseResult<>(BaseConstant.FAILED, "无数据");
        }
    }
}
