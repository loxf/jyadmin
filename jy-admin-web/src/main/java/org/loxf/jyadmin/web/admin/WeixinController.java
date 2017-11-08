package org.loxf.jyadmin.web.admin;

import org.apache.commons.codec.digest.DigestUtils;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collections;

@Controller
public class WeixinController {
    /**
     * 接入微信接口
     * @param signature
     * @param timestamp
     * @param nonce
     * @param echostr
     * @return
     */
    @RequestMapping("/admin/weixin/api_access")
    @ResponseBody
    public String apiAccess(String signature, String timestamp, String nonce, String echostr){
        // 1）将token、timestamp、nonce三个参数进行字典序排序
        ArrayList<String> list=new ArrayList<String>();
        list.add(nonce);
        list.add(timestamp);
        list.add(BaseConstant.WX_TOKEN);
        Collections.sort(list);
        //2）将三个参数字符串拼接成一个字符串进行sha1加密
        String ret = DigestUtils.shaHex(list.get(0)+list.get(1)+list.get(2));
        //3）开发者获得加密后的字符串可与signature对比，标识该请求来源于微信
        if(ret.equals(signature)){
            return echostr;
        }
        return "";
    }

}
