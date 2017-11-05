package org.loxf.jyadmin.dal.dao;

import org.apache.ibatis.annotations.Param;
import org.loxf.jyadmin.dal.po.Config;

import java.util.List;

public interface ConfigMapper {
    int insert(Config record);

    int deleteConfig(@Param("catalog") String catalog, @Param("configCode")String configCode);

    Config selectConfig(@Param("catalog") String catalog, @Param("configCode")String configCode);

    Config selectById(long id);

    int updateByPrimaryKey(Config record);

    int count(Config record);

    List<Config> list(Config record);

    int onOrOffConfig(@Param("id") long id, @Param("status")int status);
}