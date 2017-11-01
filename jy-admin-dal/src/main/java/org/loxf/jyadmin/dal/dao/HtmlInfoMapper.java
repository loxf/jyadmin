package org.loxf.jyadmin.dal.dao;

import org.apache.ibatis.annotations.Param;
import org.loxf.jyadmin.dal.po.HtmlInfo;

public interface HtmlInfoMapper {
    int insert(@Param("htmlId") String htmlId, @Param("htmlInfo")String htmlInfo);

    int update(@Param("htmlId") String htmlId, @Param("htmlInfo")String htmlInfo);

    HtmlInfo selectByHtmlId(@Param("htmlId")String htmlId);
}