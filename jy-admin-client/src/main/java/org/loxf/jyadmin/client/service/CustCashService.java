package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.client.dto.CustCashDto;
import org.loxf.jyadmin.client.tmp.CustCashUpload;

import java.util.List;

public interface CustCashService {
    /**
     * @param custCashUploads 提现记录
     * @return
     */
    BaseResult initCustCash(List<CustCashUpload> custCashUploads);
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
    BaseResult<Boolean> addCustCashRecord(CustCashDto custCashDto, String password, String sign);

    /**
     * 提现审核
     * @param orderId
     * @param status
     * @return
     */
    BaseResult<Boolean> pendingCustCashRecord(String orderId, Integer status, String remark);

    /**
     * 提现交易
     * @param custCashDto
     * @return
     */
    BaseResult<Boolean> takeCashDeal(CustCashDto custCashDto);


}
