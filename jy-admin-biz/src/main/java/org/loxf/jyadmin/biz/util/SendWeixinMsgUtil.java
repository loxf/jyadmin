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
     * @param teachers
     * @param url
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
    /**
     * 注册通知
     * @param openid
     */
    public static void sendRegisterNotice( String openid, String nickname){
        Map data = new HashMap();
        data.put("first", WeixinSender.createWXKeyWord("亲爱的会员，欢迎来到静怡雅学文化", null));
        data.put("keyword1", WeixinSender.createWXKeyWord(nickname, "#FF3030"));
        data.put("keyword2", WeixinSender.createWXKeyWord(DateUtils.formatHms(new Date()), null));
        data.put("remark", WeixinSender.createWXKeyWord("畅享优雅世界，邀请朋友一起学习可获奖学金。请尽快绑定手机/邮箱。", null));
        noticeService().insert("WX", openid, WeixinSender.createWxMsgMap(WxMsgTemplateConstant.REGISTER,
                openid, data, BaseConstant.JYZX_INDEX_URL));
    }
    /**
     * 会员升级
     * @param openid
     * @param nickname
     * @param userLevel
     */
    public static void sendBeVipNotice( String openid, String nickname, String userLevel){
        Map data = new HashMap();
        data.put("first", WeixinSender.createWXKeyWord("亲爱的" + nickname + "，您已经成功升级会员", null));
        data.put("keyword1", WeixinSender.createWXKeyWord(userLevel, "#FF3030"));
        data.put("keyword2", WeixinSender.createWXKeyWord("升级成功", null));
        data.put("remark", WeixinSender.createWXKeyWord("畅享名师干活，分享可得奖学金。快邀请您的朋友一起学习吧！", null));
        noticeService().insert("WX", openid, WeixinSender.createWxMsgMap(WxMsgTemplateConstant.BE_VIP,
                openid, data, BaseConstant.JYZX_INDEX_URL));
    }
    /**
     * 积分到账通知
     * @param openid
     * @param nickname
     * @param detailName
     * @param bp
     * @param bpBalance
     */
    public static void sendGetBpNotice( String openid, String nickname, String detailName, String bp, String bpBalance){
        Map data = new HashMap();
        data.put("first", WeixinSender.createWXKeyWord("亲爱的" + nickname + "，您的积分已经到账", null));
        data.put("keyword1", WeixinSender.createWXKeyWord(nickname, "#FF3030"));
        data.put("keyword2", WeixinSender.createWXKeyWord(DateUtils.formatHms(new Date()), null));
        data.put("keyword3", WeixinSender.createWXKeyWord(detailName, null));
        data.put("keyword4", WeixinSender.createWXKeyWord(bp, null));
        data.put("keyword5", WeixinSender.createWXKeyWord(bpBalance, null));
        data.put("remark", WeixinSender.createWXKeyWord("邀请好友，得积分；看视频，得积分；每日签到，同样得积分！", null));
        noticeService().insert("WX", openid, WeixinSender.createWxMsgMap(WxMsgTemplateConstant.BP,
                openid, data, BaseConstant.JYZX_INDEX_URL));
    }
    /**
     * TODO 活动即将开始
     * @param openid
     * @param activeName
     * @param activeTime
     * @param url
     */
    public static void sendActiveWillStartNotice(String openid, String activeName, String activeTime, String url){
        Map data = new HashMap();
        data.put("first", WeixinSender.createWXKeyWord("亲爱的会员，您参加的活动即将开始，特此通知", null));
        data.put("keyword1", WeixinSender.createWXKeyWord(activeName, "#FF3030"));
        data.put("keyword2", WeixinSender.createWXKeyWord(activeTime, null));
        data.put("remark", WeixinSender.createWXKeyWord("请您准时参加，不见不散。", null));
        noticeService().insert("WX", openid, WeixinSender.createWxMsgMap(WxMsgTemplateConstant.ACTIVE_WILL_START,
                openid, data, url));
    }
    /**
     * 用户绑定通知
     * @param openid
     * @param nickname
     * @param contact
     */
    public static void sendUserBindNotice( String openid, String nickname, String contact){
        Map data = new HashMap();
        data.put("first", WeixinSender.createWXKeyWord("亲爱的会员，您已使用" + contact + "绑定", null));
        data.put("keyword1", WeixinSender.createWXKeyWord(nickname, "#FF3030"));
        data.put("keyword2", WeixinSender.createWXKeyWord(DateUtils.formatHms(new Date()), null));
        data.put("remark", WeixinSender.createWXKeyWord("若非本人操作，请联系班主任，谢谢。", null));
        noticeService().insert("WX", openid, WeixinSender.createWxMsgMap(WxMsgTemplateConstant.BIND_USER,
                openid, data, BaseConstant.JYZX_INDEX_URL));
    }
    /**
     * 会员到期通知
     * @param openid
     * @param nickname
     * @param userLevel
     * @param expDate
     */
    public static void sendVipExpNotice( String openid, String nickname, String userLevel, String expDate){
        String url = "";
        if(userLevel.equals("VIP")){
            url = "https://www.jingyizaixian.com/confirmOrder?type=VIP&id=OFFER001";
        } else if(userLevel.equals("SVIP")){
            url = "https://www.jingyizaixian.com/confirmOrder?type=VIP&id=OFFER002";
        }
        Map data = new HashMap();
        data.put("first", WeixinSender.createWXKeyWord("亲爱的" + nickname +"，您的" + userLevel + "即将到期", null));
        data.put("keyword1", WeixinSender.createWXKeyWord(userLevel, "#FF3030"));
        data.put("keyword2", WeixinSender.createWXKeyWord(expDate, null));
        data.put("remark", WeixinSender.createWXKeyWord("点击立即续期。", null));
        noticeService().insert("WX", openid, WeixinSender.createWxMsgMap(WxMsgTemplateConstant.VIP_EXP,
                openid, data, url));
    }
    /**
     * 商品购买通知
     * @param openid
     * @param nickname
     * @param offerName
     * @param url
     */
    public static void sendBuyOfferNotice( String openid, String nickname, String offerName, String url){
        Map data = new HashMap();
        data.put("name", WeixinSender.createWXKeyWord(offerName, "#FF3030"));
        data.put("remark", WeixinSender.createWXKeyWord("点击可查看商品详情。", null));
        noticeService().insert("WX", openid, WeixinSender.createWxMsgMap(WxMsgTemplateConstant.BUY_OFFER,
                openid, data, url));
    }
    /**
     * 活动报名成功
     * @param openid
     * @param nickname
     * @param activeName
     * @param url
     */
    public static void sendActiveInNotice( String openid, String nickname, String activeName, String activeTime,
                                           String ticketNo, String addr, String url){
        Map data = new HashMap();
        data.put("first", WeixinSender.createWXKeyWord("亲爱的" + nickname +"，您已成功报名：" + activeName, null));
        data.put("keyword1", WeixinSender.createWXKeyWord(activeTime, "#FF3030"));
        data.put("keyword2", WeixinSender.createWXKeyWord(addr, null));
        data.put("remark", WeixinSender.createWXKeyWord("您的票号：" + ticketNo + "。期待您的到来。点击可查看活动详情。", null));
        noticeService().insert("WX", openid, WeixinSender.createWxMsgMap(WxMsgTemplateConstant.ACTIVE_IN,
                openid, data, url));
    }

    private static NoticeService noticeService(){
        return SpringApplicationContextUtil.getBean(NoticeService.class);
    }

}
