package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.Province;

import java.util.List;

public interface ProvinceMapper {
    List<Province> selectList(Province province);
    Province selectProvince(String provinceid);
}