package org.loxf.jyadmin.biz;

import org.apache.commons.collections.CollectionUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.util.DateUtils;
import org.loxf.jyadmin.client.dto.CompanyIncomeDto;
import org.loxf.jyadmin.client.service.CompanyIncomeService;
import org.loxf.jyadmin.dal.dao.CompanyIncomeMapper;
import org.loxf.jyadmin.dal.po.CompanyIncome;
import org.loxf.jyadmin.dal.po.CompanyIncome;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service("companyIncomeService")
public class CompanyIncomeServiceImpl implements CompanyIncomeService {
    @Autowired
    private CompanyIncomeMapper companyIncomeMapper;

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
