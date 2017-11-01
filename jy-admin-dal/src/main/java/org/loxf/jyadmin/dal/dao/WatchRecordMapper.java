package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.WatchRecord;

public interface WatchRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(WatchRecord record);

    int insertSelective(WatchRecord record);

    WatchRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(WatchRecord record);

    int updateByPrimaryKey(WatchRecord record);
}