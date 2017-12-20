package org.loxf.jyadmin.biz;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.util.IdGenerator;
import org.loxf.jyadmin.base.util.weixin.bean.UserAccessToken;
import org.loxf.jyadmin.biz.util.BizUtil;
import org.loxf.jyadmin.biz.util.SendWeixinMsgUtil;
import org.loxf.jyadmin.client.dto.CustDto;
import org.loxf.jyadmin.client.service.CustService;
import org.loxf.jyadmin.dal.dao.*;
import org.loxf.jyadmin.dal.po.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("custService")
public class CustServiceImpl implements CustService {
    private static String prefix = "CUST";
    @Autowired
    private CustMapper custMapper;
    @Autowired
    private WxUserTokenMapper wxUserTokenMapper ;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private VipInfoMapper vipInfoMapper;
    @Autowired
    private AgentInfoMapper agentInfoMapper;

    @Override
    @Transactional
    public BaseResult<String> addCust(CustDto custDto, UserAccessToken userAccessToken){
        Cust cust = new Cust();
        BeanUtils.copyProperties(custDto, cust);
        String custId = IdGenerator.generate(prefix);
        cust.setCustId(custId);
        int count = custMapper.insert(cust);
        if(count<=0){
            return new BaseResult<>(BaseConstant.FAILED, "更新失败");
        }
        WxUserToken wxUserToken = new WxUserToken();
        BeanUtils.copyProperties(userAccessToken, wxUserToken);
        // 设置userToken 30天刷新时间
        wxUserToken.setRefresh_time((System.currentTimeMillis() + 30*24*60*60*1000) + "");
        wxUserTokenMapper.insert(wxUserToken);
        // 推荐人
        if (StringUtils.isNotBlank(custDto.getRecommend())) {
            updateRecommendChildNbr(custDto.getRecommend(), 1);
        }
        // 用户账户
        Account account = new Account();
        account.setCustId(custId);
        accountMapper.insert(account);
        // 注册通知
        SendWeixinMsgUtil.sendRegisterNotice(custDto.getOpenid(), custDto.getNickName());
        return new BaseResult<>(custId);
    }

    @Override
    public PageResult<CustDto> pager(CustDto custDto) {
        if(custDto==null){
            return new PageResult<>(BaseConstant.FAILED, "入参不能为空");
        }
        Cust cust = new Cust();
        BeanUtils.copyProperties(custDto, cust);
        int total = custMapper.count(cust);
        List<CustDto> dtos = new ArrayList<>();
        if(total>0) {
            List<Cust> custList = custMapper.pager(cust);
            convertCust(custList, dtos);
        }
        int totalPage = total/custDto.getPager().getSize() + (total%custDto.getPager().getSize()==0?0:1);
        return new PageResult<CustDto>(totalPage, custDto.getPager().getSize(), total, dtos);
    }

    @Override
    public BaseResult<CustDto> queryCust(int isChinese, String phoneOrEmail){
        Cust cust = custMapper.selectByPhoneOrEmail(isChinese, phoneOrEmail);
        if(cust==null){
            return new BaseResult<>(BaseConstant.FAILED, "会员不存在");
        }
        CustDto tmp = new CustDto();
        BeanUtils.copyProperties(cust, tmp);
        return new BaseResult<>(tmp);
    }

    @Override
    public BaseResult<CustDto> queryOldCust(String phone) {
        Cust cust = custMapper.selectOldCust(phone);
        if(cust==null){
            return new BaseResult<>(BaseConstant.FAILED, "老客户不存在");
        }
        CustDto custDto = new CustDto();
        BeanUtils.copyProperties(cust, custDto);
        return new BaseResult<>(custDto);
    }

    @Override
    public BaseResult<CustDto> queryCustByCustId(String custId){
        Cust cust = custMapper.selectByCustId(custId);
        if(cust==null){
            return new BaseResult<>(BaseConstant.FAILED, "会员不存在");
        }
        CustDto tmp = new CustDto();
        BeanUtils.copyProperties(cust, tmp);
        return new BaseResult<>(tmp);
    }

    @Override
    public BaseResult<CustDto> queryCustByOpenId(String openid){
        Cust cust = custMapper.selectByOpenid(openid);
        if(cust==null){
            return new BaseResult<>(BaseConstant.FAILED, "会员不存在");
        }
        CustDto tmp = new CustDto();
        BeanUtils.copyProperties(cust, tmp);
        return new BaseResult<>(tmp);
    }

    @Override
    @Transactional
    public BaseResult refreshCustByOpenId(CustDto custDto, UserAccessToken userAccessToken){
        Cust cust = new Cust();
        BeanUtils.copyProperties(custDto, cust);
        int count = custMapper.updateByCustIdOrOpenid(cust);
        if(count<=0){
            return new BaseResult<>(BaseConstant.FAILED, "更新失败");
        }
        // 更新user_token
        WxUserToken wxUserToken = new WxUserToken();
        BeanUtils.copyProperties(userAccessToken, wxUserToken);
        // 设置userToken 30天刷新时间
        wxUserToken.setRefresh_time((System.currentTimeMillis() + 30*24*60*60*1000) + "");
        wxUserTokenMapper.updateByPrimaryKey(wxUserToken);
        return new BaseResult<>();
    }

