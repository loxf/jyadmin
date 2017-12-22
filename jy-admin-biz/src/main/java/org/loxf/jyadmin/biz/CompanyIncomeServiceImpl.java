package org.loxf.jyadmin.biz;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.util.DateUtils;
import org.loxf.jyadmin.client.dto.CompanyIncomeDto;
import org.loxf.jyadmin.client.service.CompanyIncomeService;
import org.loxf.jyadmin.client.tmp.IncomeUpload;
import org.loxf.jyadmin.dal.dao.AccountDetailMapper;
import org.loxf.jyadmin.dal.dao.CompanyIncomeMapper;
import org.loxf.jyadmin.dal.dao.CustMapper;
import org.loxf.jyadmin.dal.po.AccountDetail;
import org.loxf.jyadmin.dal.po.CompanyIncome;
import org.loxf.jyadmin.dal.po.CompanyIncome;
import org.loxf.jyadmin.dal.po.Cust;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service("companyIncomeService")
public class CompanyIncomeServiceImpl implements CompanyIncomeService {
    @Autowired
    private CompanyIncomeMapper companyIncomeMapper;
    @Autowired
    private AccountDetailMapper accountDetailMapper;
    @Autowired
    private CustMapper custMapper;

    @Override
    public BaseResult initCompanyIncome(List<IncomeUpload> list) {
        List<CompanyIncome> companyIncomes = new ArrayList<>();
        int i=0;
        Map<String, Cust> phoneAndCustMap = new HashMap<>();
        for(IncomeUpload incomeUpload : list){
            CompanyIncome companyIncome = new CompanyIncome();
            companyIncome.setScholarship(BigDecimal.ZERO);
            companyIncome.setAmount(new BigDecimal(incomeUpload.getAmount()));
            companyIncome.setSource("公司收入老数据" + (i++));
            String typeName = incomeUpload.getType();
            String detailName = incomeUpload.getType();
            int type = 1;
            if(StringUtils.isNotBlank(typeName)) {
                if (typeName.equals("独立商城")) {
                    detailName = "购买课程";
                }
                if (typeName.indexOf("VIP") >= 0) {
                    type = 3;
                } else if (typeName.indexOf("活动") >= 0) {
                    type = 5;
                }
            }
            companyIncome.setType(type);
            companyIncome.setDetailName(detailName);
            Cust cust ;
            if(phoneAndCustMap.containsKey(incomeUpload.getPhone())){
                cust = phoneAndCustMap.get(incomeUpload.getPhone());
            } else {
                cust = custMapper.selectByPhoneOrEmail(1, incomeUpload.getPhone());
                phoneAndCustMap.put(incomeUpload.getPhone(), cust);
            }
            companyIncome.setCustName(cust.getNickName());
            companyIncome.setCustId(cust.getCustId());
            companyIncome.setCreatedAt(DateUtils.toDate(incomeUpload.getDate(), "yyyy-MM-dd HH:mm:ss"));
            companyIncomes.add(companyIncome);
        }
        // 记录公司收入表
        companyIncomeMapper.insertList(companyIncomes);
        return new BaseResult();
    }

