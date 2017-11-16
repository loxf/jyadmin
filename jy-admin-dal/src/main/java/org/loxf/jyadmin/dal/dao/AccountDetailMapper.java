package org.loxf.jyadmin.dal.dao;

import org.apache.ibatis.annotations.Param;
import org.loxf.jyadmin.dal.po.AccountDetail;

import java.util.List;

public interface AccountDetailMapper {

    int insert(AccountDetail record);

    int count(AccountDetail record);

    List<AccountDetail> pager(AccountDetail record);

    AccountDetail selectByOrderId(String orderId);

    /**
     * 计算余额变化从dayBefore到现在的和
     * @param custId
     * @param dayBefore 0 代表当天，
     * @param type 1：收入 3：支出
     * @return
     */
    int sumByType(@Param("custId") String custId, @Param("dayBefore") Integer dayBefore, Integer type);

}