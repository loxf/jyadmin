package org.loxf.jyadmin.biz;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.client.dto.AgentInfoDto;
import org.loxf.jyadmin.client.service.AgentInfoService;
import org.loxf.jyadmin.client.service.ProvinceAndCityService;
import org.loxf.jyadmin.dal.dao.AgentInfoMapper;
import org.loxf.jyadmin.dal.po.AgentInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("agentInfoService")
public class AgentInfoServiceImpl implements AgentInfoService {
    @Autowired
    private AgentInfoMapper agentInfoMapper;
    @Autowired
    private ProvinceAndCityService provinceAndCityService;

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
            if(CollectionUtils.isNotEmpty(agentInfos)) {
                for (AgentInfo agent : agentInfos) {
                    AgentInfoDto tmp = new AgentInfoDto();
                    BeanUtils.copyProperties(agent, tmp);
                    if(StringUtils.isNotBlank(tmp.getProvince())) {
                        tmp.setProvince(provinceAndCityService.query("P", tmp.getProvince()).getData());
                    }
                    if(StringUtils.isNotBlank(tmp.getCity())) {
                        tmp.setCity(provinceAndCityService.query("C", tmp.getCity()).getData());
                    }
                    list.add(tmp);
                }
            }
        }
        int totalPage = total/agentInfoDto.getPager().getSize() + (total%agentInfoDto.getPager().getSize()==0?0:1);
        return new PageResult<AgentInfoDto>(totalPage, agentInfoDto.getPager().getSize(), total, list);
    }

    @Override
    public BaseResult<AgentInfoDto> queryAgent(String custId) {
        AgentInfo agentInfo = agentInfoMapper.selectByCustId(custId);
        if(agentInfo==null){
            return new BaseResult<>(BaseConstant.FAILED, "无代理信息");
        }
        AgentInfoDto dto = new AgentInfoDto();
        BeanUtils.copyProperties(agentInfo, dto);
        return new BaseResult<>(dto);
    }

    @Override
    @Transactional
    public BaseResult<String> addAgent(AgentInfoDto agentInfoDto) {
        if(agentInfoDto==null){
            return new BaseResult<>(BaseConstant.FAILED, "参数不全");
        }
        AgentInfo agentInfo = new AgentInfo();
        BeanUtils.copyProperties(agentInfoDto, agentInfo);
        if(agentInfoMapper.exists(agentInfo.getCustId())>0){
            return new BaseResult<>(BaseConstant.FAILED, "已是代理商或正在申请代理商");
        }
        if(agentInfoMapper.insert(agentInfo)>0) {
            return new BaseResult<>(agentInfoDto.getCustId());
        } else {
            return new BaseResult<>(BaseConstant.FAILED, "新增失败");
        }
    }

    @Override
    @Transactional
    public BaseResult updateAgent(AgentInfoDto agentInfoDto) {
        if(agentInfoDto==null){
            return new BaseResult<>(BaseConstant.FAILED, "参数不全");
        }
        AgentInfo agentInfo = new AgentInfo();
        BeanUtils.copyProperties(agentInfoDto, agentInfo);
        return new BaseResult(agentInfoMapper.updateByCustId(agentInfo));
    }

    @Override
    public BaseResult delAgent(String custId) {
        agentInfoMapper.delete(custId);
        return new BaseResult();
    }
}
