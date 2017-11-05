package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.client.dto.CustDto;

public interface CustService {
    public PageResult<CustDto> pager(CustDto custDto);
    public BaseResult<CustDto> queryCust(int type, String phoneOrEmail);
    public BaseResult updateCust(CustDto custDto);
    public PageResult<CustDto> queryChildList(int type, String custId, int page, int size);
    public BaseResult delCust(String custId);
}
