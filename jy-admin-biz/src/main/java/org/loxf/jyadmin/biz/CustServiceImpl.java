package org.loxf.jyadmin.biz;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.bean.Pager;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.util.DateUtils;
import org.loxf.jyadmin.base.util.IdGenerator;
import org.loxf.jyadmin.base.util.weixin.bean.UserAccessToken;
import org.loxf.jyadmin.base.util.weixin.bean.XCXLoginInfo;
import org.loxf.jyadmin.biz.util.BizUtil;
import org.loxf.jyadmin.biz.util.ConfigUtil;
import org.loxf.jyadmin.biz.util.SendWeixinMsgUtil;
import org.loxf.jyadmin.client.dto.CustDto;
import org.loxf.jyadmin.client.service.AccountService;
import org.loxf.jyadmin.client.service.CustService;
import org.loxf.jyadmin.client.tmp.CustInfoUpload;
import org.loxf.jyadmin.dal.dao.*;
import org.loxf.jyadmin.dal.po.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service("custService")
public class CustServiceImpl implements CustService {
    private static String prefix = "CUST";
    @Autowired
    private CustMapper custMapper;
    @Autowired
    private WxUserTokenMapper wxUserTokenMapper;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private VipInfoMapper vipInfoMapper;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AgentInfoMapper agentInfoMapper;
    @Autowired
    private XcxSessionkeyMapper xcxSessionkeyMapper;

    @Value("#{configProperties['JYZX.INDEX.URL']}")
    private String JYZX_INDEX_URL;


    @Override
    @Transactional
    public BaseResult<String> addOldCust(List<CustInfoUpload> custInfoUploads) {
        if (CollectionUtils.isNotEmpty(custInfoUploads)) {
            List<Cust> custList = new ArrayList<>();
            List<VipInfo> vipInfoList = new ArrayList<>();
            List<AgentInfo> agentInfoList = new ArrayList<>();
            List<Account> accountList = new ArrayList<>();
            for (CustInfoUpload custInfoUpload : custInfoUploads) {
                Cust cust = new Cust();
                String custId = IdGenerator.generate("OLD");
                cust.setCustId(custId);
                if (StringUtils.isNotBlank(custInfoUpload.getAdminSet()) && custInfoUpload.getAdminSet().equals("是")) {
                    JSONObject json = new JSONObject();
                    json.put("ADMINSET", true);
                    cust.setMetaData(json.toJSONString());
                } else if (StringUtils.isNotBlank(custInfoUpload.getAdminSet()) && custInfoUpload.getAdminSet().equals("否")) {
                    JSONObject json = new JSONObject();
                    json.put("ADMINSET", false);
                    cust.setMetaData(json.toJSONString());
                }
                Date now = new Date();
                if (StringUtils.isNotBlank(custInfoUpload.getUserLevel()) && custInfoUpload.getUserLevel().equals("普通会员")) {
                    cust.setUserLevel("NONE");
                    cust.setIsAgent(0);
                } else if (StringUtils.isNotBlank(custInfoUpload.getUserLevel()) && custInfoUpload.getUserLevel().equals("VIP")) {
                    cust.setUserLevel("VIP");
                    cust.setIsAgent(0);
                    vipInfoList.add(createVipInfo(custId, custInfoUpload.getUserLevel(), custInfoUpload.getVipDate(), now));
                } else if (StringUtils.isNotBlank(custInfoUpload.getUserLevel()) && custInfoUpload.getUserLevel().equals("SVIP")) {
                    cust.setUserLevel("SVIP");
                    cust.setIsAgent(0);
                    vipInfoList.add(createVipInfo(custId, custInfoUpload.getUserLevel(), custInfoUpload.getVipDate(), now));
                } else if (StringUtils.isNotBlank(custInfoUpload.getUserLevel()) && custInfoUpload.getUserLevel().equals("代理商")) {
                    cust.setUserLevel("SVIP");
                    cust.setIsAgent(1);
                    vipInfoList.add(createVipInfo(custId, custInfoUpload.getUserLevel(), custInfoUpload.getVipDate(), now));
                    agentInfoList.add(createAgentInfo(custId, 1, custInfoUpload.getPhone(), custInfoUpload.getVipDate(), now));
                } else if (StringUtils.isNotBlank(custInfoUpload.getUserLevel()) && custInfoUpload.getUserLevel().equals("合伙人")) {
                    cust.setUserLevel("SVIP");
                    cust.setIsAgent(2);
                    vipInfoList.add(createVipInfo(custId, custInfoUpload.getUserLevel(), custInfoUpload.getVipDate(), now));
                    agentInfoList.add(createAgentInfo(custId, 2, custInfoUpload.getPhone(), custInfoUpload.getVipDate(), now));
                } else if (StringUtils.isNotBlank(custInfoUpload.getUserLevel()) && custInfoUpload.getUserLevel().equals("分公司")) {
                    cust.setUserLevel("SVIP");
                    cust.setIsAgent(3);
                    vipInfoList.add(createVipInfo(custId, custInfoUpload.getUserLevel(), custInfoUpload.getVipDate(), now));
                    agentInfoList.add(createAgentInfo(custId, 3, custInfoUpload.getPhone(), custInfoUpload.getVipDate(), now));
                }
                cust.setRecommend(custInfoUpload.getRecommend());
                cust.setIsChinese(1);
                cust.setPhone(custInfoUpload.getPhone());
                cust.setNickName(custInfoUpload.getNickName());
                cust.setCreatedAt(DateUtils.toDate(custInfoUpload.getCreatedAt(), "yyyy-MM-dd HH:mm:ss"));
                custList.add(cust);
                Account account = new Account();
                account.setCustId(custId);
                account.setBp(new BigDecimal(custInfoUpload.getBp()));
                account.setBalance(new BigDecimal(custInfoUpload.getBalance()));
                accountList.add(account);
            }
            insertOldData(custList, vipInfoList, agentInfoList, accountList);
            return new BaseResult<>("新增客户" + custInfoUploads.size() + "位");
        }
        return new BaseResult<>(BaseConstant.FAILED, "无数据");
    }

