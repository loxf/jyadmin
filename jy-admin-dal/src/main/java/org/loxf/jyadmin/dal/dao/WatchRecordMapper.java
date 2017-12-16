package org.loxf.jyadmin.dal.dao;

import org.apache.ibatis.annotations.Param;
import org.loxf.jyadmin.dal.po.WatchRecord;

import java.util.List;

public interface WatchRecordMapper {
    List<WatchRecord> queryNeedAddBp();

    int insert(WatchRecord record);

    WatchRecord selectByPrimaryKey(Long id);

    int updateMinutes(String watchId);

    int updateStatus(String watchId);

    int count(WatchRecord record);

    List<WatchRecord> list(WatchRecord record);
}