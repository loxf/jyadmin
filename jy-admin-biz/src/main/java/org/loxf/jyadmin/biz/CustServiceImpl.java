package org.loxf.jyadmin.biz;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.util.IdGenerator;
import org.loxf.jyadmin.base.util.weixin.bean.UserAccessToken;
import org.loxf.jyadmin.client.dto.CustDto;
import org.loxf.jyadmin.client.service.CustService;
import org.loxf.jyadmin.dal.dao.AccountMapper;
import org.loxf.jyadmin.dal.dao.CustMapper;
import org.loxf.jyadmin.dal.dao.WxUserTokenMapper;
import org.loxf.jyadmin.dal.po.Account;
import org.loxf.jyadmin.dal.po.Cust;
import org.loxf.jyadmin.dal.po.WxUserToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("custService")
public class CustServiceImpl implements CustService {
    private static String prefix = "CUST";
    @Autowired
    private CustMapper custMapper;
    @Autowired
    private WxUserTokenMapper wxUserTokenMapper ;
    @Autowired
    private AccountMapper accountMapper;

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
            for(Cust po : custList){
                CustDto tmp = new CustDto();
                BeanUtils.copyProperties(po, tmp);
                dtos.add(tmp);
            }
        }
        int tatalPage = total/custDto.getPager().getSize() + (total%custDto.getPager().getSize()==0?0:1);
        return new PageResult<CustDto>(tatalPage, custDto.getPager().getSize(), total, dtos);
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
            for(Cust tmp : list){
                CustDto dto = new CustDto();
                BeanUtils.copyProperties(tmp, dto);
                resultList.add(dto);
            }
        }
        int tatalPage = total/size + (total%size==0?0:1);
        return new PageResult<>(tatalPage, page, total, resultList);
    }

    @Override
    @Transactional
    public BaseResult delCust(String custId) {
        // 获取客户信息
        Cust cust = custMapper.selectByCustId(custId);
        updateRecommendChildNbr(cust.getRecommend(), 2);
        return new BaseResult(custMapper.deleteCust(custId));
    }
}
