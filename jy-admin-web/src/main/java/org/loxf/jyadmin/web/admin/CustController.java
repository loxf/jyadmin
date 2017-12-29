package org.loxf.jyadmin.web.admin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.util.JedisUtil;
import org.loxf.jyadmin.client.dto.CustDto;
import org.loxf.jyadmin.client.service.CustService;
import org.loxf.jyadmin.client.service.VipInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin/cust")
public class CustController extends BaseControl<CustDto> {
    private static Logger logger = LoggerFactory.getLogger(CustController.class);
    @Autowired
    private CustService custService;
    @Autowired
    private VipInfoService vipInfoService;
    @Autowired
    private JedisUtil jedisUtil;

    @RequestMapping("/index")
    public String index(){
        return "cust/cust_list";
    }

    @RequestMapping("/list")
    @ResponseBody
    public PageResult list(CustDto custDto){
        initRangeDate(custDto);
        PageResult<CustDto> pageResult = custService.pager(custDto);
        return pageResult;
    }

    @RequestMapping("/modifyRecommend")
    @ResponseBody
    public BaseResult modifyRecommend(String custId, String recommend){
        if(StringUtils.isBlank(custId) || StringUtils.isBlank(recommend)){
            return new BaseResult(0, "推荐人不能为空");
        }
        int isChinese = 1;//国内用户
        if(recommend.indexOf("@")>0) {
            isChinese = 2;
        }
        BaseResult<CustDto> baseResult = custService.queryCust(isChinese, recommend);
        if(baseResult.getCode()== BaseConstant.FAILED){
            return baseResult;
        } else {
            // 执行修改逻辑
            return custService.updateRecommend(custId, baseResult.getData().getCustId());
        }
    }

    @RequestMapping("/modifyUserLevel")
    @ResponseBody
    public BaseResult modifyUserLevel(String custId, String userLevel){
        if(StringUtils.isBlank(custId) || StringUtils.isBlank(custId)){
            return new BaseResult(0, "客户ID不能为空");
        }
        BaseResult vipBaseResult = vipInfoService.changeVipLevel(custId, userLevel);
        // 执行修改逻辑
        if(vipBaseResult.getCode()==BaseConstant.SUCCESS) {
            CustDto custDto = new CustDto();
            custDto.setCustId(custId);
            custDto.setUserLevel(userLevel);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ADMINSET", true);
            custDto.setMetaData(jsonObject.toJSONString());
            BaseResult baseResult = custService.updateCust(custDto);
            // 如果购买的是VIP，设置用户信息刷新标志
            jedisUtil.set("REFRESH_CUST_INFO_" + custId, "true", 60);
            return baseResult;
        }
        return vipBaseResult;
    }

    @RequestMapping("/toChildList")
    public String toChildList(Model model, int type, String custId){
        model.addAttribute("type", type);
        model.addAttribute("custId", custId);
        return "main/cust/childList";
    }

    @RequestMapping("/childList")
    @ResponseBody
    public PageResult<CustDto> queryChildList(int type, String custId, int page, int size){
        return custService.queryChildList(type, custId, page, size);
    }

    @RequestMapping("/delCust")
    @ResponseBody
    public BaseResult delCust(String custId){
        return custService.delCust(custId);
    }
}
