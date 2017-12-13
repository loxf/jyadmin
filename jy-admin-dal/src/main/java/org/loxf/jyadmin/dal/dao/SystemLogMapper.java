package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.SystemLog;

import java.util.List;

public interface SystemLogMapper {
    int insert(SystemLog record);

    List<SystemLog> selectList(SystemLog record);

}