package org.loxf.jyadmin.dal.dao;

import org.apache.ibatis.annotations.Param;
import org.loxf.jyadmin.dal.po.Trade;

import java.util.List;

public interface TradeMapper {
    int insert(Trade record);

    Trade selectByOrderId(String orderId);

    List<Trade> selectList(@Param("status") Integer status, @Param("size") Integer size);

    int updateByOrderId(@Param("orderId") String orderId, @Param("status")Integer status, @Param("msg")String msg);
}