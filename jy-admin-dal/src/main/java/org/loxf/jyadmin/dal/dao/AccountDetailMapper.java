package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.AccountDetail;

public interface AccountDetailMapper {
    int deleteByPrimaryKey(Long id);

    int insert(AccountDetail record);

    int insertSelective(AccountDetail record);

    AccountDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AccountDetail record);

    int updateByPrimaryKey(AccountDetail record);
}