    @Override
    public BaseResult initScholarshipIncome(List<IncomeUpload> list) {
        List<CompanyIncome> companyIncomes = new ArrayList<>();
        List<AccountDetail> accountDetails = new ArrayList<>();
        int i=0;
        Map<String, Cust> phoneAndCustMap = new HashMap<>();
        for(IncomeUpload incomeUpload : list){
            CompanyIncome companyIncome = new CompanyIncome();
            companyIncome.setScholarship(new BigDecimal(incomeUpload.getAmount()));
            companyIncome.setAmount(BigDecimal.ZERO);
            companyIncome.setSource("用户收入老数据" + (i++));
            String typeName = incomeUpload.getType();
            String detailName = incomeUpload.getType();
            int type = 1;
            if(StringUtils.isNotBlank(typeName)) {
                if (typeName.equals("独立商城")) {
                    detailName = "购买课程";
                }
                if (typeName.indexOf("VIP") >= 0) {
                    type = 3;
                } else if (typeName.indexOf("活动") >= 0) {
                    type = 5;
                }
            }
            companyIncome.setType(type);
            companyIncome.setDetailName(detailName);
            // 消费者
            Cust cust ;
            // 获益者
            Cust custSource ;
            if(phoneAndCustMap.containsKey(incomeUpload.getPhone())){
                cust = phoneAndCustMap.get(incomeUpload.getPhone());
            } else {
                cust = custMapper.selectByPhoneOrEmail(1, incomeUpload.getPhone());
                phoneAndCustMap.put(incomeUpload.getPhone(), cust);
            }
            if(phoneAndCustMap.containsKey(incomeUpload.getSourcePhone())){
                custSource = phoneAndCustMap.get(incomeUpload.getSourcePhone());
            } else {
                custSource = custMapper.selectByPhoneOrEmail(1, incomeUpload.getSourcePhone());
                phoneAndCustMap.put(incomeUpload.getSourcePhone(), custSource);
            }
            companyIncome.setCustName(cust.getNickName());
            companyIncome.setCustId(cust.getCustId());
            companyIncome.setBeneficiary(custSource.getCustId());// 受益人
            companyIncome.setCreatedAt(DateUtils.toDate(incomeUpload.getDate(), "yyyy-MM-dd HH:mm:ss"));
            companyIncomes.add(companyIncome);

            AccountDetail accountDetail = new AccountDetail();
            accountDetail.setCustId(custSource.getCustId());
            accountDetail.setOrderId(companyIncome.getSource());
            accountDetail.setDetailName(companyIncome.getDetailName() + "(奖学金)");
            accountDetail.setType(1);
            accountDetail.setChangeBalance(companyIncome.getScholarship());
            accountDetail.setCreatedAt(companyIncome.getCreatedAt());
            accountDetails.add(accountDetail);
        }
        // 记录公司收入表
        companyIncomeMapper.insertList(companyIncomes);
        // 记录账户明细表
        accountDetailMapper.insertList(accountDetails);
        return new BaseResult();
    }

    @Override
    public PageResult<CompanyIncomeDto> pager(CompanyIncomeDto companyIncomeDto) {
        if (companyIncomeDto == null) {
            return new PageResult<>(BaseConstant.FAILED, "参数为空");
        }
        CompanyIncome companyIncome = new CompanyIncome();
        BeanUtils.copyProperties(companyIncomeDto, companyIncome);
        int total = companyIncomeMapper.count(companyIncome);
        List<CompanyIncomeDto> result = new ArrayList<>();
        if (total > 0) {
            List<CompanyIncome> list = companyIncomeMapper.list(companyIncome);
            if (CollectionUtils.isNotEmpty(list)) {
                for (CompanyIncome tmp : list) {
                    CompanyIncomeDto dto = new CompanyIncomeDto();
                    BeanUtils.copyProperties(tmp, dto);
                    result.add(dto);
                }
            }
        }
        int totalPage = total / companyIncomeDto.getPager().getSize() + (total % companyIncomeDto.getPager().getSize() == 0 ? 0 : 1);
        return new PageResult(totalPage, companyIncomeDto.getPager().getPage(), total, result);
    }

    @Override
    public BaseResult queryCompanyIncome() {
        // 获取今天
        Date now = new Date();
        Date lastWeek = DateUtils.getAddTime(now, Calendar.DATE, -7);
        Date lastMonth = DateUtils.getAddTime(now, Calendar.DATE, -30);
        double todayIncome = companyIncomeMapper.queryIncome(DateUtils.format(now));
        double weekIncome = companyIncomeMapper.queryIncome(DateUtils.format(lastWeek));
        double monthIncome = companyIncomeMapper.queryIncome(DateUtils.format(lastMonth));
        double allIncome = companyIncomeMapper.queryIncome(null);
        String [][] result = new String [4][2];
        result[0] = getArr("公司净收入", allIncome);
        result[1] = getArr("最近三十天", monthIncome);
        result[2] = getArr("最近七天", weekIncome);
        result[3] = getArr("今日收入", todayIncome);
        return new BaseResult(result);
    }

    @Override
    public BaseResult queryScholarship() {
        // 获取今天
        Date now = new Date();
        Date lastWeek = DateUtils.getAddTime(now, Calendar.DATE, -7);
        Date lastMonth = DateUtils.getAddTime(now, Calendar.DATE, -30);
        double today = companyIncomeMapper.queryScholarship(DateUtils.format(now));
        double week = companyIncomeMapper.queryScholarship(DateUtils.format(lastWeek));
        double month = companyIncomeMapper.queryScholarship(DateUtils.format(lastMonth));
        double all = companyIncomeMapper.queryScholarship(null);
        String [][] result = new String [4][2];
        result[0] = getArr("总奖学金", all);
        result[1] = getArr("最近三十天", month);
        result[2] = getArr("最近七天", week);
        result[3] = getArr("今日收入", today);
        return new BaseResult(result);
    }

    private String[] getArr(String name, double value){
        String [] tmp = new String[2];
        tmp[0] = name;
        tmp[1] = value + "";
        return tmp;
    }
}
