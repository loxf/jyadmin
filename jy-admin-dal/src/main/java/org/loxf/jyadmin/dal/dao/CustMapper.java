package org.loxf.jyadmin.dal.dao;

import org.apache.ibatis.annotations.Param;
import org.loxf.jyadmin.dal.po.Cust;

import java.util.List;
import java.util.Map;

public interface CustMapper {
    int deleteCust(String id);

    int insert(Cust record);

    int insertSelective(Cust record);

    Cust selectByOpenid(String openid);

    Cust selectByCustId(String custId);

    List<Cust> selectByName(String name);

    Cust selectByPhoneOrEmail(@Param("isChinese") int isChinese, @Param("phone")String phone);

    int count(Cust cust);

    List<Cust> pager(Cust cust);

    List<Cust> queryChildList(@Param("list") List<String> parentId, @Param("start")int start, @Param("size") int size);

    int queryChildListCount(@Param("list")List<String> parentId);

    int updateByCustIdOrOpenid(Cust record);

    /**
     * @param custId
     * @param type 1:一级 2:二级
     * @param isAdd 1:加 2:减
     * @return
     */
    int updateChildNbr(@Param("custId")String custId, @Param("type")int type, @Param("isAdd")int isAdd);

    List<Map> queryCustIncreaseLast7Day();

    List<Map> queryCustUserLevelDistribute();
}