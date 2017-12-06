package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.OrderAttr;
import org.loxf.jyadmin.dal.po.OrderAttrKey;

public interface OrderAttrMapper {
    int deleteByPrimaryKey(OrderAttrKey key);

    int insert(OrderAttr record);

    int insertSelective(OrderAttr record);

    OrderAttr selectByPrimaryKey(OrderAttrKey key);

    int updateByPrimaryKeySelective(OrderAttr record);

    int updateByPrimaryKey(OrderAttr record);
}