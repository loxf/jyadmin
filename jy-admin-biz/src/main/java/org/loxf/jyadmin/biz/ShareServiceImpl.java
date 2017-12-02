package org.loxf.jyadmin.biz;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.biz.util.ConfigUtil;
import org.loxf.jyadmin.client.dto.ShareDto;
import org.loxf.jyadmin.client.service.AccountService;
import org.loxf.jyadmin.client.service.CustBpDetailService;
import org.loxf.jyadmin.client.service.ShareService;
import org.loxf.jyadmin.dal.dao.ShareMapper;
import org.loxf.jyadmin.dal.po.Config;
import org.loxf.jyadmin.dal.po.Share;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service("shareService")
public class ShareServiceImpl implements ShareService {
    @Autowired
    private ShareMapper shareMapper;
    @Autowired
    private AccountService accountService;

    /**
     * @param custId
     * @param shareObj
     * @param type 分享类型：VIDEO/ACTIVE/RECOMMEND/PAGE
     * @return
     */
    @Override
    @Transactional
    public BaseResult shareInfo(String custId, String detailName, String shareObj, String type) {
        boolean addBp = false;
        if(shareMapper.existsByObjAndType(custId, type, shareObj)==0){
            addBp = true;
        }
        if(addBp){
            String bp;
            if(type.equals("VIDEO")) {
                bp = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "BP_SHARE_VIDEO", "10").getConfigValue();
            } else if(type.equals("ACTIVE")){
                bp = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "BP_SHARE_ACTIVE", "10").getConfigValue();
            } else if(type.equals("RECOMMEND")){
                bp = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "BP_SHARE_REGISTER", "30").getConfigValue();
            } else if(type.equals("PAGE")){
                bp = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "BP_SHARE_OTHER", "5").getConfigValue();
            } else {
                bp = "1";
            }
            // 新增积分
            BaseResult baseResult = accountService.increase(custId, null, new BigDecimal(bp), null, detailName);
            if(baseResult.getCode()==BaseConstant.FAILED){
                return baseResult;
            }
        }
        // 新增分享记录
        Share share = new Share();
        share.setCustId(custId);
        share.setShareObj(shareObj);
        share.setType(type);
        shareMapper.insert(share);
        return new BaseResult();
    }
}
