package org.loxf.jyadmin.dal.dao;

import org.apache.ibatis.annotations.Param;
import org.loxf.jyadmin.dal.po.Cust;

import java.util.List;

public interface CustMapper {
    int deleteCust(String id);

    int insert(Cust record);

    int insertSelective(Cust record);

    Cust selectByWX(String wx);

    Cust selectByCustId(String custId);

    Cust selectByPhoneOrEmail(@Param("isChinese") int isChinese, @Param("phone")String phone);

    int count(Cust cust);

    List<Cust> pager(Cust cust);

    List<Cust> queryChildList(@Param("list") List<String> parentId, @Param("start")int start, @Param("size") int size);

    int queryChildListCount(@Param("list")List<String> parentId);

    int updateByCustId(Cust record);
}