package org.loxf.jyadmin.web.admin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.util.DateUtils;
import org.loxf.jyadmin.client.dto.AgentInfoDto;
import org.loxf.jyadmin.client.dto.ConfigDto;
import org.loxf.jyadmin.client.dto.CustDto;
import org.loxf.jyadmin.client.dto.ProvinceDto;
import org.loxf.jyadmin.client.service.AgentInfoService;
import org.loxf.jyadmin.client.service.CustService;
import org.loxf.jyadmin.client.service.ProvinceAndCityService;
import org.loxf.jyadmin.biz.util.ConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/admin/partner")
public class PartnerController extends BaseControl<AgentInfoDto> {
    private static Logger logger = LoggerFactory.getLogger(PartnerController.class);
    @Autowired
    private AgentInfoService agentInfoService;
    @Autowired
    private CustService custService;
    @Autowired
    private ProvinceAndCityService provinceAndCityService;

    @RequestMapping("/index")
    public String index(Model model){
        BaseResult<List<ProvinceDto>> baseResult = provinceAndCityService.queryProvince(null);
        model.addAttribute("provinceList", baseResult.getData());
        return "cust/partner_list";
    }

    @RequestMapping("/list")
    @ResponseBody
    public PageResult list(AgentInfoDto agentInfoDto){
        initRangeDate(agentInfoDto);
        agentInfoDto.setStatus(1);
        PageResult<AgentInfoDto> pageResult = agentInfoService.pager(agentInfoDto);
        return pageResult;
    }

    @RequestMapping("/toPend")
    public String toPend(Model model){
        BaseResult<List<ProvinceDto>> baseResult = provinceAndCityService.queryProvince(null);
        model.addAttribute("provinceList", baseResult.getData());
        return "cust/partner_pend";
    }

    @RequestMapping("/pendList")
    @ResponseBody
    public PageResult pendList(AgentInfoDto agentInfoDto){
        initRangeDate(agentInfoDto);
        PageResult<AgentInfoDto> pageResult = agentInfoService.pager(agentInfoDto);
        return pageResult;
    }

    @RequestMapping("/modifyVipNbr")
    @ResponseBody
    public BaseResult modifyVipNbr(String custId, Integer vipNbr, Integer svipNbr){
        if(StringUtils.isBlank(custId) || StringUtils.isBlank(custId)){
            return new BaseResult(0, "客户ID不能为空");
        }
        // 执行修改逻辑
        BaseResult<AgentInfoDto> agentBaseResult = agentInfoService.queryAgent(custId);
        if(agentBaseResult.getCode()== BaseConstant.SUCCESS) {
            AgentInfoDto agentInfoDto = agentBaseResult.getData();
            String metaData = agentInfoDto.getMetaData();
            JSONObject object ;
            if(StringUtils.isNotBlank(metaData)){
                object = JSON.parseObject(metaData);
            } else {
                object = new JSONObject();
            }
            if(vipNbr!=null){
                object.put("totalVIP", vipNbr);
            }
            if(svipNbr!=null){
                object.put("totalSVIP", svipNbr);
            }
            AgentInfoDto dto = new AgentInfoDto();
            dto.setCustId(custId);
            dto.setMetaData(object.toJSONString());
            return agentInfoService.updateAgent(dto);
        }
        return agentBaseResult;
    }

    @RequestMapping("/modifyAgentType")
    @ResponseBody
    public BaseResult modifyAgentType(String custId, Integer type){
        // 更新客户信息
        // 更新客户信息
        CustDto custDto = new CustDto();
        custDto.setCustId(custId);
        custDto.setIsAgent(type);
        custService.updateCust(custDto);
        // 更新代理信息
        AgentInfoDto dto = new AgentInfoDto();
        dto.setCustId(custId);
        dto.setType(type);
        return agentInfoService.updateAgent(dto);
    }

    /**
     * @param custId
     * @param status 1：通过 2：不通过
     * @return
     */
    @ResponseBody
    @RequestMapping("/isPassAgent")
    public BaseResult isPassAgent(String custId, Integer status){
        // 更新代理信息
        BaseResult<AgentInfoDto> baseResult = agentInfoService.queryAgent(custId);
        if(baseResult.getCode()==BaseConstant.FAILED){
            return baseResult;
        }
        AgentInfoDto dto = baseResult.getData();
        dto.setCustId(custId);
        dto.setStatus(status);
        AgentInfoDto params = baseResult.getData();
        if(status==1){
            // 通过，需要更新客户信息
            CustDto custDto = new CustDto();
            custDto.setCustId(custId);
            custDto.setIsAgent(dto.getType());
            custService.updateCust(custDto);

            JSONObject metaData = new JSONObject();
            // 获取不同等级代理商对应的VIP数量 生失效时间
            ConfigDto config = null;
            if(dto.getType()==1) {
                config = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "AGENT_VIP_SVIP_NBR");
            } else if(dto.getType()==2) {
                config = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "PARTNER_VIP_SVIP_NBR");
            } else if(dto.getType()==3) {
                config = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "COMPANY_VIP_SVIP_NBR");
            }
            if(config==null){
                return new BaseResult(BaseConstant.FAILED, "获取配置失败");
            }
            String cfgValue = config.getConfigValue();
            String [] arr = cfgValue.split("_");
            metaData.put("totalVIP", Integer.valueOf(arr[0]));
            metaData.put("totalSVIP", Integer.valueOf(arr[0]));
            metaData.put("useVIP", 0);
            metaData.put("useSVIP", 0);
            params.setMetaData(metaData.toJSONString());
            long days = 365;// 默认一年有效
            if(arr.length==3){
                days = Long.valueOf(arr[2]);
            }
            long nowTime = System.currentTimeMillis();
            long expireTime = nowTime + DateUtils.getDayToTime(days);
            Date effDate = DateUtils.getStartDate(new Date(nowTime));
            Date expDate = DateUtils.getEndDate(new Date(expireTime));
            params.setEffDate(DateUtils.formatHms(effDate));
            params.setExpDate(DateUtils.formatHms(expDate));
        }

        params.setCustId(custId);
        params.setStatus(status);
        return agentInfoService.updateAgent(params);
    }

    @RequestMapping("/delAgent")
    @ResponseBody
    public BaseResult delAgent(String custId){
        // 更新客户信息
        CustDto custDto = new CustDto();
        custDto.setCustId(custId);
        custDto.setIsAgent(0);
        custService.updateCust(custDto);
        return agentInfoService.delAgent(custId);
    }
}
