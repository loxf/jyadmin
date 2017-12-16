package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.client.dto.VipInfoDto;

public interface VipInfoService {
    BaseResult<VipInfoDto> queryVipInfo(String custId);
    BaseResult changeVipLevel(String custId, String userLevel);
}
