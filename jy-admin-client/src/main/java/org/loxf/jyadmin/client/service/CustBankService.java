package org.loxf.jyadmin.client.service;

import com.alibaba.fastjson.JSONArray;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.client.dto.CustBankDto;

import java.util.List;
import java.util.Map;

public interface CustBankService {
    BaseResult<CustBankDto> queryBank(String cardId);

    PageResult<CustBankDto> pager(CustBankDto bankDto);

    BaseResult addBankCard(CustBankDto custBankDto);

    BaseResult unBind(String cardId);

    BaseResult update(CustBankDto custBankDto);

    BaseResult<List<Map<String, String>>> queryBankList();
}
