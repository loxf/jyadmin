package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.Trade;
import org.loxf.jyadmin.dal.po.TradeKey;

public interface TradeMapper {
    int deleteByPrimaryKey(TradeKey key);

    int insert(Trade record);

    int insertSelective(Trade record);

    Trade selectByPrimaryKey(TradeKey key);

    int updateByPrimaryKeySelective(Trade record);

    int updateByPrimaryKey(Trade record);
}