package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.Order;

import java.util.List;

public interface OrderMapper {
    int insert(Order record);

    Order selectByOrderId(String orderId);

    int updateByOrderId(Order record);

    int count(Order record);

    List<Order> list(Order record);

}