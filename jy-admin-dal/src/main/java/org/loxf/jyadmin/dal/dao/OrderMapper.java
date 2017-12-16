package org.loxf.jyadmin.dal.dao;

import org.apache.ibatis.annotations.Param;
import org.loxf.jyadmin.dal.po.Order;

import java.util.List;
import java.util.Map;

public interface OrderMapper {
    int insert(Order record);

    Order selectByOrderId(String orderId);

    int updateByOrderId(@Param("orderId") String orderId, @Param("outTradeNo") String outTradeNo, @Param("status")Integer status, @Param("msg")String msg);

    int count(Order record);

    List<Order> list(Order record);

    List<Order> queryTimeoutOrder();

    List<Map> queryOrderDistributeByLast7Day();
}