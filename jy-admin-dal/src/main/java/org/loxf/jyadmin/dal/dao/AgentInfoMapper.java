package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.AgentInfo;

import java.util.List;

public interface AgentInfoMapper {
    int insert(AgentInfo record);

    AgentInfo selectByCustId(Long id);

    List<AgentInfo> pager(AgentInfo record);

    int count(AgentInfo record);

    int updateByPrimaryKeySelective(AgentInfo record);

    int updateByPrimaryKey(AgentInfo record);
}