package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.util.weixin.bean.UserAccessToken;
import org.loxf.jyadmin.client.dto.CustDto;

public interface CustService {
    BaseResult<String> addCust(CustDto custDto, UserAccessToken userAccessToken);
    PageResult<CustDto> pager(CustDto custDto);
    BaseResult<CustDto> queryCust(int type, String phoneOrEmail);

    /**
     * 根据电话/邮箱获取老客户
     * @param phoneOrEmail
     * @return
     */
    BaseResult<CustDto> queryOldCust(String phoneOrEmail);
    BaseResult<CustDto> queryCustByCustId(String custId);
    BaseResult<CustDto> queryCustByOpenId(String openid);
    BaseResult refreshCustByOpenId(CustDto custDto, UserAccessToken userAccessToken);
    BaseResult updateCust(CustDto custDto);
    PageResult<CustDto> queryChildList(int type, String custId, int page, int size);
    BaseResult delCust(String custId);
    BaseResult delOldCust(String custId);
    BaseResult updateRecommend(String custId , String recommend);
    BaseResult unvalidVip(String custId);
    BaseResult unvalidAgent(String custId);

    /**
     * 根据用户等级获取不同等级的数量
     * @return
     */
    BaseResult queryCustLevelNbr();
    /**
     * 获取最近一周客户增长数量
     * @return
     */
    BaseResult queryCustIncrease();
}
