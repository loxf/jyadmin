package org.loxf.jyadmin.dal.dao;

import org.apache.ibatis.annotations.Param;
import org.loxf.jyadmin.dal.po.News;

import java.util.List;

public interface NewsMapper {
    int deleteByTitleId(String titleId);

    int insert(News record);

    News selectByTitleId(String titleId);

    int updateByTitleId(News record);

    List<News> list(News record);

    int count(News record);

    int deployNewsOrNot(@Param("titleId") String titleId, @Param("oper")Integer oper);

}