package org.loxf.jyadmin.web.admin;

import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.client.dto.AgentInfoDto;
import org.loxf.jyadmin.client.service.AgentInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin/partner")
public class PartnerController extends BaseControl<AgentInfoDto> {
    private static Logger logger = LoggerFactory.getLogger(PartnerController.class);
    @Autowired
    private AgentInfoService agentInfoService;

    @RequestMapping("/index")
    public String index(){
        return "cust/partner_list";
    }

    @RequestMapping("/list")
    @ResponseBody
    public PageResult list(AgentInfoDto agentInfoDto){
        initRangeDate(agentInfoDto);
        PageResult<AgentInfoDto> pageResult = agentInfoService.pager(agentInfoDto);
        return pageResult;
    }

}
