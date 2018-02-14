package org.loxf.jyadmin.dal.dao;

import org.apache.ibatis.annotations.Param;
import org.loxf.jyadmin.dal.po.Event;

import java.util.List;

public interface EventMapper {
    int insert(Event record);

    int update(Event record);

    int count(Event record);

    List<Event> list(Event record);
}