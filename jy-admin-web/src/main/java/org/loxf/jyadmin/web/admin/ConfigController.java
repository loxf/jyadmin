package org.loxf.jyadmin.web.admin;

import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.biz.util.ConfigProperties;
import org.loxf.jyadmin.client.constant.BaseConstant;
import org.loxf.jyadmin.client.dto.ConfigDto;
import org.loxf.jyadmin.client.service.ConfigService;
import org.loxf.jyadmin.client.service.HtmlInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin/config")
public class ConfigController {
    @Autowired
    private ConfigService configService;
    @Autowired
    private HtmlInfoService htmlInfoService;
    @Autowired
    private ConfigProperties configProperties;

    private static String[] catalogs = {"RUNTIME", "PAY" , "BP" , "COM"};

    @RequestMapping("/index")
    public String index(Model model, String catalog){
        if(StringUtils.isBlank(catalog) || Arrays.asList(catalog).contains(catalog)) {
            model.addAttribute("catalog", catalog);
            return "config/config_list";
        } else {
            model.addAttribute("errorMsg", "当前目录不存在");
            return "layout_error";
        }
    }

    @RequestMapping("/toAddConfig")
    public String toAddConfig(Model model, String catalog){
        model.addAttribute("catalog", catalog);
        return "main/config/addConfig";
    }

    @RequestMapping("/toEditConfig")
    public String toEditConfig(Model model, String catalog, String configCode){
        BaseResult<ConfigDto> configDtoBaseResult = configService.queryConfig(catalog, configCode);
        if(configDtoBaseResult.getCode()== BaseConstant.SUCCESS && configDtoBaseResult.getData()!=null){
            model.addAttribute("configDto", configDtoBaseResult.getData());
            model.addAttribute("basePic", configProperties.getIMG_SERVER());
            if("HTML".equals(configDtoBaseResult.getData().getType())){
                String htmlId = configDtoBaseResult.getData().getConfigValue();
                String htmlInfo = htmlInfoService.getHtml(htmlId).getData();
                model.addAttribute("htmlInfo", htmlInfo);
            }
            return "main/config/editConfig";
        } else {
            model.addAttribute("errorMsg", "配置不存在");
            return "main/error";
        }
    }

    @RequestMapping("/list")
    @ResponseBody
    public PageResult<ConfigDto> list(ConfigDto configConfigDto){
        return configService.pager(configConfigDto);
    }

    @RequestMapping("/addConfig")
    @ResponseBody
    public BaseResult addConfig(ConfigDto configDto){
        return configService.newConfig(configDto);
    }

    @RequestMapping("/editConfig")
    @ResponseBody
    public BaseResult editConfig(ConfigDto configDto){
        return configService.updateConfig(configDto);
    }

    @RequestMapping("/delConfig")
    @ResponseBody
    public BaseResult delConfig(String catalog, String configCode){
        return configService.deleteConfig(catalog, configCode);
    }

    @RequestMapping("/onOrOffConfig")
    @ResponseBody
    public BaseResult onOrOffConfig(Long id, Integer status){
        return configService.onOrOffConfig(id, status);
    }

}
