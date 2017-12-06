package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.util.weixin.bean.UserAccessToken;
import org.loxf.jyadmin.client.dto.CustDto;

public interface CustService {
    public BaseResult<String> addCust(CustDto custDto, UserAccessToken userAccessToken);
    public PageResult<CustDto> pager(CustDto custDto);
    public BaseResult<CustDto> queryCust(int type, String phoneOrEmail);
    public BaseResult<CustDto> queryCustByCustId(String custId);
    public BaseResult<CustDto> queryCustByOpenId(String openid);
    public BaseResult refreshCustByOpenId(CustDto custDto, UserAccessToken userAccessToken);
    public BaseResult updateCust(CustDto custDto);
    public PageResult<CustDto> queryChildList(int type, String custId, int page, int size);
    public BaseResult delCust(String custId);
    public BaseResult updateRecommend(String custId , String recommend);
    public BaseResult unvalidVip(String custId);
}
