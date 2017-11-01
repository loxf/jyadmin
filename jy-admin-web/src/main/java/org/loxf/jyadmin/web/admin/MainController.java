package org.loxf.jyadmin.web.admin;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.client.dto.AreaDto;
import org.loxf.jyadmin.client.dto.CityDto;
import org.loxf.jyadmin.client.dto.ProvinceDto;
import org.loxf.jyadmin.client.service.ProvinceAndCityService;
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

    @RequestMapping("/admin/index")
    public String index(Model model, HttpServletRequest request, HttpServletResponse response){
        model.addAttribute("isIndex", true);
        return "index";
    }
    @RequestMapping("/")
    public String welcome(Model model, HttpServletRequest request, HttpServletResponse response){
        return "login";
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
