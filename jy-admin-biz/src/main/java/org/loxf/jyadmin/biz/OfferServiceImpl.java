package org.loxf.jyadmin.biz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.util.IdGenerator;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.biz.util.ConfigUtil;
import org.loxf.jyadmin.client.dto.ConfigDto;
import org.loxf.jyadmin.client.dto.OfferDto;
import org.loxf.jyadmin.client.dto.OfferRelDto;
import org.loxf.jyadmin.client.service.OfferService;
import org.loxf.jyadmin.dal.dao.*;
import org.loxf.jyadmin.dal.po.Config;
import org.loxf.jyadmin.dal.po.Offer;
import org.loxf.jyadmin.dal.po.OfferRel;
import org.loxf.jyadmin.dal.po.WatchRecord;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("offerService")
public class OfferServiceImpl implements OfferService {

    private static String prefix = "OFFER";
    @Autowired
    private OfferMapper offerMapper;
    @Autowired
    private OfferRelMapper offerRelMapper;
    @Autowired
    private ConfigMapper configMapper;
    @Autowired
    private IndexRecommendMapper indexRecommendMapper;
    @Autowired
    private WatchRecordMapper watchRecordMapper;

    @Override
    public PageResult<OfferDto> pager(OfferDto offerDto, Integer appType){
        if(offerDto==null){
            return new PageResult<>(BaseConstant.FAILED, "入参不能为空");
        }
        Offer offer = new Offer();
        BeanUtils.copyProperties(offerDto, offer);
        int total = offerMapper.count(offer, appType);
        List<OfferDto> dtos = new ArrayList<>();
        if(total>0) {
            int basePlay = Integer.valueOf(ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_COM,
                    "BASE_PLAY_TIME", "0").getConfigValue());
            List<Offer> offerList = offerMapper.pager(offer, appType);
            if(CollectionUtils.isNotEmpty(offerList)) {
                for (Offer po : offerList) {
                    OfferDto tmp = new OfferDto();
                    BeanUtils.copyProperties(po, tmp);
                    if (appType==2 && po.getOfferType().equals("CLASS")) {
                        String videoId = po.getMainMedia();
                        WatchRecord watchRecord = new WatchRecord();
                        watchRecord.setVideoId(videoId);
                        int times = watchRecordMapper.count(watchRecord);
                        tmp.setPlayTime(basePlay + times);
                    }
                    dtos.add(tmp);
                }
            }
        }
        int totalPage = total/offerDto.getPager().getSize() + (total%offerDto.getPager().getSize()==0?0:1);
        return new PageResult<OfferDto>(totalPage, offerDto.getPager().getPage(), total, dtos);
    }

    @Override
    public BaseResult<OfferDto> queryOffer(String offerId){
        Offer offer = offerMapper.selectByOfferId(offerId);
        OfferDto dto = new OfferDto();
        BeanUtils.copyProperties(offer, dto);
        return new BaseResult<>(dto);
    }

    @Override
    @Transactional
    public BaseResult newOffer(OfferDto offerDto, List<OfferRelDto> offerRelDtos){
        Offer offer = new Offer();
        BeanUtils.copyProperties(offerDto, offer);
        String offerId = IdGenerator.generate(prefix);
        offer.setOfferId(offerId);
        if(offerMapper.insert(offer)>0){
            addOfferRel(offerId, offerRelDtos);
        }
        return new BaseResult(offerId);
    }

    @Override
    @Transactional
    public BaseResult updateOffer(OfferDto offerDto, List<OfferRelDto> offerRelDtos){
        Offer again = offerMapper.selectByOfferId(offerDto.getOfferId());
        if("OFFER".equals(again.getOfferType())){
            // 套餐要删除offerrel 再新增
            offerRelMapper.deleteByOfferIdAndRelType(offerDto.getOfferId(), "OFFER");
            addOfferRel(offerDto.getOfferId(), offerRelDtos);
        }
        Offer offer = new Offer();
        BeanUtils.copyProperties(offerDto, offer);
        offerMapper.updateByOfferId(offer);
        return new BaseResult(offerDto.getOfferId());
    }

    private void addOfferRel(String offerId, List<OfferRelDto> offerRelDtos){
        for(OfferRelDto dto : offerRelDtos) {
            OfferRel rel = new OfferRel();
            BeanUtils.copyProperties(dto, rel);
            rel.setOfferId(offerId);
            offerRelMapper.insert(rel);
        }
    }

    @Override
    @Transactional
    public BaseResult deleteOffer(String offerId){
        Offer again = offerMapper.selectByOfferId(offerId);
        indexRecommendMapper.delete(again.getOfferType(), again.getOfferId());
        return new BaseResult(offerMapper.deleteByOfferId(offerId));
    }

    @Override
    @Transactional
    public BaseResult onOrOffOffer(String offerId, Integer status){
        return new BaseResult(offerMapper.onOrOffOffer(offerId, status));
    }

    @Override
    public BaseResult<List<OfferDto>> showOfferRel(String offerId, String relType){
        List<Offer> list = offerMapper.showOfferByOfferIdAndRelType(offerId, relType);
        List<OfferDto> result = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(list)){
            for(Offer offer : list){
                OfferDto dto = new OfferDto();
                BeanUtils.copyProperties(offer, dto);
                result.add(dto);
            }
        }
        return new BaseResult<>(result);
    }

    @Override
    public BaseResult<List<OfferDto>> pagerOfferAndActive(String name){
        List<Offer> offerList = offerMapper.pagerOfferAndActive(name);
        List<OfferDto> result = new ArrayList<>();
        if(offerList!=null){
            for (Offer offer : offerList){
                OfferDto dto = new OfferDto();
                BeanUtils.copyProperties(offer, dto);
                result.add(dto);
            }
        }
        return new BaseResult(result);
    }

    @Override
    @Transactional
    public BaseResult sendIndexRecommend(String offerId, Integer type){
        Offer offer = offerMapper.selectByOfferId(offerId);
        if(offer==null){
            return new BaseResult(BaseConstant.FAILED, "商品不存在");
        }
        String metaData = offer.getMetaData();
        JSONObject metaJSON ;
        if(StringUtils.isBlank(metaData)){
            metaJSON = new JSONObject();
        } else {
            metaJSON = JSON.parseObject(metaData);
        }
        if(type==1){
            if(indexRecommendMapper.exists(offer.getOfferType(), offerId)==0) {
                indexRecommendMapper.insert(offer.getOfferType(), offerId);
            } else {
                indexRecommendMapper.updateByPrimaryKey(offer.getOfferType(), offerId);
            }
            metaJSON.put("INDEX", "on");
        } else {
            // 取消
            metaJSON.remove("INDEX");
            indexRecommendMapper.delete(offer.getOfferType(), offerId);
        }
        Offer offerRefresh = new Offer();
        offerRefresh.setOfferId(offerId);
        offerRefresh.setMetaData(metaJSON.toJSONString());
        offerMapper.updateByOfferId(offerRefresh);
        return new BaseResult();
    }

    @Override
    public BaseResult<List<String>> queryOfferByChildOffer(String childOfferId) {
        return new BaseResult<>(offerRelMapper.queryOfferIdByRelOfferId(childOfferId));
    }
}
