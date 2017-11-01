package org.loxf.jyadmin.web.admin;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.client.service.HtmlInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin/htmlInfo")
public class HtmlController {
    @Autowired
    private HtmlInfoService htmlInfoService;

    @RequestMapping("/add")
    @ResponseBody
    public BaseResult<String> addHtmlInfo(String htmlInfo){
        return htmlInfoService.insertHtml(htmlInfo);
    }

    @RequestMapping("/edit")
    @ResponseBody
    public BaseResult<String> editHtmlInfo(String htmlId, String htmlInfo){
        return htmlInfoService.updateHtml(htmlId, htmlInfo);
    }

    @RequestMapping("/get")
    @ResponseBody
    public BaseResult<String> getHtml(String htmlId){
        return htmlInfoService.getHtml(htmlId);
    }
}
