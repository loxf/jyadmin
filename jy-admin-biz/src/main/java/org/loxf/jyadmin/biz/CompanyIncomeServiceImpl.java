package org.loxf.jyadmin.biz;

import org.apache.commons.collections.CollectionUtils;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.client.dto.CompanyIncomeDto;
import org.loxf.jyadmin.client.service.CompanyIncomeService;
import org.loxf.jyadmin.dal.dao.CompanyIncomeMapper;
import org.loxf.jyadmin.dal.po.CompanyIncome;
import org.loxf.jyadmin.dal.po.CompanyIncome;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
}
