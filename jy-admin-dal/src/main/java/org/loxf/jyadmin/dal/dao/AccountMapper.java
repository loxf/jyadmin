package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.Account;

public interface AccountMapper {

    int insert(Account record);

    Account selectAccount(String custId);

    int deleteByCustId(String custId);

    int lockAccount(Account record);

    int updateBalanceOrBp(Account record);
}