    @Override
    @Transactional
    public BaseResult updateCust(CustDto custDto){
        if(custDto==null || StringUtils.isBlank(custDto.getCustId())){
            return new BaseResult<>(BaseConstant.FAILED, "参数不全");
        }
        Cust tmp = new Cust();
        BeanUtils.copyProperties(custDto, tmp);
        custMapper.updateByCustIdOrOpenid(tmp);
        return new BaseResult();
    }

    @Override
    @Transactional
    public BaseResult updateRecommend(String custId, String recommend){
        if( StringUtils.isBlank(custId) || StringUtils.isBlank(recommend)){
            return new BaseResult<>(BaseConstant.FAILED, "参数不全");
        }
        // 获取客户信息
        Cust cust = custMapper.selectByCustId(custId);
        // 原推荐人处理
        if(StringUtils.isNotBlank(cust.getRecommend())){
            updateRecommendChildNbr(cust.getRecommend(), 2);
        }
        Cust tmp = new Cust();
        tmp.setCustId(custId);
        tmp.setRecommend(recommend);
        // 更新当前客户的推荐人信息
        custMapper.updateByCustIdOrOpenid(tmp);
        // 更新推荐人的信息
        updateRecommendChildNbr(recommend, 1);
        return new BaseResult();
    }

    @Override
    @Transactional
    public BaseResult unvalidVip(String custId) {
        // 更新VIP信息表
        VipInfo vipInfo = new VipInfo();
        vipInfo.setCustId(custId);
        vipInfo.setStatus(3);
        vipInfoMapper.updateByCustId(vipInfo);
        Cust cust = new Cust();
        cust.setCustId(custId);
        cust.setUserLevel("NONE");
        custMapper.updateByCustIdOrOpenid(cust);
        return new BaseResult();
    }

    @Override
    @Transactional
    public BaseResult unvalidAgent(String custId) {
        // 更新代理信息表
        AgentInfo agentInfo = new AgentInfo();
        agentInfo.setCustId(custId);
        agentInfo.setStatus(3);
        agentInfoMapper.updateByCustId(agentInfo);
        Cust cust = new Cust();
        cust.setCustId(custId);
        cust.setIsAgent(0);
        custMapper.updateByCustIdOrOpenid(cust);
        return new BaseResult();
    }

    @Override
    public BaseResult queryCustLevelNbr() {
        List<Map> data = custMapper.queryCustUserLevelDistribute();
        // 初始化最近七天数据
        return new BaseResult(BizUtil.getArrData(data));
    }

    @Override
    public BaseResult queryCustIncrease() {
        List<Map> data = custMapper.queryCustIncreaseLast7Day();
        // 初始化最近七天数据
        return new BaseResult(BizUtil.getDataByDate(data));
    }

    /**
     * @param recommendId
     * @param isAdd 1:加 2:减
     */
    private void updateRecommendChildNbr(String recommendId, int isAdd){
        Cust recommendCust = custMapper.selectByCustId(recommendId);
        if(recommendCust!=null) {
            // 原推荐人不为空
            custMapper.updateChildNbr(recommendId, 1, isAdd);
            if(StringUtils.isNotBlank(recommendCust.getRecommend())){
                custMapper.updateChildNbr(recommendCust.getRecommend(), 2, isAdd);
            }
        }
    }

    /**
     * @param type 1:直接同学 2:间接同学
     * @param custId
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageResult<CustDto> queryChildList(int type, String custId, int page, int size){
        List<String> recommends = new ArrayList<>();
        // 获取自己
        recommends.add(custId);
        if(type==2){
            // 获取所有的一级同学
            List<Cust> firstList = custMapper.queryChildList(recommends, 0, 10000);
            if(CollectionUtils.isEmpty(firstList)){
                return new PageResult(1, page, 0, null);
            } else {
                recommends.clear();
                for(Cust tmp : firstList){
                    recommends.add(tmp.getCustId());
                }
            }
        }
        int total = custMapper.queryChildListCount(recommends);
        List<CustDto> resultList = null;
        if(total>0){
            resultList = new ArrayList<>();
            if(page<=0){
                page = 1;
            }
            int start = (page-1)*size;
            List<Cust> list = custMapper.queryChildList(recommends, start, size);
            convertCust(list, resultList);
        }
        int totalPage = total/size + (total%size==0?0:1);
        return new PageResult<>(totalPage, page, total, resultList);
    }

    private void convertCust(List<Cust> list, List<CustDto> resultList){
        if(CollectionUtils.isNotEmpty(list)) {
            for (Cust tmp : list) {
                CustDto dto = new CustDto();
                BeanUtils.copyProperties(tmp, dto);
                resultList.add(dto);
            }
        }
    }

    @Override
    @Transactional
    public BaseResult delCust(String custId) {
        // 获取客户信息
        Cust cust = custMapper.selectByCustId(custId);
        updateRecommendChildNbr(cust.getRecommend(), 2);
        return new BaseResult(custMapper.deleteCust(custId));
    }

    @Override
    @Transactional
    public BaseResult delOldCust(String custId) {
        // 获取客户信息
        Cust cust = custMapper.selectByCustId(custId);
        return new BaseResult(custMapper.deleteCust(custId));
    }


}
