package org.loxf.jyadmin.web.admin;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.client.dto.ActiveCustListDto;
import org.loxf.jyadmin.client.dto.ActiveDto;
import org.loxf.jyadmin.client.dto.CityDto;
import org.loxf.jyadmin.client.dto.ProvinceDto;
import org.loxf.jyadmin.client.service.*;
import org.loxf.jyadmin.biz.util.ConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@Controller
@RequestMapping("/admin/active")
public class ActiveController extends BaseControl<ActiveDto> {
    private static Logger logger = LoggerFactory.getLogger(ActiveController.class);
    @Autowired
    private ActiveService activeService;
    @Autowired
    private ActiveCustListService activeCustListService;
    @Autowired
    private HtmlInfoService htmlInfoService;
    @Autowired
    private ProvinceAndCityService provinceAndCityService;

    @RequestMapping("/index")
    public String index(Model model){
        BaseResult<List<ProvinceDto>> baseResult = provinceAndCityService.queryProvince(null);
        model.addAttribute("provinceList", baseResult.getData());
        return "active/active_list";
    }

    @RequestMapping("/toAddActive")
    public String toAddActive(Model model){
        BaseResult<List<ProvinceDto>> baseResult = provinceAndCityService.queryProvince(null);
        model.addAttribute("provinceList", baseResult.getData());
        return "main/active/addActive";
    }

    @RequestMapping("/toEditActive")
    public String toEditActive(String activeId, Model model){
        // 获取商品
        ActiveDto activeDto = activeService.queryActive(activeId).getData();
        if(activeDto!=null && activeDto.getStatus()!=1) {
            model.addAttribute("active", activeDto);
            BaseResult<List<ProvinceDto>> baseResultProvince = provinceAndCityService.queryProvince(null);
            model.addAttribute("provinceList", baseResultProvince.getData());
            if (StringUtils.isNotBlank(activeDto.getProvince())) {
                CityDto cityDto = new CityDto();
                cityDto.setProvinceid(activeDto.getProvince());
                BaseResult<List<CityDto>> baseResultCity = provinceAndCityService.queryCity(cityDto);
                model.addAttribute("cityList", baseResultCity.getData());
            }
            String activePrivi = activeDto.getActivePrivi();
            if (StringUtils.isNotBlank(activePrivi)) {
                JSONObject activePriviJson = JSONObject.parseObject(activePrivi);
                model.addAttribute("activePriviJson", activePriviJson);
            }
            String metaData = activeDto.getMetaData();
            if (StringUtils.isNotBlank(metaData)) {
                JSONObject metaDataJson = JSONObject.parseObject(metaData);
                model.addAttribute("metaDataJson", metaDataJson);
            }
            String htmlId = activeDto.getHtmlId();
            String htmlInfo = htmlInfoService.getHtml(htmlId).getData();
            model.addAttribute("htmlId", htmlId);
            model.addAttribute("htmlInfo", htmlInfo);
            return "main/active/editActive";
        } else {
            model.addAttribute("errorMsg", "活动不存在或活动已发布");
            return "main/error";
        }
    }

    @RequestMapping("/list")
    @ResponseBody
    public PageResult list(ActiveDto activeDto){
        initRangeDate(activeDto);
        PageResult<ActiveDto> pageResult = activeService.pager(activeDto);
        return pageResult;
    }

    @RequestMapping("/newActive")
    @ResponseBody
    public BaseResult newActive(ActiveDto activeDto, HttpServletRequest request){
        // 处理活动时间
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            activeDto.setActiveStartTime(sf.parse(activeDto.getStartDate()));
            activeDto.setActiveEndTime(sf.parse(activeDto.getEndDate()));
        } catch (ParseException e) {
            logger.error("时间转化失败：", e);
        }
        return activeService.newActive(activeDto);
    }

    @RequestMapping("/editActive")
    @ResponseBody
    public BaseResult editActive(ActiveDto activeDto){
        // 处理活动时间
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            activeDto.setActiveStartTime(sf.parse(activeDto.getStartDate()));
            activeDto.setActiveEndTime(sf.parse(activeDto.getEndDate()));
        } catch (ParseException e) {
            logger.error("时间转化失败：", e);
        }
        return activeService.updateActive(activeDto);
    }
    @RequestMapping("/deleteActive")
    @ResponseBody
    public BaseResult deleteActive(String activeId){
        return activeService.deleteActive(activeId);
    }

    @RequestMapping("/onOrOffActive")
    @ResponseBody
    public BaseResult onOrOffActive(String activeId, Integer status){
        return activeService.onOrOffActive(activeId, status);
    }

    @RequestMapping("/toStudentList")
    public String toStudentList(Model model, String activeId){
        model.addAttribute("activeId", activeId);
        return "main/active/studentList";
    }
    @RequestMapping("/studentList")
    @ResponseBody
    public BaseResult<List<ActiveCustListDto>> studentList(String activeId){
        return activeCustListService.queryList(activeId);
    }

    @RequestMapping("/indexRecommend")
    @ResponseBody
    public BaseResult indexRecommend(String activeId){
        return activeService.sendIndexRecommend(activeId);
    }
}
