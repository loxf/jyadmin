package org.loxf.jyadmin.biz;

import org.apache.commons.collections.CollectionUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.biz.util.IdGenerator;
import org.loxf.jyadmin.client.constant.BaseConstant;
import org.loxf.jyadmin.client.dto.OfferDto;
import org.loxf.jyadmin.client.dto.OfferRelDto;
import org.loxf.jyadmin.client.service.OfferService;
import org.loxf.jyadmin.dal.dao.OfferMapper;
import org.loxf.jyadmin.dal.dao.OfferRelMapper;
import org.loxf.jyadmin.dal.po.Offer;
import org.loxf.jyadmin.dal.po.OfferRel;
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

    @Override
    public PageResult<OfferDto> pager(OfferDto offerDto){
        if(offerDto==null){
            return new PageResult<>(BaseConstant.FAILED, "入参不能为空");
        }
        Offer offer = new Offer();
        BeanUtils.copyProperties(offerDto, offer);
        int total = offerMapper.count(offer);
        List<OfferDto> dtos = new ArrayList<>();
        if(total>0) {
            List<Offer> custList = offerMapper.pager(offer);
            for(Offer po : custList){
                OfferDto tmp = new OfferDto();
                BeanUtils.copyProperties(po, tmp);
                dtos.add(tmp);
            }
        }
        int tatalPage = total/offerDto.getPager().getSize() + (total%offerDto.getPager().getSize()==0?0:1);
        return new PageResult<OfferDto>(tatalPage, offerDto.getPager().getPage(), total, dtos);
    }

    @Override
    public OfferDto queryOffer(String offerId){
        Offer offer = offerMapper.selectByOfferId(offerId);
        OfferDto dto = new OfferDto();
        BeanUtils.copyProperties(offer, dto);
        return dto;
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
    public BaseResult deleteOffer(String offerId){
        return new BaseResult(offerMapper.deleteByOfferId(offerId));
    }

    @Override
    public BaseResult onOrOffOffer(String offerId, Integer status){
        return new BaseResult(offerMapper.onOrOffOffer(offerId, status));
    }

    @Override
    public BaseResult<List<OfferDto>> showOffer(String offerId, String relType){
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
}
