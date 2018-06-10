package org.loxf.jyadmin.web.admin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.BeanList;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.bean.Pager;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.util.JedisUtil;
import org.loxf.jyadmin.biz.util.SendWeixinMsgUtil;
import org.loxf.jyadmin.client.dto.*;
import org.loxf.jyadmin.client.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
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
    private VideoConfigService videoConfigService;
    @Autowired
    private CustService custService;
    @Autowired
    private ClassQuestionService classQuestionService;
    @Autowired
    private JedisUtil jedisUtil;
    @Value("#{configProperties['JYZX.INDEX.URL']}")
    private String JYZX_INDEX_URL;


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
            model.addAttribute("htmlId", htmlId);
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

    @RequestMapping("/toBuyOffer")
    public String toBuyOffer(String offerId, Model model){
        // 获取商品
        OfferDto offerDto = offerService.queryOffer(offerId).getData();
        if(offerDto!=null && offerDto.getStatus()==1) {
            model.addAttribute("offer", offerDto);
            String buyPrivi = offerDto.getBuyPrivi();
            if (StringUtils.isNotBlank(buyPrivi)) {
                JSONObject buyJson = JSONObject.parseObject(buyPrivi);
                if(buyJson.size()>0) {
                    model.addAttribute("buyJson", buyJson);
                    return "main/offer/buyOffer";
                }
            }
            model.addAttribute("errorMsg", "不能直接购买此商品");
            return "main/error";
        } else {
            model.addAttribute("errorMsg", "商品不存在或已下架");
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
        if(StringUtils.isBlank(offerDto.getOfferType())){
            return new BaseResult(BaseConstant.FAILED, "商品类型为空");
        }
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
    public BaseResult indexRecommend(String offerId, Integer type){
        return offerService.sendIndexRecommend(offerId, type);
    }

    @RequestMapping("/getDetailUrl")
    @ResponseBody
    public BaseResult<String> getDetailUrl(String offerId, String offerType, String type){
        return new BaseResult(getUrl(offerId, offerType, type));
    }

    @RequestMapping("/sendWeiXin")
    @ResponseBody
    public BaseResult sendWeiXin(String offerId, String type, String offerName, String addr, String teachers){
        String key = "SEND_CLASS_NOTICE_" + offerId;
        if(jedisUtil.setnx(key,"true", 300)>0) {
            CustDto custDto = new CustDto();
            custDto.setPager(new Pager(1, 100000));
            List<CustDto> custDtoList = custService.pager(custDto).getData();
            if (CollectionUtils.isNotEmpty(custDtoList)) {
                String url = getUrl(offerId, type, "GZH");
                for (CustDto cust : custDtoList) {
                    if (StringUtils.isNotBlank(cust.getOpenid())) {
                        SendWeixinMsgUtil.sendClassOfferNotice(cust.getOpenid(), offerName, addr, teachers, url);
                    }
                }
            }
            return new BaseResult();
        } else {
            return new BaseResult(BaseConstant.FAILED, "五分钟内，不能重复发送。");
        }
    }
    @RequestMapping("/toViewExam")
    public String toViewExam(Model model, String offerId){
        BaseResult<OfferDto> offerDtoBaseResult = offerService.queryOffer(offerId);
        if(offerDtoBaseResult.getCode()==BaseConstant.FAILED||offerDtoBaseResult.getData()==null){
            model.addAttribute("errorMsg", "商品不存在或已下架");
            return "main/error";
        }
        String metaData = offerDtoBaseResult.getData().getMetaData();
        JSONObject jsonObject = JSONObject.parseObject(metaData);
        model.addAttribute("passScore", jsonObject.get("EXAMPASS"));
        model.addAttribute("examName", offerDtoBaseResult.getData().getOfferName());
        model.addAttribute("offerId", offerId);
        return "main/offer/viewExam";
    }

    @RequestMapping("/toSettingExam")
    public String toSettingExam(Model model, String offerId){
        BaseResult<OfferDto> offerDtoBaseResult = offerService.queryOffer(offerId);
        if(offerDtoBaseResult.getCode()==BaseConstant.FAILED||offerDtoBaseResult.getData()==null){
            model.addAttribute("errorMsg", "商品不存在或已下架");
            return "main/error";
        }
        String metaData = offerDtoBaseResult.getData().getMetaData();
        JSONObject jsonObject = JSONObject.parseObject(metaData);
        model.addAttribute("passScore", jsonObject.get("EXAMPASS"));
        model.addAttribute("examName", offerDtoBaseResult.getData().getOfferName());
        model.addAttribute("offerId", offerId);
        return "main/offer/settingExam";
    }

    @RequestMapping("/queryQuestionList")
    @ResponseBody
    public BaseResult queryQuestionList(String offerId){
        // 获取当前考试的题目
        return classQuestionService.queryQuestions(offerId);
    }
    @RequestMapping("/settingQuestion")
    @ResponseBody
    public BaseResult settingQuestion(String offerId, String examName, Integer passScore, String questionStr){
        if(StringUtils.isBlank(questionStr)||questionStr.equals("[]")){
            return new BaseResult(BaseConstant.FAILED, "考题不能为空");
        }
        BaseResult<OfferDto> offerDtoBaseResult = offerService.queryOffer(offerId);
        if(offerDtoBaseResult.getCode()==BaseConstant.FAILED){
            return offerDtoBaseResult;
        }
        List<ClassQuestionDto> classQuestionDtos = JSON.parseArray(questionStr, ClassQuestionDto.class);
        for(ClassQuestionDto object : classQuestionDtos){
            object.setOfferId(offerId);
            object.setExamName(examName);
            String options = object.getOptions();
            if(StringUtils.isBlank(options)||options.equals("[]")){
                return new BaseResult(BaseConstant.FAILED, "选项不能为空");
            }
        }
        BaseResult baseResult = classQuestionService.settingQuestion(classQuestionDtos);
        if(baseResult.getCode()==BaseConstant.SUCCESS){
            // 修改商品的元数据
            OfferDto offerDto = offerDtoBaseResult.getData();
            String metaData = offerDto.getMetaData();
            JSONObject metaJSON;
            if(StringUtils.isNotBlank(metaData)){
                metaJSON = JSON.parseObject(metaData);
            } else {
                metaJSON = new JSONObject();
            }
            metaJSON.put("EXAMID", offerId);
            metaJSON.put("EXAMENABLE", false);
            metaJSON.put("EXAMPASS", passScore);
            offerDto.setMetaData(metaJSON.toJSONString());
            offerService.updateOffer(offerDto, null);
        }
        return baseResult;
    }

    /**
     * @param offerId
     * @param type 1:发布
     * @return
     */
    @RequestMapping("/onOrOffExam")
    @ResponseBody
    public BaseResult onOrOffExam(String offerId, int type){
        BaseResult<OfferDto> offerDtoBaseResult = offerService.queryOffer(offerId);
        if(offerDtoBaseResult.getCode()==BaseConstant.FAILED){
            return offerDtoBaseResult;
        }
        // 修改商品的元数据
        OfferDto offerDto = offerDtoBaseResult.getData();
        String metaData = offerDto.getMetaData();
        JSONObject metaJSON = null;
        boolean hasExam = true;
        if(StringUtils.isBlank(metaData)){
            hasExam = false;
        } else {
            metaJSON = JSON.parseObject(metaData);
            if(!metaJSON.containsKey("EXAMID")){
                hasExam = false;
            }
        }
        if(!hasExam){
            return new BaseResult(BaseConstant.FAILED, "未设置考题，不能发布/取消");
        }
        metaJSON.put("EXAMENABLE", type==1);
        offerDto.setMetaData(metaJSON.toJSONString());
        return offerService.updateOffer(offerDto, null);
    }
    private String getUrl(String offerId, String offerType, String type){
        if("GZH".equals(type)) {
            if (offerType.equals("ACTIVE")) {
                return String.format(JYZX_INDEX_URL + BaseConstant.ACTIVE_DETAIL_URL, offerId);
            } else if (offerType.equals("OFFER")) {
                return String.format(JYZX_INDEX_URL + BaseConstant.OFFER_DETAIL_URL, offerId);
            } else if (offerType.equals("CLASS")) {
                return String.format(JYZX_INDEX_URL + BaseConstant.CLASS_DETAIL_URL, offerId);
            }
        } else {
            String xcxUrl = "/pages/index/index?id=,";
            try {
                if (offerType.equals("ACTIVE")) {
                    return xcxUrl + URLEncoder.encode(String.format(JYZX_INDEX_URL + BaseConstant.ACTIVE_DETAIL_URL, offerId), "utf-8");
                } else if (offerType.equals("OFFER")) {
                    return xcxUrl + URLEncoder.encode(String.format(JYZX_INDEX_URL + BaseConstant.OFFER_DETAIL_URL, offerId), "utf-8");
                } else if (offerType.equals("CLASS")) {
                    return xcxUrl + URLEncoder.encode(String.format(JYZX_INDEX_URL + BaseConstant.CLASS_DETAIL_URL, offerId), "utf-8");
                }
            } catch (Exception ex){
                logger.warn("url编码失败", ex);
                return "/pages/index/index";
            }
        }
        return null;
    }
}
