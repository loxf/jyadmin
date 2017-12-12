package org.loxf.jyadmin.web.admin;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.client.dto.AccountDetailDto;
import org.loxf.jyadmin.client.dto.CompanyIncomeDto;
import org.loxf.jyadmin.client.dto.CustCashDto;
import org.loxf.jyadmin.client.dto.CustDto;
import org.loxf.jyadmin.client.service.AccountDetailService;
import org.loxf.jyadmin.client.service.AccountService;
import org.loxf.jyadmin.client.service.CompanyIncomeService;
import org.loxf.jyadmin.client.service.CustCashService;
import org.loxf.jyadmin.dal.po.AccountDetail;
import org.loxf.jyadmin.dal.po.CompanyIncome;
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
    private AccountDetailService accountDetailService;
    @Autowired
    private CustCashService custCashService;
    @Autowired
    private CompanyIncomeService companyIncomeService;

    /**
     * 余额管理
     *
     * @return
     */
    @RequestMapping("/accountBalanceMgr")
    public String accountBalanceMgr() {
        return "account/balance_list";
    }

    /**
     * 提现管理
     *
     * @return
     */
    @RequestMapping("/custCashMgr")
    public String custCashMgr() {
        return "account/cash_list";
    }

    /**
     * 银行卡管理
     *
     * @return
     */
    @RequestMapping("/custBankcard")
    public String custBankcard() {
        return "account/bank_list";
    }

    /**
     * 用户收入
     *
     * @return
     */
    @RequestMapping("/userIncomeMgr")
    public String userIncomeMgr() {
        return "account/user_income_list";
    }

    /**
     * 公司收入
     *
     * @return
     */
    @RequestMapping("/companyIncomeMgr")
    public String companyIncomeMgr() {
        return "account/company_income_list";
    }

    /**
     * 用户余额列表
     *
     * @param custDto
     * @param contact
     * @return
     */
    @RequestMapping("/queryCustBalanceList")
    @ResponseBody
    public PageResult<JSONObject> queryCustBalanceList(CustDto custDto, String contact) {
        if(StringUtils.isNotBlank(contact)) {
            String emailReg = "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?";
            int isChinese = 1;// 发送方式 SMS
            if (contact.matches(emailReg)) {
                isChinese = 2;// EMAIL
                custDto.setEmail(contact);
            } else {
                custDto.setPhone(contact);
            }
            custDto.setIsChinese(isChinese);
        }
        return accountService.queryBalanceList(custDto);
    }

    @RequestMapping("/queryCashList")
    @ResponseBody
    public PageResult<CustCashDto> queryCashList(CustCashDto custCashDto) {
        initRangeDate(custCashDto);
        return custCashService.queryCustCash(custCashDto);
    }

    @RequestMapping("/pendingCustCash")
    @ResponseBody
    public BaseResult pendingCustCash(String orderId, Integer status, String remark) {
        return custCashService.pendingCustCashRecord(orderId, status, remark);
    }

    @RequestMapping("/queryCompanyIncomeList")
    @ResponseBody
    public PageResult<CompanyIncomeDto> queryCompanyIncomeList(CompanyIncomeDto companyIncomeDto) {
        initRangeDate(companyIncomeDto);
        return companyIncomeService.pager(companyIncomeDto);
    }

    @RequestMapping("/queryUserIncomeList")
    @ResponseBody
    public PageResult<AccountDetailDto> queryUserIncomeList(AccountDetailDto accountDetailDto) {
        initRangeDate(accountDetailDto);
        accountDetailDto.setType(1);// 收入
        return accountDetailService.queryDetails(accountDetailDto);
    }
}
