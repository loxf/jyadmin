package org.loxf.jyadmin.dal.dao;

import org.apache.ibatis.annotations.Param;
import org.loxf.jyadmin.dal.po.Active;
import org.loxf.jyadmin.dal.po.Active;

import java.util.List;

public interface ActiveMapper {
    int deleteByActiveId(String id);

    List<Active> pager(Active active);

    int count(Active active);

    Active selectByActiveId(String id);

    int insert(Active record);

    int updateByActiveId(Active record);

    int onOrOffActive(@Param("activeId") String activeId, @Param("status") Integer status);
}