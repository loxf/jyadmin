package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.Area;

import java.util.List;

public interface AreaMapper {
    List<Area> selectList(Area city);
    Area selectArea(String cityid);
}