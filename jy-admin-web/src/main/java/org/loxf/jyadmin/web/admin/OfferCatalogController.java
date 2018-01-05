package org.loxf.jyadmin.web.admin;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.client.dto.OfferCatalogDto;
import org.loxf.jyadmin.client.service.ConfigService;
import org.loxf.jyadmin.client.service.OfferCatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin/offerCatalog")
public class OfferCatalogController extends BaseControl<OfferCatalogDto> {
    private static Logger logger = LoggerFactory.getLogger(OfferCatalogController.class);

    @Autowired
    private OfferCatalogService offerCatalogService;
    @Autowired
    private ConfigService configService;

    @RequestMapping("/index")
    public String index(){
        return "offerCatalog/offerCatalog_list";
    }

    @RequestMapping("/toAddOfferCatalog")
    public String toAddOfferCatalog(){
        return "main/offerCatalog/addOfferCatalog";
    }

    @RequestMapping("/toEditOfferCatalog")
    public String toEditOfferCatalog(String catalogId, Model model){
        // 获取商品
        BaseResult<OfferCatalogDto> catalogDtoBaseResult = offerCatalogService.queryById(catalogId);
        if(catalogDtoBaseResult.getCode()== BaseConstant.SUCCESS || catalogDtoBaseResult.getData()!=null) {
            model.addAttribute("offerCatalog", catalogDtoBaseResult.getData());
        }
        return "main/offerCatalog/editOfferCatalog";
    }

    @RequestMapping("/list")
    @ResponseBody
    public PageResult list(OfferCatalogDto offerCatalogDto){
        initRangeDate(offerCatalogDto);
        PageResult<OfferCatalogDto> pageResult = offerCatalogService.pager(offerCatalogDto);
        return pageResult;
    }

    @RequestMapping("/newOfferCatalog")
    @ResponseBody
    public BaseResult newOfferCatalog(String catalogName, String pic){
        return offerCatalogService.addCatalog(catalogName, pic);
    }

    @RequestMapping("/editOfferCatalog")
    @ResponseBody
    public BaseResult editOfferCatalog(OfferCatalogDto offerCatalogDto){
        return offerCatalogService.updateCatalog(offerCatalogDto);
    }

    @RequestMapping("/delOfferCatalog")
    @ResponseBody
    public BaseResult delOfferCatalog(String catalogId){
        return offerCatalogService.rmCatalog(catalogId);
    }

}
