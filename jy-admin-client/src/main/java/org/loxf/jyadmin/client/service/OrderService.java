package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.client.dto.OrderDto;

import java.util.Map;

public interface OrderService {
    BaseResult<Map<String, String>> createOrder(OrderDto orderDto);
    BaseResult<String> completeOrder(OrderDto orderDto);
    BaseResult<OrderDto> queryOrder(String orderId);
    PageResult<OrderDto> pager(OrderDto orderDto);

    /**
     * 是否已经购买
     * @param custId
     * @param type
     * @param obj
     * @return
     */
    BaseResult hasBuy(String custId, int type, String obj);
}