    @Override
    @Transactional
    public BaseResult<String> updateOldCustRecommend() {
        List<Cust> custList = custMapper.queryOldCustDealRecommend();
        if(CollectionUtils.isNotEmpty(custList)){
            // 更新推荐人
            for(Cust cust : custList){
                String phone = cust.getRecommend();
                Cust recommendCust = custMapper.selectByPhoneOrEmail(1, phone);
                if(recommendCust!=null) {
                    cust.setRecommend(recommendCust.getCustId());
                    custMapper.updateByCustId(cust);
                }
            }
            // 更新下级数量
            for(Cust cust : custList){
                if(StringUtils.isNotBlank(cust.getRecommend())) {
                    updateRecommendChildNbr(cust.getRecommend(), 1);
                }
            }
        }
        return new BaseResult<>();
    }

    public void insertOldData(List<Cust> custList, List<VipInfo> vipInfoList,
                              List<AgentInfo> agentInfoList, List<Account> accountList) {
        if (CollectionUtils.isNotEmpty(custList)) {
            for (int i=0; i<custList.size();) {
                int end = (i+500)>custList.size()?custList.size():(i+500);
                custMapper.insertList(custList.subList(i, end));
                i = end;
            }
        }
        if (CollectionUtils.isNotEmpty(accountList)) {
            for (int i=0; i<accountList.size();) {
                int end = (i+500)>accountList.size()?accountList.size():(i+500);
                accountMapper.insertList(accountList.subList(i, end));
                i = end;
            }
        }
        if (CollectionUtils.isNotEmpty(agentInfoList)) {
            agentInfoMapper.insertList(agentInfoList);
        }
        if (CollectionUtils.isNotEmpty(vipInfoList)) {
            vipInfoMapper.insertList(vipInfoList);
        }
    }

    private VipInfo createVipInfo(String custId, String userLevel, String vipDate, Date now) {
        VipInfo vipInfo = new VipInfo();
        vipInfo.setType(userLevel);
        vipInfo.setEffDate(DateUtils.toDate(vipDate, "yyyy-MM-dd HH:mm:ss"));
        Date end = DateUtils.getAddTime(now, Calendar.DATE, 365);
        vipInfo.setExpDate(end);
        vipInfo.setCustId(custId);
        vipInfo.setStatus(1);
        return vipInfo;
    }

    private AgentInfo createAgentInfo(String custId, Integer type, String phone, String effDate, Date now) {
        AgentInfo agentInfo = new AgentInfo();
        agentInfo.setType(type);
        agentInfo.setEffDate(effDate);
        Date end = DateUtils.getAddTime(now, Calendar.DATE, 365);
        agentInfo.setExpDate(DateUtils.formatHms(end));
        agentInfo.setCustId(custId);
        agentInfo.setStatus(1);
        agentInfo.setPhone(phone);
        return agentInfo;
    }

