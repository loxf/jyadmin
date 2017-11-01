package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.ActiveCustList;

import java.util.List;

public interface ActiveCustListMapper {
    List<ActiveCustList> queryList(String activeId);

    int insert(ActiveCustList record);

    int countByActiveId(String activeId);
}