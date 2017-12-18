package org.loxf.jyadmin.biz.util;

import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.constant.WxMsgTemplateConstant;
import org.loxf.jyadmin.base.util.DateUtils;
import org.loxf.jyadmin.base.util.SpringApplicationContextUtil;
import org.loxf.jyadmin.biz.msg.WeixinSender;
import org.loxf.jyadmin.client.service.NoticeService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SendWeixinMsgUtil {

    /**
     * 奖学金
     * @param openid
     * @param money
     * @param nickName
     */
    public static void sendScholarshipMsg( String openid, String money,String nickName){
        Map data = new HashMap();
        data.put("first", WeixinSender.createWXKeyWord("恭喜您，获得了一笔奖学金。", null));
        data.put("keyword1", WeixinSender.createWXKeyWord(money, "#FF3030"));
        data.put("keyword2", WeixinSender.createWXKeyWord(nickName, null));
        data.put("remark", WeixinSender.createWXKeyWord("可通过菜单“奖学金”进行余额查询，余额提现，清单明细查询等操作", null));
        noticeService().insert("WX", openid, WeixinSender.createWxMsgMap(WxMsgTemplateConstant.JXJ,
                openid, data, BaseConstant.JYZX_ACCOUNT_URL));
    }
    /**
     * 课程通知
     * @param openid
     * @param className
     * @param addr
     */
    public static void sendClassOfferNotice( String openid, String className,String addr, String teachers, String url){
        Map data = new HashMap();
        data.put("first", WeixinSender.createWXKeyWord("亲爱的会员，新的课程上线了。", null));
        data.put("keyword1", WeixinSender.createWXKeyWord(className, "#FF3030"));
        data.put("keyword2", WeixinSender.createWXKeyWord(addr, null));
        data.put("keyword3", WeixinSender.createWXKeyWord(teachers, null));
        data.put("keyword4", WeixinSender.createWXKeyWord(DateUtils.formatHms(new Date()), null));
        data.put("remark", WeixinSender.createWXKeyWord("点击学习课程，邀请朋友一起学习可获奖学金", null));
        noticeService().insert("WX", openid, WeixinSender.createWxMsgMap(WxMsgTemplateConstant.CLASS_NOTICE,
                openid, data, url));
    }

    private static NoticeService noticeService(){
        return SpringApplicationContextUtil.getBean(NoticeService.class);
    }

}
