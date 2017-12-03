package org.loxf.jyadmin.biz;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.client.dto.PurchasedVideoDto;
import org.loxf.jyadmin.client.service.PurchasedVideoService;
import org.loxf.jyadmin.dal.dao.PurchasedVideoMapper;
import org.loxf.jyadmin.dal.po.PurchasedVideo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("purchasedVideoService")
public class PurchasedVideoServiceImpl implements PurchasedVideoService {
    @Autowired
    private PurchasedVideoMapper purchasedVideoMapper;
    @Override
    public BaseResult<Integer> count(PurchasedVideoDto dto) {
        PurchasedVideo purchasedVideo = new PurchasedVideo();
        BeanUtils.copyProperties(dto, purchasedVideo);
        Integer count = purchasedVideoMapper.count(purchasedVideo);
        if(count==null){
            count = 0;
        }
        return new BaseResult<>(count);
    }

    @Override
    public BaseResult<Boolean> insert(PurchasedVideoDto dto) {
        PurchasedVideo purchasedVideo = new PurchasedVideo();
        BeanUtils.copyProperties(dto, purchasedVideo);
        return new BaseResult<>(purchasedVideoMapper.insert(purchasedVideo)>0);
    }
}
