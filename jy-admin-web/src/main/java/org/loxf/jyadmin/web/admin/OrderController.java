package org.loxf.jyadmin.web.admin;

import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.client.dto.OfferDto;
import org.loxf.jyadmin.client.dto.OrderDto;
import org.loxf.jyadmin.client.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin/order")
public class OrderController extends BaseControl<OrderDto> {
    private static Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @RequestMapping("/index")
    public String index(Model model){
        return "order/order_list";
    }
    @RequestMapping("/list")
    @ResponseBody
    public PageResult list(OrderDto orderDto){
        initRangeDate(orderDto);
        PageResult<OrderDto> pageResult = orderService.pager(orderDto);
        return pageResult;
    }

}
