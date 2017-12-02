package org.loxf.jyadmin.dal.dao;

import org.apache.ibatis.annotations.Param;
import org.loxf.jyadmin.dal.po.Share;

public interface ShareMapper {
    int existsByObjAndType(@Param("custId") String custId, @Param("type") String type, @Param("shareObj") String shareObj);

    int insert(Share record);

}