    @Override
    @Transactional
    public BaseResult<String> addCust(CustDto custDto, UserAccessToken userAccessToken) {
        Cust cust = new Cust();
        BeanUtils.copyProperties(custDto, cust);
        String custId = IdGenerator.generate(prefix);
        cust.setCustId(custId);
        cust.setIsAgent(0);
        cust.setUserLevel("NONE");
        int count = custMapper.insert(cust);
        if (count <= 0) {
            return new BaseResult<>(BaseConstant.FAILED, "更新失败");
        }
        WxUserToken wxUserToken = new WxUserToken();
        BeanUtils.copyProperties(userAccessToken, wxUserToken);
        // 设置userToken 30天刷新时间
        wxUserToken.setRefresh_time((System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000) + "");
        if(wxUserTokenMapper.selectByPrimaryKey(userAccessToken.getOpenid())!=null){
            wxUserTokenMapper.updateByPrimaryKey(wxUserToken);
        } else {
            wxUserTokenMapper.insert(wxUserToken);
        }
        // 用户账户
        Account account = new Account();
        account.setCustId(custId);
        accountMapper.insert(account);

        // 推荐人
        if (StringUtils.isNotBlank(custDto.getRecommend())) {
            updateRecommendChildNbr(custDto.getRecommend(), 1);
            String bp = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_BP, "SUB_BIND_PHONE_BP", "10").getConfigValue();
            accountService.increase(custDto.getRecommend(), null, new BigDecimal(bp), null, "推荐同学注册得积分", custDto.getCustId());
        }
        // 注册通知
        SendWeixinMsgUtil.sendRegisterNotice(custDto.getOpenid(), custDto.getNickName(), JYZX_INDEX_URL);
        return new BaseResult<>(custId);
    }

    @Override
    public BaseResult<String> addCust(CustDto custDto, Object loginInfo) {
        Cust cust = new Cust();
        BeanUtils.copyProperties(custDto, cust);
        String custId = IdGenerator.generate(prefix);
        cust.setCustId(custId);
        cust.setIsAgent(0);
        cust.setUserLevel("NONE");
        // 处理授权信息
        boolean isWxGZH = false;
        if(loginInfo instanceof XCXLoginInfo){
            // 小程序刷新用户信息
            cust.setXcxOpenid(((XCXLoginInfo) loginInfo).getOpenid());
            cust.setUnionid(((XCXLoginInfo) loginInfo).getUnionid());
            ((XCXLoginInfo) loginInfo).setCustId(custId);
            return refreshXCXLoginInfo((XCXLoginInfo)loginInfo);
        } else if(loginInfo instanceof UserAccessToken){
            isWxGZH = true;
            cust.setOpenid(((UserAccessToken) loginInfo).getOpenid());
            cust.setUnionid(((UserAccessToken) loginInfo).getUnionid());
            return refreshUserAccessToken((UserAccessToken)loginInfo);
        }
        int count = custMapper.insert(cust);
        if (count <= 0) {
            return new BaseResult<>(BaseConstant.FAILED, "新增客户失败");
        }
        // 用户账户
        Account account = new Account();
        account.setCustId(custId);
        accountMapper.insert(account);

        // 推荐人
        if (StringUtils.isNotBlank(custDto.getRecommend())) {
            updateRecommendChildNbr(custDto.getRecommend(), 1);
            String bp = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_BP, "SUB_BIND_PHONE_BP", "10").getConfigValue();
            accountService.increase(custDto.getRecommend(), null, new BigDecimal(bp), null, "推荐同学注册得积分", custDto.getCustId());
        }
        // 注册通知
        if(isWxGZH) {
            SendWeixinMsgUtil.sendRegisterNotice(custDto.getOpenid(), custDto.getNickName(), JYZX_INDEX_URL);
        }
        return new BaseResult<>(custId);
    }

    @Override
    public PageResult<CustDto> searchPage(String keyword, Integer page, Integer size) {
        if (StringUtils.isBlank(keyword)) {
            return new PageResult<>(BaseConstant.FAILED, "入参不能为空");
        }
        if(page==null){
            page = 1;
        }
        if(size==null){
            size = 20;
        }
        Cust cust = new Cust();
        cust.setPhone(keyword);
        cust.setNickName(keyword);
        cust.setEmail(keyword);
        cust.setRealName(keyword);
        Pager pager = new Pager(page, size);
        cust.setPager(pager);
        int total = custMapper.searchCount(cust);
        List<CustDto> dtos = new ArrayList<>();
        if (total > 0) {
            List<Cust> custList = custMapper.searchPager(cust);
            convertCust(custList, dtos);
        }
        int totalPage = total / cust.getPager().getSize() + (total % cust.getPager().getSize() == 0 ? 0 : 1);
        return new PageResult<CustDto>(totalPage, cust.getPager().getSize(), total, dtos);
    }
    @Override
    public PageResult<CustDto> pager(CustDto custDto) {
        if (custDto == null) {
            return new PageResult<>(BaseConstant.FAILED, "入参不能为空");
        }
        Cust cust = new Cust();
        BeanUtils.copyProperties(custDto, cust);
        int total = custMapper.count(cust);
        List<CustDto> dtos = new ArrayList<>();
        if (total > 0) {
            List<Cust> custList = custMapper.pager(cust);
            convertCust(custList, dtos);
        }
        int totalPage = total / custDto.getPager().getSize() + (total % custDto.getPager().getSize() == 0 ? 0 : 1);
        return new PageResult<CustDto>(totalPage, custDto.getPager().getSize(), total, dtos);
    }

    @Override
    public BaseResult<CustDto> queryCust(int isChinese, String phoneOrEmail) {
        Cust cust = custMapper.selectByPhoneOrEmail(isChinese, phoneOrEmail);
        if (cust == null) {
            return new BaseResult<>(BaseConstant.FAILED, "会员不存在");
        }
        CustDto tmp = new CustDto();
        BeanUtils.copyProperties(cust, tmp);
        return new BaseResult<>(tmp);
    }

    @Override
    @Transactional
    public BaseResult bindCust(CustDto custDto) {
        Cust cust = custMapper.selectByPhoneOrEmail(custDto.getIsChinese(), (custDto.getIsChinese()==1?custDto.getPhone():custDto.getEmail()));
        if(cust!=null){
            return new BaseResult(BaseConstant.FAILED, "当前" + (custDto.getIsChinese()==1?"手机":"邮箱") + "已被绑定，请更换。");
        }
        return updateCust(custDto);
    }

    @Override
    public BaseResult<CustDto> queryOldCust(String phone) {
        Cust cust = custMapper.selectOldCust(phone);
        if (cust == null) {
            return new BaseResult<>(BaseConstant.FAILED, "老客户不存在");
        }
        CustDto custDto = new CustDto();
        BeanUtils.copyProperties(cust, custDto);
        return new BaseResult<>(custDto);
    }

    @Override
    public BaseResult<CustDto> queryCustByCustId(String custId) {
        Cust cust = custMapper.selectByCustId(custId);
        if (cust == null) {
            return new BaseResult<>(BaseConstant.FAILED, "会员不存在");
        }
        CustDto tmp = new CustDto();
        BeanUtils.copyProperties(cust, tmp);
        return new BaseResult<>(tmp);
    }

    @Override
    public BaseResult<CustDto> queryCustByOpenId(String openid) {
        Cust cust = custMapper.selectByOpenid(openid);
        if (cust == null) {
            return new BaseResult<>(BaseConstant.FAILED, "会员不存在");
        }
        CustDto tmp = new CustDto();
        BeanUtils.copyProperties(cust, tmp);
        return new BaseResult<>(tmp);
    }

    @Override
    public BaseResult<CustDto> queryCustByUnionId(String unionid) {
        Cust cust = custMapper.selectByUnionid(unionid);
        if (cust == null) {
            return new BaseResult<>(BaseConstant.FAILED, "会员不存在");
        }
        CustDto tmp = new CustDto();
        BeanUtils.copyProperties(cust, tmp);
        return new BaseResult<>(tmp);
    }

    @Transactional
    public BaseResult refreshUserAccessToken(UserAccessToken userAccessToken) {
        // 更新user_token
        WxUserToken wxUserToken = new WxUserToken();
        BeanUtils.copyProperties(userAccessToken, wxUserToken);
        // 设置userToken 30天刷新时间
        wxUserToken.setRefresh_time((System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000) + "");
        if(wxUserTokenMapper.selectByPrimaryKey(wxUserToken.getOpenid())!=null) {
            wxUserTokenMapper.updateByPrimaryKey(wxUserToken);
        } else {
            wxUserTokenMapper.insert(wxUserToken);
        }
        return new BaseResult<>();
    }

    @Transactional
    public BaseResult refreshXCXLoginInfo(XCXLoginInfo loginInfo){
        XcxSessionkey xcxSessionkey = new XcxSessionkey();
        BeanUtils.copyProperties(loginInfo, xcxSessionkey);
        xcxSessionkey.setSessionKey(loginInfo.getSession_key());
        if(xcxSessionkeyMapper.selectByCustId(loginInfo.getCustId())!=null) {
            xcxSessionkeyMapper.updateByCustId(xcxSessionkey);
        } else {
            xcxSessionkeyMapper.insert(xcxSessionkey);
        }
        return new BaseResult<>();
    }

    @Override
    @Transactional
    public BaseResult refreshCustByUnionId(CustDto custDto, Object loginInfo) {
        Cust cust = new Cust();
        BeanUtils.copyProperties(custDto, cust);
        int count = custMapper.updateByUnionId(cust);
        if (count <= 0) {
            return new BaseResult<>(BaseConstant.FAILED, "更新失败");
        }
        if(loginInfo instanceof XCXLoginInfo){
            // 小程序刷新用户信息
            return refreshXCXLoginInfo((XCXLoginInfo)loginInfo);
        } else if(loginInfo instanceof UserAccessToken){
            return refreshUserAccessToken((UserAccessToken)loginInfo);
        }
        return new BaseResult(BaseConstant.FAILED, "登录信息不正确");
    }

    @Override
    @Transactional
    public BaseResult updateCust(CustDto custDto) {
        if (custDto == null || StringUtils.isBlank(custDto.getCustId())) {
            return new BaseResult<>(BaseConstant.FAILED, "参数不全");
        }
        if("NONE".equals(custDto.getUserLevel())) {
            custDto.setUserLevel(null);
        }
        Cust tmp = new Cust();
        BeanUtils.copyProperties(custDto, tmp);
        custMapper.updateByCustId(tmp);
        return new BaseResult();
    }

    @Override
    @Transactional
    public BaseResult updateRecommend(String custId, String recommend) {
        if (StringUtils.isBlank(custId) || StringUtils.isBlank(recommend)) {
            return new BaseResult<>(BaseConstant.FAILED, "参数不全");
        }
        // 获取客户信息
        Cust cust = custMapper.selectByCustId(custId);
        // 原推荐人处理
        if (StringUtils.isNotBlank(cust.getRecommend())) {
            updateRecommendChildNbr(cust.getRecommend(), 2);
        }
        Cust tmp = new Cust();
        tmp.setCustId(custId);
        tmp.setRecommend(recommend);
        // 更新当前客户的推荐人信息
        custMapper.updateByCustId(tmp);
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
        cust.setMetaData("");
        custMapper.updateByCustId(cust);
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
        custMapper.updateByCustId(cust);
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

    @Override
    public BaseResult updateOldCustInfo(CustDto custDto) {
        Cust cust = new Cust();
        BeanUtils.copyProperties(custDto, cust);
        custMapper.updateOldCustInfo(cust);
        return new BaseResult();
    }

    /**
     * @param recommendId
     * @param isAdd       1:加 2:减
     */
    private void updateRecommendChildNbr(String recommendId, int isAdd) {
        Cust recommendCust = custMapper.selectByCustId(recommendId);
        if (recommendCust != null) {
            // 原推荐人不为空
            custMapper.updateChildNbr(recommendId, 1, isAdd);
            if (StringUtils.isNotBlank(recommendCust.getRecommend())) {
                custMapper.updateChildNbr(recommendCust.getRecommend(), 2, isAdd);
            }
        }
    }

    /**
     * @param type   1:直接同学 2:间接同学
     * @param custId
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageResult<CustDto> queryChildList(int type, String custId, int page, int size) {
        List<String> recommends = new ArrayList<>();
        // 获取自己
        recommends.add(custId);
        if (type == 2) {
            // 获取所有的一级同学
            List<Cust> firstList = custMapper.queryChildList(recommends, 0, 10000);
            if (CollectionUtils.isEmpty(firstList)) {
                return new PageResult(1, page, 0, null);
            } else {
                recommends.clear();
                for (Cust tmp : firstList) {
                    recommends.add(tmp.getCustId());
                }
            }
        }
        int total = custMapper.queryChildListCount(recommends);
        List<CustDto> resultList = null;
        if (total > 0) {
            resultList = new ArrayList<>();
            if (page <= 0) {
                page = 1;
            }
            int start = (page - 1) * size;
            List<Cust> list = custMapper.queryChildList(recommends, start, size);
            convertCust(list, resultList);
        }
        int totalPage = total / size + (total % size == 0 ? 0 : 1);
        return new PageResult<>(totalPage, page, total, resultList);
    }

    private void convertCust(List<Cust> list, List<CustDto> resultList) {
        if (CollectionUtils.isNotEmpty(list)) {
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
        accountMapper.deleteByCustId(custId);
        return new BaseResult(custMapper.deleteCust(custId));
    }

    @Override
    @Transactional
    public BaseResult delTmpCust(String custId) {
        // 获取客户信息
        return new BaseResult(custMapper.deleteCust(custId));
    }


}
