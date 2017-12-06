package org.loxf.jyadmin.dal.dao;

import org.apache.ibatis.annotations.Param;
import org.loxf.jyadmin.dal.po.OrderAttr;

import java.util.List;

public interface OrderAttrMapper {

    int insertList(@Param("attrList") List<OrderAttr> attrList, @Param("orderId")String orderId);

    List<OrderAttr> selectByOrderId(String orderId);
}