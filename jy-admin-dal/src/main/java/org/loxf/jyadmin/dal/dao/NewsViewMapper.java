package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.NewsView;

public interface NewsViewMapper {
    int insert(NewsView record);

    int exists(NewsView record);

    int updateReadTimes(NewsView record);

}