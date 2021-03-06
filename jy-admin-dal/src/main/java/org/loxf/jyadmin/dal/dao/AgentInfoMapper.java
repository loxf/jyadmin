package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.AgentInfo;

import java.util.HashMap;
import java.util.List;

public interface AgentInfoMapper {
    int insert(AgentInfo record);

    int insertList(List<AgentInfo> record);

    int updateByCustId(AgentInfo record);

    AgentInfo selectByCustId(String custId);

    List<AgentInfo> pager(AgentInfo record);

    int count(AgentInfo record);

    int delete(String custId);

    int exists(String custId);

    List<AgentInfo> queryExpireInfo();

    List<HashMap> queryWillExpireInfo();
}