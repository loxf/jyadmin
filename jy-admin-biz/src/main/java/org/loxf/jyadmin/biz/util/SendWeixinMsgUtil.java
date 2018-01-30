package org.loxf.jyadmin.biz.util;

import org.loxf.jyadmin.base.constant.BaseConstant;
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
    public static void sendScholarshipMsg( String openid, String money,String nickName, String JYZX_INDEX_URL){
        String JXJ = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "WX_MSG_JXJ").getConfigValue();
        Map data = new HashMap();
        data.put("first", WeixinSender.createWXKeyWord("恭喜您，获得了一笔奖学金。", null));
        data.put("keyword1", WeixinSender.createWXKeyWord(money, "#FF3030"));
        data.put("keyword2", WeixinSender.createWXKeyWord(nickName, null));
        data.put("remark", WeixinSender.createWXKeyWord("可通过菜单“奖学金”进行余额查询，余额提现，清单明细查询等操作", null));
        noticeService().insert("WX", openid, WeixinSender.createWxMsgMap(JXJ,
                openid, data, JYZX_INDEX_URL + BaseConstant.JYZX_ACCOUNT_URL));
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
        String CLASS_NOTICE = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "WX_MSG_CLASS_NOTICE").getConfigValue();
        Map data = new HashMap();
        data.put("first", WeixinSender.createWXKeyWord("亲爱的会员，新的课程上线了。", null));
        data.put("keyword1", WeixinSender.createWXKeyWord(className, "#FF3030"));
        data.put("keyword2", WeixinSender.createWXKeyWord(addr, null));
        data.put("keyword3", WeixinSender.createWXKeyWord(teachers, null));
        data.put("keyword4", WeixinSender.createWXKeyWord(DateUtils.formatHms(new Date()), null));
        data.put("remark", WeixinSender.createWXKeyWord("点击学习课程，邀请朋友一起学习可获奖学金", null));
        noticeService().insert("WX", openid, WeixinSender.createWxMsgMap(CLASS_NOTICE,
                openid, data, url));
    }
    /**
     * 注册通知
     * @param openid
     */
    public static void sendRegisterNotice( String openid, String nickname, String JYZX_INDEX_URL){
        String REGISTER = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "WX_MSG_REGISTER").getConfigValue();
        Map data = new HashMap();
        data.put("first", WeixinSender.createWXKeyWord("亲爱的会员，欢迎来到静怡雅学文化", null));
        data.put("keyword1", WeixinSender.createWXKeyWord(nickname, "#FF3030"));
        data.put("keyword2", WeixinSender.createWXKeyWord(DateUtils.formatHms(new Date()), null));
        data.put("remark", WeixinSender.createWXKeyWord("畅享优雅世界，邀请朋友一起学习可获奖学金。请尽快绑定手机/邮箱。", null));
        noticeService().insert("WX", openid, WeixinSender.createWxMsgMap(REGISTER,
                openid, data, JYZX_INDEX_URL));
    }
    /**
     * 会员升级
     * @param openid
     * @param nickname
     * @param userLevel
     */
    public static void sendBeVipNotice( String openid, String nickname, String userLevel, String JYZX_INDEX_URL){
        String BE_VIP = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "WX_MSG_BE_VIP").getConfigValue();
        Map data = new HashMap();
        data.put("first", WeixinSender.createWXKeyWord("亲爱的" + nickname + "，您已经成功升级会员", null));
        data.put("keyword1", WeixinSender.createWXKeyWord(userLevel, "#FF3030"));
        data.put("keyword2", WeixinSender.createWXKeyWord("升级成功", null));
        data.put("remark", WeixinSender.createWXKeyWord("畅享名师干活，分享可得奖学金。快邀请您的朋友一起学习吧！", null));
        noticeService().insert("WX", openid, WeixinSender.createWxMsgMap(BE_VIP,
                openid, data, JYZX_INDEX_URL));
    }
    /**
     * 积分到账通知
     * @param openid
     * @param nickname
     * @param detailName
     * @param bp
     * @param bpBalance
     */
    public static void sendGetBpNotice( String openid, String nickname, String detailName, String bp, String bpBalance, String JYZX_INDEX_URL){
        String BP = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "WX_MSG_BP").getConfigValue();
        Map data = new HashMap();
        data.put("first", WeixinSender.createWXKeyWord("亲爱的" + nickname + "，您的积分已经到账", null));
        data.put("keyword1", WeixinSender.createWXKeyWord(nickname, "#FF3030"));
        data.put("keyword2", WeixinSender.createWXKeyWord(DateUtils.formatHms(new Date()), null));
        data.put("keyword3", WeixinSender.createWXKeyWord(detailName, null));
        data.put("keyword4", WeixinSender.createWXKeyWord(bp, null));
        data.put("keyword5", WeixinSender.createWXKeyWord(bpBalance, null));
        data.put("remark", WeixinSender.createWXKeyWord("邀请好友，得积分；看视频，得积分；每日签到，同样得积分！", null));
        noticeService().insert("WX", openid, WeixinSender.createWxMsgMap(BP,
                openid, data, JYZX_INDEX_URL));
    }
    /**
     * TODO 活动即将开始
     * @param openid
     * @param activeName
     * @param activeTime
     * @param url
     */
    public static void sendActiveWillStartNotice(String openid, String activeName, String activeTime, String url){
        String ACTIVE_WILL_START = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "WX_MSG_ACTIVE_WILL_START").getConfigValue();
        Map data = new HashMap();
        data.put("first", WeixinSender.createWXKeyWord("亲爱的会员，您参加的活动即将开始，特此通知", null));
        data.put("keyword1", WeixinSender.createWXKeyWord(activeName, "#FF3030"));
        data.put("keyword2", WeixinSender.createWXKeyWord(activeTime, null));
        data.put("remark", WeixinSender.createWXKeyWord("请您准时参加，不见不散。", null));
        noticeService().insert("WX", openid, WeixinSender.createWxMsgMap(ACTIVE_WILL_START,
                openid, data, url));
    }
    /**
     * 用户绑定通知
     * @param openid
     * @param nickname
     * @param contact
     */
    public static void sendUserBindNotice( String openid, String nickname, String contact, String JYZX_INDEX_URL){
        String BIND_USER = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "WX_MSG_BIND_USER").getConfigValue();
        Map data = new HashMap();
        data.put("first", WeixinSender.createWXKeyWord("亲爱的会员，您已使用" + contact + "绑定", null));
        data.put("keyword1", WeixinSender.createWXKeyWord(nickname, "#FF3030"));
        data.put("keyword2", WeixinSender.createWXKeyWord(DateUtils.formatHms(new Date()), null));
        data.put("remark", WeixinSender.createWXKeyWord("若非本人操作，请联系班主任，谢谢。", null));
        noticeService().insert("WX", openid, WeixinSender.createWxMsgMap(BIND_USER,
                openid, data, JYZX_INDEX_URL));
    }
    /**
     * 会员到期通知
     * @param openid
     * @param nickname
     * @param userLevel
     * @param expDate
     */
    public static void sendVipExpNotice( String openid, String nickname, String userLevel, String expDate, String JYZX_INDEX_URL){
        String VIP_EXP = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "WX_MSG_VIP_EXP").getConfigValue();
        String url = "";
        if(userLevel.equals("VIP")){
            url = JYZX_INDEX_URL + BaseConstant.BE_VIP_URL;
        } else if(userLevel.equals("SVIP")){
            url = JYZX_INDEX_URL + BaseConstant.BE_SVIP_URL;
        }
        Map data = new HashMap();
        data.put("first", WeixinSender.createWXKeyWord("亲爱的" + nickname +"，您的" + userLevel + "即将到期", null));
        data.put("keyword1", WeixinSender.createWXKeyWord(userLevel, "#FF3030"));
        data.put("keyword2", WeixinSender.createWXKeyWord(expDate, null));
        data.put("remark", WeixinSender.createWXKeyWord("点击立即续期。", null));
        noticeService().insert("WX", openid, WeixinSender.createWxMsgMap(VIP_EXP,
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
        String BUY_OFFER = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "WX_MSG_BUY_OFFER").getConfigValue();
        Map data = new HashMap();
        data.put("name", WeixinSender.createWXKeyWord(offerName, "#FF3030"));
        data.put("remark", WeixinSender.createWXKeyWord("点击可查看商品详情。", null));
        noticeService().insert("WX", openid, WeixinSender.createWxMsgMap(BUY_OFFER,
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
        String ACTIVE_IN = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "WX_MSG_ACTIVE_IN").getConfigValue();
        Map data = new HashMap();
        data.put("first", WeixinSender.createWXKeyWord("亲爱的" + nickname +"，您已成功报名：" + activeName, null));
        data.put("keyword1", WeixinSender.createWXKeyWord(activeTime, "#FF3030"));
        data.put("keyword2", WeixinSender.createWXKeyWord(addr, null));
        data.put("remark", WeixinSender.createWXKeyWord("您的票号：" + ticketNo + "。期待您的到来。点击可查看活动详情。", null));
        noticeService().insert("WX", openid, WeixinSender.createWxMsgMap(ACTIVE_IN,
                openid, data, url));
    }
    /**
     * 证书领取
     * @param openid
     * @param nickname
     * @param certifyName
     * @param url
     */
    public static void sendGetCertifyNotice( String openid, String nickname, String certifyName, String certifyTime,
                                           String content, String url){
        String ACTIVE_IN = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "WX_MSG_GET_CERTIFY").getConfigValue();
        Map data = new HashMap();
        data.put("first", WeixinSender.createWXKeyWord("亲爱的" + nickname +"，您通过考试，成功获取证书一张", null));
        data.put("keyword1", WeixinSender.createWXKeyWord(certifyName, "#FF3030"));
        data.put("keyword2", WeixinSender.createWXKeyWord(certifyTime, null));
        data.put("keyword2", WeixinSender.createWXKeyWord(content, null));
        data.put("remark", WeixinSender.createWXKeyWord("点击我的成绩，可查看证书。", null));
        noticeService().insert("WX", openid, WeixinSender.createWxMsgMap(ACTIVE_IN,
                openid, data, url));
    }

    private static NoticeService noticeService(){
        return SpringApplicationContextUtil.getBean(NoticeService.class);
    }

}
