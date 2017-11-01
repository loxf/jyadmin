package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.Share;

public interface ShareMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Share record);

    int insertSelective(Share record);

    Share selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Share record);

    int updateByPrimaryKey(Share record);
}