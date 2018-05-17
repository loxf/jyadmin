package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.util.weixin.bean.UserAccessToken;
import org.loxf.jyadmin.base.util.weixin.bean.XCXLoginInfo;
import org.loxf.jyadmin.client.dto.CustDto;
import org.loxf.jyadmin.client.tmp.CustInfoUpload;

import java.util.List;

public interface CustService {
    BaseResult<String> addOldCust(List<CustInfoUpload> custInfoUploads);
    BaseResult<String> updateOldCustRecommend();
    @Deprecated
    BaseResult<String> addCust(CustDto custDto, UserAccessToken userAccessToken);

    /**
     * @param custDto
     * @param loginInfo 登录信息
     * @return
     */
    BaseResult<String> addCust(CustDto custDto, Object loginInfo);
    PageResult<CustDto> searchPage(String keyword, Integer page, Integer size);
    PageResult<CustDto> pager(CustDto custDto);
    BaseResult<CustDto> queryCust(int type, String phoneOrEmail);
    BaseResult bindCust(CustDto custDto);
    /**
     * 根据电话/邮箱获取老客户
     * @param phoneOrEmail
     * @return
     */
    BaseResult<CustDto> queryOldCust(String phoneOrEmail);
    BaseResult<CustDto> queryCustByCustId(String custId);
    @Deprecated
    BaseResult<CustDto> queryCustByOpenId(String openid);
    BaseResult<CustDto> queryCustByUnionId(String unionid);

    /**
     * @param custDto
     * @param loginInfo 登录信息
     * @return
     */
    BaseResult refreshCustByUnionId(CustDto custDto, Object loginInfo);
    BaseResult updateCust(CustDto custDto);
    PageResult<CustDto> queryChildList(int type, String custId, int page, int size);
    BaseResult delCust(String custId);
    BaseResult delTmpCust(String custId);
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

    BaseResult updateOldCustInfo(CustDto custDto);
}
