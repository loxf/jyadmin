package org.loxf.jyadmin.biz;

import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.biz.util.IdGenerator;
import org.loxf.jyadmin.client.constant.BaseConstant;
import org.loxf.jyadmin.client.dto.OfferCatalogDto;
import org.loxf.jyadmin.client.service.OfferCatalogService;
import org.loxf.jyadmin.dal.dao.OfferCatalogMapper;
import org.loxf.jyadmin.dal.dao.OfferMapper;
import org.loxf.jyadmin.dal.po.Offer;
import org.loxf.jyadmin.dal.po.OfferCatalog;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lenovo
 */
@Service("offerCatalogService")
public class OfferCatalogServiceImpl implements OfferCatalogService {
    private static String prefix = "CATA";
    @Autowired
    private OfferCatalogMapper offerCatalogMapper;
    @Autowired
    private OfferMapper offerMapper;

    @Override
    public BaseResult<String> addCatalog(String catalogName, String picUrl) {
        OfferCatalog offerCatalog = new OfferCatalog();
        offerCatalog.setCatalogName(catalogName);
        offerCatalog.setPic(picUrl);
        offerCatalog.setCatalogId(IdGenerator.generate(prefix));
        offerCatalogMapper.insert(offerCatalog);
        return new BaseResult<>(offerCatalog.getCatalogId());
    }

    @Override
    public BaseResult<String> updateCatalog(OfferCatalogDto catalogDto){
        if(StringUtils.isNotBlank(catalogDto.getCatalogId())) {
            OfferCatalog offerCatalog = new OfferCatalog();
            BeanUtils.copyProperties(catalogDto, offerCatalog);
            offerCatalogMapper.updateByPrimaryKey(offerCatalog);
            return new BaseResult<>(catalogDto.getCatalogId());
        } else {
            return new BaseResult<>(BaseConstant.FAILED, "分类ID为空");
        }
    }

    @Override
    public BaseResult<String> rmCatalog(String catalogId) {
        // 判断分类是否包含商品。包含不能删除
        Offer offer = new Offer();
        offer.setCatalogId(catalogId);
        int count = offerMapper.count(offer);
        if(count>0){
            return new BaseResult<>(BaseConstant.FAILED, "当前分类包含商品，不能删除");
        } else {
            offerCatalogMapper.deleteByPrimaryKey(catalogId);
        }
        return new BaseResult<>(catalogId);
    }

    @Override
    public PageResult<OfferCatalogDto> pager(OfferCatalogDto catalogDto) {
        OfferCatalog offerCatalog = new OfferCatalog();
        BeanUtils.copyProperties(catalogDto, offerCatalog);
        int total = offerCatalogMapper.count(offerCatalog);
        List<OfferCatalogDto> ret = new ArrayList<>();
        if(total>0) {
            List<OfferCatalog> list = offerCatalogMapper.list(offerCatalog);
            for(OfferCatalog tmp : list){
                OfferCatalogDto dto = new OfferCatalogDto();
                BeanUtils.copyProperties(tmp, dto);
                ret.add(dto);
            }
        }
        int tatalPage = total/catalogDto.getPager().getSize() + (total%catalogDto.getPager().getSize()==0?0:1);
        return new PageResult<>(tatalPage, catalogDto.getPager().getPage(), total, ret);
    }

    @Override
    public BaseResult<OfferCatalogDto> queryById(String catalogId){
        OfferCatalogDto dto = new OfferCatalogDto();
        OfferCatalog offerCatalog = offerCatalogMapper.queryById(catalogId);
        BeanUtils.copyProperties(offerCatalog, dto);
        return new BaseResult<>(dto);
    }
}
