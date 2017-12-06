package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;

public interface TradeService {
    BaseResult<String> completeTrade(String orderId, Integer status, String msg);
}
