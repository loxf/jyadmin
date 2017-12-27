package org.loxf.jyadmin.web.admin;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.client.dto.AdminDto;
import org.loxf.jyadmin.client.dto.AreaDto;
import org.loxf.jyadmin.client.dto.CityDto;
import org.loxf.jyadmin.client.dto.ProvinceDto;
import org.loxf.jyadmin.client.service.ProvinceAndCityService;
import org.loxf.jyadmin.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class MainController {
    @Autowired
    private ProvinceAndCityService provinceAndCityService;

    @RequestMapping("/")
    public String welcome(Model model, HttpServletRequest request, HttpServletResponse response){
        model.addAttribute("sysTitle", "静怡雅学文化管理系统");
        return "login";
    }

    @RequestMapping("/admin/index")
    public String index(Model model, HttpServletRequest request, HttpServletResponse response){
        AdminDto adminDto = CookieUtil.getAdmin(request);
        if(adminDto==null){
            return "login";
        }
        model.addAttribute("isIndex", true);
        model.addAttribute("username", adminDto.getRealName());
        return "index";
    }

    @RequestMapping("/admin/error")
    public String error(Model model, String errorMsg, HttpServletRequest request, HttpServletResponse response){
        model.addAttribute("errorMsg", errorMsg);
        return "layout_error";
    }

    @RequestMapping("/admin/province")
    @ResponseBody
    public BaseResult province(ProvinceDto provinceDto, Model model, HttpServletRequest request, HttpServletResponse response){
        return provinceAndCityService.queryProvince(provinceDto);
    }
    @RequestMapping("/admin/city")
    @ResponseBody
    public BaseResult city(CityDto cityDto, Model model, HttpServletRequest request, HttpServletResponse response){
        return provinceAndCityService.queryCity(cityDto);
    }
    @RequestMapping("/admin/area")
    @ResponseBody
    public BaseResult area(AreaDto areaDto, Model model, HttpServletRequest request, HttpServletResponse response){
        return provinceAndCityService.queryArea(areaDto);
    }
}
