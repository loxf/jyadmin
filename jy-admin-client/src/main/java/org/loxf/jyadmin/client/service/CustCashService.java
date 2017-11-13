package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.client.dto.CustCashDto;

import java.util.List;

public interface CustCashService {
    /**
     * @param custCashDto 获取提现记录
     * @return
     */
    PageResult<CustCashDto> queryCustCash(CustCashDto custCashDto);

    /**
     * @param custCashDto
     * @param password 支付密码
     * @return
     */
    BaseResult<Boolean> addCustCashRecord(CustCashDto custCashDto, String password);

    /**
     * 提现审核
     * @param recordId
     * @param status
     * @return
     */
    BaseResult<Boolean> pendingCustCashRecord(Long recordId, Integer status, String remark);
}
