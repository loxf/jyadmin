package org.loxf.jyadmin.web.admin;

import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.client.dto.CustBpDetailDto;
import org.loxf.jyadmin.client.dto.OrderDto;
import org.loxf.jyadmin.client.service.CustBpDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin/bp")
public class BpController extends BaseControl<CustBpDetailDto> {
    @Autowired
    private CustBpDetailService custBpDetailService;

    @RequestMapping("/index")
    public String index(Model model){
        return "bp/bp_list";
    }

    @RequestMapping("/list")
    @ResponseBody
    public PageResult list(CustBpDetailDto custBpDetailDto){
        initRangeDate(custBpDetailDto);
        PageResult<CustBpDetailDto> pageResult = custBpDetailService.pager(custBpDetailDto);
        return pageResult;
    }
}
