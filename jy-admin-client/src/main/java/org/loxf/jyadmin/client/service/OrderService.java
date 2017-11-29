package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.client.dto.OrderDto;

public interface OrderService {
    BaseResult<String> createOrder(OrderDto orderDto);
    BaseResult<OrderDto> queryOrder(String orderId);
    PageResult<OrderDto> pager(OrderDto orderDto);
}
