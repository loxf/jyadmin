package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.client.dto.AgentInfoDto;

public interface AgentInfoService {
    PageResult<AgentInfoDto> pager(AgentInfoDto agentInfoDto);
    BaseResult<AgentInfoDto> queryAgent(String custId);
    BaseResult<String> addAgent(AgentInfoDto agentInfoDto);
    BaseResult updateAgent(AgentInfoDto agentInfoDto);
    BaseResult delAgent(String custId);
}
