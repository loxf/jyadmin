package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.CustBank;

import java.util.List;

public interface CustBankMapper {

    CustBank queryCard(String cardId);

    int count(CustBank record);

    List<CustBank> pager(CustBank record);

    int insert(CustBank record);

    int insertList(List<CustBank> list);

    int update(CustBank record);

    int unbind(String cardId);
}