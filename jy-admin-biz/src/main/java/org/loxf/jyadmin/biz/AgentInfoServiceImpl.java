package org.loxf.jyadmin.biz;

import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.client.constant.BaseConstant;
import org.loxf.jyadmin.client.dto.AgentInfoDto;
import org.loxf.jyadmin.client.service.AgentInfoService;
import org.loxf.jyadmin.dal.dao.AgentInfoMapper;
import org.loxf.jyadmin.dal.po.AgentInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("agentInfoService")
public class AgentInfoServiceImpl implements AgentInfoService {
    @Autowired
    private AgentInfoMapper agentInfoMapper;

    @Override
    public PageResult<AgentInfoDto> pager(AgentInfoDto agentInfoDto) {
        if(agentInfoDto==null){
            return new PageResult<>(BaseConstant.FAILED, "参数为空");
        }
        AgentInfo agentInfo = new AgentInfo();
        BeanUtils.copyProperties(agentInfoDto, agentInfo);
        int total = agentInfoMapper.count(agentInfo);
        List<AgentInfoDto> list = new ArrayList<>();
        if(total>0) {
            List<AgentInfo> agentInfos = agentInfoMapper.pager(agentInfo);
            for(AgentInfo agent : agentInfos){
                AgentInfoDto tmp = new AgentInfoDto();
                BeanUtils.copyProperties(agent, tmp);
                list.add(tmp);
            }
        }
        int tatalPage = total/agentInfoDto.getPager().getSize() + (total%agentInfoDto.getPager().getSize()==0?0:1);
        return new PageResult<AgentInfoDto>(tatalPage, agentInfoDto.getPager().getSize(), total, list);
    }
}
