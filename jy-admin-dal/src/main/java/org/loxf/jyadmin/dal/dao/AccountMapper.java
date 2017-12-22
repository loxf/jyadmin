package org.loxf.jyadmin.dal.dao;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Param;
import org.loxf.jyadmin.dal.po.Account;
import org.loxf.jyadmin.dal.po.Cust;

import java.util.HashMap;
import java.util.List;

public interface AccountMapper {

    int insert(Account record);

    int insertList(List<Account> list);

    Account selectAccount(String custId);

    int deleteByCustId(String custId);

    int lockAccount(String custId);

    int unlockAccount(String custId);

    int updateBalanceOrBp(Account record);

    int setPayPassword(@Param("custId") String custId, @Param("password")String password);

    List<HashMap> selectBpTop10();

    int queryBpRankingByCustId(String custId);

    int queryBalanceListCount(Cust cust);

    List<JSONObject> queryBalanceList(Cust cust);
}