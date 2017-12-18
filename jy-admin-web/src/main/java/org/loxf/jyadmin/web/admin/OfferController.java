package org.loxf.jyadmin.web.admin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.bean.Pager;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.client.dto.OfferCatalogDto;
import org.loxf.jyadmin.client.dto.OfferDto;
import org.loxf.jyadmin.client.dto.OfferRelDto;
import org.loxf.jyadmin.client.dto.VideoConfigDto;
import org.loxf.jyadmin.client.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("/admin/offer")
public class OfferController extends BaseControl<OfferDto> {
    private static Logger logger = LoggerFactory.getLogger(OfferController.class);

    @Autowired
    private OfferService offerService;
    @Autowired
    private OfferCatalogService offerCatalogService;
    @Autowired
    private HtmlInfoService htmlInfoService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private IndexRecommendService indexRecommendService;
    @Autowired
    private VideoConfigService videoConfigService;

    @RequestMapping("/index")
    public String index(Model model){
        model.addAttribute("catalogDtos", initOfferCatalog());
        return "offer/offer_list";
    }

    @RequestMapping("/toAddOffer")
    public String toAddOffer(Model model, String type){
        model.addAttribute("catalogDtos", initOfferCatalog());
        if("CLASS".equals(type)) {
            return "main/offer/addClass";
        } else if("OFFER".equals(type)) {
            BaseResult<List<OfferDto>> baseResult = offerService.pagerOfferAndActive(null);
            model.addAttribute("offerAndActiveList", baseResult.getData());
            return "main/offer/addOffer";
        } else {
            return "main/offer/addClass";
        }
    }

    @RequestMapping("/toEditOffer")
    public String toEditOffer(String offerId, Model model){
        // 获取商品
        OfferDto offerDto = offerService.queryOffer(offerId).getData();
        if(offerDto!=null && offerDto.getStatus()!=1) {
            model.addAttribute("catalogDtos", initOfferCatalog());
            model.addAttribute("offer", offerDto);
            String htmlId = offerDto.getHtmlId();
            String htmlInfo = htmlInfoService.getHtml(htmlId).getData();
            model.addAttribute("htmlInfo", htmlInfo);
            String buyPrivi = offerDto.getBuyPrivi();
            if (StringUtils.isNotBlank(buyPrivi)) {
                JSONObject buyJson = JSONObject.parseObject(buyPrivi);
                model.addAttribute("buyJson", buyJson);
            }
            String metaData = offerDto.getMetaData();
            if (StringUtils.isNotBlank(metaData)) {
                JSONObject metaDataJson = JSONObject.parseObject(metaData);
                model.addAttribute("metaDataJson", metaDataJson);
            }
            // 套餐与课程的区别
            if("OFFER".equals(offerDto.getOfferType())) {
                BaseResult<List<OfferDto>> baseResult = offerService.pagerOfferAndActive(null);
                model.addAttribute("offerAndActiveList", baseResult.getData());
                return "main/offer/editOffer";
            } else {
                if(StringUtils.isNotBlank(offerDto.getMainMedia())){
                    BaseResult<VideoConfigDto> videoConfigDtoBaseResult = videoConfigService.queryVideo(offerDto.getMainMedia());
                    if(videoConfigDtoBaseResult.getCode()==BaseConstant.SUCCESS && videoConfigDtoBaseResult.getData()!=null) {
                        model.addAttribute("videoName", videoConfigDtoBaseResult.getData().getVideoName());
                    }
                }
                return "main/offer/editClass";
            }
        } else {
            model.addAttribute("errorMsg", "商品不存在或已上架");
            return "main/error";
        }
    }

    @RequestMapping("/list")
    @ResponseBody
    public PageResult list(OfferDto offerDto){
        initRangeDate(offerDto);
        PageResult<OfferDto> pageResult = offerService.pager(offerDto,1);
        return pageResult;
    }

    @RequestMapping("/newOffer")
    @ResponseBody
    public BaseResult newOffer(OfferDto offerDto, String relOfferStr, HttpServletRequest request){
        List<OfferRelDto> list = dealRelOfferStr(relOfferStr);
        BaseResult<String> baseResult = offerService.newOffer(offerDto, list);
        return baseResult;
    }

    @RequestMapping("/editOffer")
    @ResponseBody
    public BaseResult editOffer(OfferDto offerDto, String relOfferStr){
        List<OfferRelDto> list = dealRelOfferStr(relOfferStr);
        return offerService.updateOffer(offerDto, list);
    }

    private List<OfferRelDto> dealRelOfferStr(String relOfferStr){
        List<OfferRelDto> list = new ArrayList<>();
        if(StringUtils.isNotBlank(relOfferStr)) {
            JSONArray jsonArray = JSON.parseArray(relOfferStr);
            for(Object object : jsonArray){
                JSONObject json = (JSONObject) object;
                OfferRelDto relDto = JSON.parseObject(json.toJSONString(), OfferRelDto.class);
                list.add(relDto);
            }
        }
        return list;
    }

    @RequestMapping("/deleteOffer")
    @ResponseBody
    public BaseResult deleteOffer(String offerId){
        return offerService.deleteOffer(offerId);
    }

    @RequestMapping("/onOrOffOffer")
    @ResponseBody
    public BaseResult onOrOffOffer(String offerId, Integer status){
        return offerService.onOrOffOffer(offerId, status);
    }

    @RequestMapping("/toShowOffer")
    public String toShowOffer(Model model, String offerId, String relType){
        model.addAttribute("offerId", offerId);
        model.addAttribute("relType", relType);
        return "main/offer/relOfferList";
    }

    /**
     * 选商品/活动列表
     * @return
     */
    @RequestMapping("/simpleOfferList")
    @ResponseBody
    public BaseResult<List<OfferDto>> simpleOfferList(String name){
        return offerService.pagerOfferAndActive(name);
    }

    @RequestMapping("/showOffer")
    @ResponseBody
    public BaseResult<List<OfferDto>> showOffer(String offerId, String relType){
        return offerService.showOfferRel(offerId, relType);
    }
    private List<OfferCatalogDto> initOfferCatalog(){
        OfferCatalogDto offerCatalogDto = new OfferCatalogDto();
        Pager pager = new Pager(1, 1000);
        offerCatalogDto.setPager(pager);
        PageResult<OfferCatalogDto> tmp = offerCatalogService.pager(offerCatalogDto);
        return tmp.getData();
    }

    @RequestMapping("/indexRecommend")
    @ResponseBody
    public BaseResult indexRecommend(String offerId){
        return offerService.sendIndexRecommend(offerId);
    }

    @RequestMapping("/getDetailUrl")
    @ResponseBody
    public BaseResult getDetailUrl(String offerId, String type){
        if(type.equals("ACTIVE")) {
            return new BaseResult(String.format(BaseConstant.ACTIVE_DETAIL_URL, offerId));
        } else if(type.equals("OFFER")){
            return new BaseResult(String.format(BaseConstant.OFFER_DETAIL_URL, offerId));
        } else if(type.equals("CLASS")){
            return new BaseResult(String.format(BaseConstant.CLASS_DETAIL_URL, offerId));
        }
        return new BaseResult(BaseConstant.FAILED, "类型不存在");
    }
}
