package org.loxf.jyadmin.web.admin;

import org.apache.commons.collections.CollectionUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.util.ExcelImportUtil;
import org.loxf.jyadmin.client.dto.AdminDto;
import org.loxf.jyadmin.client.service.*;
import org.loxf.jyadmin.client.tmp.CustInfoUpload;
import org.loxf.jyadmin.client.tmp.IncomeUpload;
import org.loxf.jyadmin.client.tmp.OrderInfoUpload;
import org.loxf.jyadmin.util.CookieUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("/admin/init")
public class InitController {
    private static Logger logger = LoggerFactory.getLogger(InitController.class);

    @Autowired
    private CustService custService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CompanyIncomeService companyIncomeService;

    @RequestMapping("/index")
    public String toIndex(Model model, HttpServletRequest request) {
        AdminDto adminDto = CookieUtil.getAdmin(request);
        if (adminDto.getUserName().equals("admin")) {
            return "system/index";
        } else {
            model.addAttribute("errorMsg", "无权操作");
            return "layout_error";
        }
    }

    @RequestMapping("/custInit")
    @ResponseBody
    public BaseResult<String> custInit(HttpServletRequest request, @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            List<CustInfoUpload> list = ExcelImportUtil.parseUploadDataToList(file.getInputStream(), file.getOriginalFilename(), CustInfoUpload.class);
            BaseResult baseResult = custService.addOldCust(list);
            // 处理代理关系
            custService.updateOldCustRecommend();
            return baseResult;
        } catch (Exception e) {
            logger.error("上传失败", e);
            return new BaseResult(BaseConstant.FAILED, e.getMessage());
        }
    }


    @RequestMapping("/orderInit")
    @ResponseBody
    public BaseResult orderInit(HttpServletRequest request,
                            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            List<OrderInfoUpload> list = ExcelImportUtil.parseUploadDataToList(file.getInputStream(), file.getOriginalFilename(), OrderInfoUpload.class);
            BaseResult baseResult = orderService.createOldOrder(list);
            return baseResult;
        } catch (Exception e) {
            logger.error("上传失败", e);
            return new BaseResult(BaseConstant.FAILED, e.getMessage());
        }
    }

    @RequestMapping("/companyIncomeInit")
    @ResponseBody
    public BaseResult companyIncomeInit(HttpServletRequest request,
                                    @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            List<IncomeUpload> list = ExcelImportUtil.parseUploadDataToList(file.getInputStream(), file.getOriginalFilename(), IncomeUpload.class);
            return companyIncomeService.initCompanyIncome(list);
        } catch (Exception e) {
            logger.error("上传失败", e);
            return new BaseResult(BaseConstant.FAILED, e.getMessage());
        }
    }

    @RequestMapping("/userIncomeInit")
    @ResponseBody
    public BaseResult userIncomeInit(HttpServletRequest request,
                                 @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            List<IncomeUpload> list = ExcelImportUtil.parseUploadDataToList(file.getInputStream(), file.getOriginalFilename(), IncomeUpload.class);
            return companyIncomeService.initScholarshipIncome(list);
        } catch (Exception e) {
            logger.error("上传失败", e);
            return new BaseResult(BaseConstant.FAILED, e.getMessage());
        }
    }

    @RequestMapping("/takeCashInit")
    @ResponseBody
    public String takeCashInit(HttpServletRequest request,
                               @RequestParam(value = "file", required = false) MultipartFile file) {
        return "";
    }

}
