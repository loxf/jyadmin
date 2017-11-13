package org.loxf.jyadmin.web.admin;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.client.dto.CustCashDto;
import org.loxf.jyadmin.client.service.AccountService;
import org.loxf.jyadmin.client.service.CustCashService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin/account")
public class AccountController extends BaseControl {

    @Autowired
    private AccountService accountService;

    @Autowired
    private CustCashService custCashService;

    @RequestMapping("/custCashMgr")
    public String custCashMgr(){
        return "account/cash_list";
    }

    @RequestMapping("/queryCashList")
    @ResponseBody
    public PageResult<CustCashDto> queryCashList(CustCashDto custCashDto){
        initRangeDate(custCashDto);
        return custCashService.queryCustCash(custCashDto);
    }

    @RequestMapping("/pendingCustCash")
    @ResponseBody
    public BaseResult pendingCustCash(Long id, Integer status, String remark){
        return custCashService.pendingCustCashRecord(id, status, remark);
    }
}
