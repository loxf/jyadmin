package org.loxf.jyadmin.dal.dao;

import org.apache.ibatis.annotations.Param;
import org.loxf.jyadmin.dal.po.Active;
import org.loxf.jyadmin.dal.po.ActiveCustList;

import java.util.List;

public interface ActiveCustListMapper {
    List<ActiveCustList> queryList(String activeId);

    int insert(ActiveCustList record);

    int countByActiveId(String activeId);

    int countByCustId(String custId);

    int hasJoin(@Param("activeId") String activeId, @Param("custId") String custId);

    List<ActiveCustList> queryListByCustId(@Param("custId") String custId, @Param("start") int start, @Param("size") int size);
}