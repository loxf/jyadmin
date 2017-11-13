package org.loxf.jyadmin.dal.dao;

import org.apache.ibatis.annotations.Param;
import org.loxf.jyadmin.dal.po.IndexRecommend;

import java.util.List;

public interface IndexRecommendMapper {
    int insert(@Param("type") String type,@Param("objId") String objId);

    List<IndexRecommend> selectShow();

    int updateByPrimaryKey(@Param("type") String type,@Param("objId") String objId);
}