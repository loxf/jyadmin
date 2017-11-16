package org.loxf.jyadmin.biz;

import org.apache.commons.collections.CollectionUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.client.dto.CustBankDto;
import org.loxf.jyadmin.client.service.CustBankService;
import org.loxf.jyadmin.dal.dao.CustBankMapper;
import org.loxf.jyadmin.dal.po.CustBank;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("custBankService")
public class CustBankServiceImpl implements CustBankService {
    @Autowired
    private CustBankMapper custBankMapper;

    @Override
    public PageResult<CustBankDto> pager(CustBankDto bankDto) {
        if (bankDto == null) {
            return new PageResult<>(BaseConstant.FAILED, "参数不全");
        }
        CustBank custBank = new CustBank();
        BeanUtils.copyProperties(bankDto, custBank);
        int total = custBankMapper.count(custBank);
        List<CustBankDto> bankDtos = new ArrayList<>();
        if (total > 0) {
            List<CustBank> list = custBankMapper.pager(custBank);
            if (CollectionUtils.isNotEmpty(list)) {
                for (CustBank custBank1 : list) {
                    CustBankDto custBankDto = new CustBankDto();
                    BeanUtils.copyProperties(custBank1, custBankDto);
                    bankDtos.add(custBankDto);
                }
            }
        }
        int totalPage = total / bankDto.getPager().getSize() + (total % bankDto.getPager().getSize() == 0 ? 0 : 1);
        return new PageResult<>(totalPage, bankDto.getPager().getPage(), total, bankDtos);
    }

    @Override
    public BaseResult addBankCard(CustBankDto custBankDto) {
        if (custBankDto == null) {
            return new BaseResult(BaseConstant.FAILED, "参数不全");
        }
        CustBank custBank = new CustBank();
        BeanUtils.copyProperties(custBankDto, custBank);
        return new BaseResult(custBankMapper.insert(custBank) > 0);
    }

    @Override
    public BaseResult unBind(String custId, Long id) {
        if (custId == null || id == null) {
            return new BaseResult(BaseConstant.FAILED, "参数不全");
        }
        return new BaseResult(custBankMapper.unbind(custId, id) > 0);
    }

    @Override
    public BaseResult update(CustBankDto custBankDto) {
        if (custBankDto == null || custBankDto.getId()==null || custBankDto.getCustId()==null) {
            return new BaseResult(BaseConstant.FAILED, "参数不全");
        }
        CustBank custBank = new CustBank();
        BeanUtils.copyProperties(custBankDto, custBank);
        return new BaseResult(custBankMapper.update(custBank) > 0);
    }
}
