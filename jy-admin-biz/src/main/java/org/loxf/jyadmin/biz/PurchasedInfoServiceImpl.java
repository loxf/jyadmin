package org.loxf.jyadmin.biz;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.client.dto.PurchasedInfoDto;
import org.loxf.jyadmin.client.service.PurchasedInfoService;
import org.loxf.jyadmin.dal.dao.PurchasedInfoMapper;
import org.loxf.jyadmin.dal.po.PurchasedInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("purchasedInfoService")
public class PurchasedInfoServiceImpl implements PurchasedInfoService {
    @Autowired
    private PurchasedInfoMapper purchasedInfoMapper;
    @Override
    public BaseResult<Integer> count(PurchasedInfoDto dto) {
        PurchasedInfo purchasedInfo = new PurchasedInfo();
        BeanUtils.copyProperties(dto, purchasedInfo);
        Integer count = purchasedInfoMapper.count(purchasedInfo);
        if(count==null){
            count = 0;
        }
        return new BaseResult<>(count);
    }

    @Override
    public BaseResult<Boolean> insert(PurchasedInfoDto dto) {
        PurchasedInfo purchasedInfo = new PurchasedInfo();
        BeanUtils.copyProperties(dto, purchasedInfo);
        return new BaseResult<>(purchasedInfoMapper.insert(purchasedInfo)>0);
    }
}
