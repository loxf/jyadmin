package org.loxf.jyadmin.client.service;

import com.alibaba.fastjson.JSONArray;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.client.dto.CustBankDto;

public interface CustBankService {
    PageResult<CustBankDto> pager(CustBankDto bankDto);

    BaseResult addBankCard(CustBankDto custBankDto);

    BaseResult unBind(String cardId);

    BaseResult update(CustBankDto custBankDto);

    BaseResult<String[]> queryBankList();
}
