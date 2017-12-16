package org.loxf.jyadmin.biz;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.util.DateUtils;
import org.loxf.jyadmin.biz.util.ConfigUtil;
import org.loxf.jyadmin.client.dto.VipInfoDto;
import org.loxf.jyadmin.client.service.VipInfoService;
import org.loxf.jyadmin.dal.dao.VipInfoMapper;
import org.loxf.jyadmin.dal.po.VipInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

@Service("vipInfoService")
public class VipInfoServiceImpl implements VipInfoService {
    @Autowired
    private VipInfoMapper vipInfoMapper;

    @Override
    public BaseResult<VipInfoDto> queryVipInfo(String custId) {
        VipInfo vipInfo = vipInfoMapper.selectByCustId(custId);
        if(vipInfo==null){
            return new BaseResult<>(BaseConstant.FAILED, "无VIP信息");
        }
        VipInfoDto vipInfoDto = new VipInfoDto();
        BeanUtils.copyProperties(vipInfo, vipInfoDto);
        return new BaseResult<>(vipInfoDto);
    }

    @Override
    @Transactional
    public BaseResult changeVipLevel(String custId, String userLevel) {
        VipInfo vipInfo = vipInfoMapper.selectByCustId(custId);
        if (vipInfo != null) {
            if ("NONE".equals(userLevel)) {
                Date now = new Date();
                if (vipInfo.getStatus() == 1 && vipInfo.getEffDate().before(now) && vipInfo.getExpDate().after(now)) {
                    vipInfo.setStatus(3);
                    vipInfo.setExpDate(now);
                }
            } else {
                vipInfo.setType(userLevel);
                vipInfo.setStatus(1);
                Date now = new Date();
                vipInfo.setEffDate(now);
                Date end = DateUtils.getAddTime(now, Calendar.DATE, getValidDay(userLevel));
                vipInfo.setExpDate(DateUtils.getEndDate(end));
            }
            vipInfoMapper.updateByCustId(vipInfo);
        } else {
            if (!"NONE".equals(userLevel)) {
                vipInfo = new VipInfo();
                vipInfo.setCustId(custId);
                vipInfo.setStatus(1);
                vipInfo.setType(userLevel);
                Date now = new Date();
                vipInfo.setEffDate(now);
                Date end = DateUtils.getAddTime(now, Calendar.DATE, getValidDay(userLevel));
                vipInfo.setExpDate(DateUtils.getEndDate(end));
                vipInfoMapper.insert(vipInfo);
            }
        }
        return new BaseResult();
    }
    public int getValidDay(String vipType) {
        int validDay = 0;
        if (vipType.equals("VIP")) {
            validDay = Integer.valueOf(ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_PAY,
                    "VIP_VALID_DAY", "365").getConfigValue());
        } else {
            validDay = Integer.valueOf(ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_PAY,
                    "SVIP_VALID_DAY", "365").getConfigValue());
        }
        return validDay;
    }
}
