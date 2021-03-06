package org.loxf.jyadmin.base.constant;

public class BaseConstant {
    /** 返回码常量 */
    public static final int FAILED = 0;
    public static final int SUCCESS = 1;
    public static final int NOT_LOGIN = -1;
    public static final String SUCCESS_MSG = "操作成功!";
    public static final String FAILED_MSG = "操作失败!";

    /**
     * 服务器地址
     */
    public static String SERVER_IP = "36.24.18.156";
    // token相关
    public static String USER_COOKIE_NAME = "JY_TOKEN";
    public static String JY_CUST_ID = "JY_CUST_ID";
    public static String ADMIN_COOKIE_NAME = "JYADMIN_TOKEN";
    public static String WX_JS_TICKET = "WX_JS_TICKET";
    // 微信相关
    public static String WX_ACCESS_TOKEN = "WX_ACCESS_TOKEN";

    // 配置相关
    public static String CONFIG_TYPE_RUNTIME = "RUNTIME";
    public static String CONFIG_TYPE_PAY = "PAY";
    public static String CONFIG_TYPE_BP = "BP";
    public static String CONFIG_TYPE_COM = "COM";

    // 微信接口
    // ACCESS TOKEN获取接口
    public static String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
    /**
     * 微信用户登录接口
     */
    public static String USER_AUTH_CODE_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=%s#wechat_redirect";
    /**
     * 获取微信用户TOKEN接口
     */
    public static String QUERY_USER_TOKEN_BY_CODE_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
    /**
     * 刷新用户TOKEN接口
     */
    public static String REFRESH_USER_TOKEN_BY_CODE_URL = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=%s&grant_type=refresh_token&refresh_token=%s";
    /**
     * 用户信息接口
     */
    public static String USER_INFO_URL = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=zh_CN";
    /**
     * 检查用户access token接口
     */
    public static String CHECK_USER_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/auth?access_token=%s&openid=%s";
    /**
     * js ticket接口
     */
    public static String JS_TICKET_URL = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=%s&type=jsapi";
    /**
     * 微信RSA加密公钥接口
     */
    public static String WEIXIN_RSA_API = "https://fraud.mch.weixin.qq.com/risk/getpublickey";
    /**
     * 微信付款-微信
     */
    public static String WEIXIN_PAY_OPENID = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";
    /**
     * 微信付款-银行
     */
    public static String WEIXIN_PAY_BANK = "https://api.mch.weixin.qq.com/mmpaysptrans/pay_bank";
    /**
     * 微信模板消息接口
     */
    public static String WEIXIN_TEMPLATE_MSG = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s";

    // 登录地址
    public static String LOGIN_URL = "/api/loginByWx?targetUrl=%s";
    // 微信回调地址
    public static String WX_PAY_CALLBACK = "/api/weixin/payorder";
    // 奖学金页面
    public static String JYZX_ACCOUNT_URL =  "/myAccount";
    // 申请代理商
    public static String JYZX_BE_AGENT_URL = "/beTheAgent";
    // APP主页推广二维码地址
    public static String JYZX_INDEX_RECOMMEND_URL =  "?recommend=%s";
    // 套餐详情
    public static String OFFER_DETAIL_URL =  "/packageDetail?id=%s";
    // 活动详情
    public static String ACTIVE_DETAIL_URL =  "/activityDetail?id=%s";
    // 课程详情
    public static String CLASS_DETAIL_URL =  "/lessonDetail?id=%s";
    // 升级VIP
    public static String BE_VIP_URL = "/confirmOrder?type=VIP&id=OFFER001&otherId=OFFER002";
    // 升级SVIP
    public static String BE_SVIP_URL = "/confirmOrder?type=VIP&id=OFFER002";

    // 微信菜单
    // 创建
    public static String WEIXIN_MENU_CREATE = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=%s";
    // 自定义菜单查询接口
    public static String WEIXIN_MENU_GET = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=%s";
    // 自定义菜单删除接口
    public static String WEIXIN_MENU_DEL = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=%s";
    // 获取自定义菜单配置接口
    public static String WEIXIN_MENU_SELFMENU = "https://api.weixin.qq.com/cgi-bin/get_current_selfmenu_info?access_token=%s";

}
