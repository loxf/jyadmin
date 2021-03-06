package org.loxf.jyadmin.biz;

import com.alibaba.fastjson.JSONArray;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.util.IdGenerator;
import org.loxf.jyadmin.biz.util.ConfigUtil;
import org.loxf.jyadmin.client.dto.CustBankDto;
import org.loxf.jyadmin.client.service.CustBankService;
import org.loxf.jyadmin.dal.dao.CustBankMapper;
import org.loxf.jyadmin.dal.po.CustBank;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("custBankService")
public class CustBankServiceImpl implements CustBankService {
    @Autowired
    private CustBankMapper custBankMapper;

    @Override
    public BaseResult<CustBankDto> queryBank(String cardId) {
        CustBank custBank = custBankMapper.queryCard(cardId);
        if(custBank==null){
            return new BaseResult<>(BaseConstant.FAILED, "不存在此卡");
        }
        CustBankDto custBankDto = new CustBankDto();
        BeanUtils.copyProperties(custBank, custBankDto);
        return new BaseResult<>(custBankDto);
    }

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
        String cardId = IdGenerator.generate("CARD");
        CustBank custBank = new CustBank();
        BeanUtils.copyProperties(custBankDto, custBank);
        custBank.setCardId(cardId);
        if(custBankMapper.insert(custBank) > 0) {
            return new BaseResult(cardId);
        } else {
            return new BaseResult(BaseConstant.FAILED, "新增银行卡失败");
        }
    }

    @Override
    public BaseResult unBind(String cardId) {
        if (StringUtils.isBlank(cardId)) {
            return new BaseResult(BaseConstant.FAILED, "参数不全");
        }
        return new BaseResult(custBankMapper.unbind(cardId) > 0);
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

    @Override
    public BaseResult<List<Map<String, String>>> queryBankList() {
        String bankStr = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_PAY, "PAY_BANK_LIST", "").getConfigValue();
        if(StringUtils.isNotBlank(bankStr)){
            String [] bankArr = bankStr.split(",");
            List<Map<String, String>> result = new ArrayList<>();
            for(String tmp : bankArr){
                String[] nameAndCode = tmp.split(":");
                String name = nameAndCode[0];
                String code = nameAndCode[1];
                Map<String, String> map = new HashMap<>();
                map.put("name", name);
                map.put("code", code);
                result.add(map);
            }
            return new BaseResult<>(result);
        }
        return new BaseResult<>(BaseConstant.FAILED, "配置PAY_BANK_LIST缺失");
    }
}
