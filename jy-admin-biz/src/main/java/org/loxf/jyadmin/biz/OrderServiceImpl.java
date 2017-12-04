package org.loxf.jyadmin.biz;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.util.IdGenerator;
import org.loxf.jyadmin.client.dto.OrderDto;
import org.loxf.jyadmin.client.service.OrderService;
import org.loxf.jyadmin.dal.dao.CustMapper;
import org.loxf.jyadmin.dal.dao.OrderMapper;
import org.loxf.jyadmin.dal.po.Cust;
import org.loxf.jyadmin.dal.po.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("orderService")
public class OrderServiceImpl implements OrderService {
    private static String prefix = "ORD";
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private CustMapper custMapper;

    @Override
    @Transactional
    public BaseResult<String> createOrder(OrderDto orderDto) {
        // todo 订单签名
        if (orderDto == null) {
            return new BaseResult(BaseConstant.FAILED, "参数为空");
        }
        if (StringUtils.isNotBlank(orderDto.getOrderName()) || orderDto.getOrderType() == null || StringUtils.isNotBlank(orderDto.getCustId())
                || orderDto.getOrderMoney() == null || StringUtils.isNotBlank(orderDto.getObjId()) || orderDto.getPayType() == null) {
            return new BaseResult(BaseConstant.FAILED, "关键参数缺失");
        }
        Order order = new Order();
        BeanUtils.copyProperties(orderDto, order);
        order.setOrderId(IdGenerator.generate(prefix));
        if (orderMapper.insert(order) > 0) {
            return new BaseResult<>(order.getOrderId());
        } else {
            return new BaseResult<>(BaseConstant.FAILED, "新建订单失败");
        }
    }

    @Override
    public BaseResult<String> completeOrder(OrderDto orderDto) {
        return null;
    }

    @Override
    public BaseResult<OrderDto> queryOrder(String orderId) {
        Order order = orderMapper.selectByOrderId(orderId);
        if (order == null) {
            return new BaseResult<>(BaseConstant.FAILED, "订单不存在");
        }
        OrderDto orderDto = new OrderDto();
        BeanUtils.copyProperties(order, orderDto);
        return new BaseResult(orderDto);
    }

    @Override
    public PageResult<OrderDto> pager(OrderDto orderDto) {
        if (orderDto == null) {
            return new PageResult(BaseConstant.FAILED, "参数为空");
        }
        // 联系方式处理
        if(StringUtils.isNotBlank(orderDto.getContact())){
            String emailReg = "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?";
            int isChinese = 1;// 发送方式 SMS
            if(orderDto.getContact().matches(emailReg)){
                isChinese = 2;// EMAIL
            }
            Cust cust = custMapper.selectByPhoneOrEmail(isChinese, orderDto.getContact());
            if(cust==null){
                return new PageResult(1, orderDto.getPager().getPage(), 0,null);
            }
            orderDto.setCustId(cust.getCustId());
        }
        Order order = new Order();
        BeanUtils.copyProperties(orderDto, order);

        int count = orderMapper.count(order);
        List<OrderDto> result = null;
        if (count > 0) {
            List<Order> list = orderMapper.list(order);
            if (CollectionUtils.isNotEmpty(list)) {
                result = new ArrayList<>();
                for (Order tmp : list) {
                    OrderDto dto = new OrderDto();
                    BeanUtils.copyProperties(tmp, dto);
                    result.add(dto);
                }
            }
        }
        int page = count / orderDto.getPager().getSize() + (count % orderDto.getPager().getSize() > 0 ? 1 : 0);
        return new PageResult<>(page, orderDto.getPager().getPage(), count, result);
    }
}
