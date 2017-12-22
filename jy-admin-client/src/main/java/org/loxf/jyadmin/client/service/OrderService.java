package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.client.dto.OrderAttrDto;
import org.loxf.jyadmin.client.dto.OrderDto;
import org.loxf.jyadmin.client.tmp.OrderInfoUpload;

import java.util.List;
import java.util.Map;

public interface OrderService {
    BaseResult createOldOrder(List<OrderInfoUpload> orderInfoUploadList);
    BaseResult<Map<String, String>> createOrder(String openid, String ip, OrderDto orderDto, List<OrderAttrDto> orderAttrDtoList);
    BaseResult<String> completeOrder(String orderId, String partnerOrderId, Integer status, String msg);
    BaseResult<OrderDto> queryOrder(String orderId);
    PageResult<OrderDto> pager(OrderDto orderDto);

    /**
     * 是否已经购买
     * @param custId
     * @param type
     * @param obj
     * @return
     */
    BaseResult<Boolean> hasBuy(String custId, int type, String obj);
    BaseResult queryOrderIncrease();
}
