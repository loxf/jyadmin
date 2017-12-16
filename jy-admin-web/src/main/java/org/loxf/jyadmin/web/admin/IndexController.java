package org.loxf.jyadmin.web.admin;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.client.service.CompanyIncomeService;
import org.loxf.jyadmin.client.service.CustService;
import org.loxf.jyadmin.client.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin/indexData")
public class IndexController {
    @Autowired
    private CustService custService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CompanyIncomeService companyIncomeService;
    @RequestMapping("/custLevel")
    @ResponseBody
    public BaseResult queryCustLevelNbr(){
        return custService.queryCustLevelNbr();
    }

    @RequestMapping("/companyIncome")
    @ResponseBody
    public BaseResult queryCompanyIncome(){
        return companyIncomeService.queryCompanyIncome();
    }

    @RequestMapping("/scholarship")
    @ResponseBody
    public BaseResult queryScholarship(){
        return companyIncomeService.queryScholarship();
    }

    @RequestMapping("/custIncrease")
    @ResponseBody
    public BaseResult queryCustIncrease(){
        return custService.queryCustIncrease();
    }

    @RequestMapping("/orderIncrease")
    @ResponseBody
    public BaseResult queryOrderIncrease(){
        return orderService.queryOrderIncrease();
    }
}
