package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.City;

import java.util.List;

public interface CityMapper {
    List<City> selectList(City city);
    City selectCity(String cityid);
}