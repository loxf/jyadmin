package org.loxf.jyadmin.web.admin;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.bean.Pager;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.client.dto.CertifyConfigDto;
import org.loxf.jyadmin.client.dto.OfferDto;
import org.loxf.jyadmin.client.service.CertifyConfigService;
import org.loxf.jyadmin.client.service.OfferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin/certify")
public class CertifyController extends BaseControl<CertifyConfigDto> {
    private static Logger logger = LoggerFactory.getLogger(CertifyController.class);

    @Autowired
    private CertifyConfigService certifyService;
    @Autowired
    private OfferService offerService;

    @RequestMapping("/index")
    public String index(){
        return "certify/certify_list";
    }

    @RequestMapping("/toAddCertify")
    public String toAddCertify(Model model){
        settingClassList(model);
        return "main/certify/addCertify";
    }

    @RequestMapping("/toEditCertify")
    public String toEditCertify(String certifyId, Model model){
        settingClassList(model);
        // 获取证书
        BaseResult<CertifyConfigDto> configDtoBaseResult = certifyService.queryById(certifyId);
        if(configDtoBaseResult.getCode()== BaseConstant.SUCCESS || configDtoBaseResult.getData()!=null) {
            model.addAttribute("certify", configDtoBaseResult.getData());
        }
        return "main/certify/editCertify";
    }

    private void settingClassList(Model model){
        OfferDto offerDto = new OfferDto();
        offerDto.setOfferType("CLASS");
        offerDto.setPager(new Pager(1, 1000));
        PageResult<OfferDto> pageResult = offerService.pager(offerDto,1);
        model.addAttribute("offerList", pageResult.getData());
    }

    @RequestMapping("/list")
    @ResponseBody
    public PageResult list(CertifyConfigDto certifyDto){
        initRangeDate(certifyDto);
        PageResult<CertifyConfigDto> pageResult = certifyService.pager(certifyDto);
        return pageResult;
    }

    @RequestMapping("/newCertify")
    @ResponseBody
    public BaseResult newCertify(CertifyConfigDto certifyConfigDto){
        return certifyService.insert(certifyConfigDto);
    }

    @RequestMapping("/editCertify")
    @ResponseBody
    public BaseResult editCertify(CertifyConfigDto certifyDto){
        return certifyService.update(certifyDto);
    }

    @RequestMapping("/delCertify")
    @ResponseBody
    public BaseResult delCertify(String certifyId){
        return certifyService.delete(certifyId);
    }

}
