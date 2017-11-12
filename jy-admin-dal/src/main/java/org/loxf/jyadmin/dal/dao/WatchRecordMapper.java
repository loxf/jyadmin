package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.WatchRecord;

public interface WatchRecordMapper {
    int insert(WatchRecord record);

    WatchRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKey(WatchRecord record);

    int countByVideo(String videoId);
}