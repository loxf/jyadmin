package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.client.dto.AgentInfoDto;

public interface AgentInfoService {
    public PageResult<AgentInfoDto> pager(AgentInfoDto agentInfoDto);

}
