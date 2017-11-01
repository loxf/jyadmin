package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.client.dto.OfferCatalogDto;

/**
 * @author lenovo
 */
public interface OfferCatalogService {
    public BaseResult<String> addCatalog(String catalogName, String picUrl);
    public BaseResult<String> rmCatalog(String catalogId);
    public BaseResult<String> updateCatalog(OfferCatalogDto catalogDto);
    public PageResult<OfferCatalogDto> pager(OfferCatalogDto catalogDto);
    public BaseResult<OfferCatalogDto> queryById(String catalogId);
}
