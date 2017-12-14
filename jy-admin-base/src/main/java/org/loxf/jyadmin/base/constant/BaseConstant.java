package org.loxf.jyadmin.base.constant;

public class BaseConstant {
    public static final int FAILED = 0;
    public static final int SUCCESS = 1;
    public static final int NOT_LOGIN = -1;
    public static final String SUCCESS_MSG = "操作成功!";
    public static final String FAILED_MSG = "操作失败!";
    public static String USER_COOKIE_NAME = "JY_TOKEN";
    public static String JY_CUST_ID = "JY_CUST_ID";
    public static String WX_BODYPREFIX = "静怡雅学馆";
    public static String ADMIN_COOKIE_NAME = "JYADMIN_TOKEN";
    public static String LOGIN_URL = "https://www.jingyizaixian.com/api/loginByWx?targetUrl=%s";
    public static String WX_TOKEN = "jyzx2015weixin";
    public static String WX_APPSECRET = "8a51e801fcc89fc9aab671737560b4ed";
    public static String WX_APPID = "wxf45ae0ff3e1bd94e";
    public static String WX_MCHID = "1243841702";
    public static String WX_MCH_KEY = "198842500kaikaikaikaikaikaikaika";
    public static String WX_EncodingAESKey = "OTHx0cgmgNyHHJFxyuKeVQz7Ovzntifve1k6HJUWE1P";
    public static String WX_ACCESS_TOKEN = "WX_ACCESS_TOKEN";
    public static String WX_JS_TICKET = "WX_JS_TICKET";
    public static String WX_MCH_SCURET = "jingyizaixianPay1988425";

    public static String CONFIG_TYPE_RUNTIME = "RUNTIME";
    public static String CONFIG_TYPE_PAY = "PAY";
    public static String CONFIG_TYPE_BP = "BP";
    public static String CONFIG_TYPE_COM = "COM";

    public static String WX_PAY_CALLBACK = "https://www.jingyizaixian.com/api/weixin/payorder";

    public static String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
    public static String USER_AUTH_CODE_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=%s#wechat_redirect";
    public static String QUERY_USER_TOKEN_BY_CODE_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
    public static String REFRESH_USER_TOKEN_BY_CODE_URL = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=%s&grant_type=refresh_token&refresh_token=%s";
    public static String USER_INFO_URL = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=zh_CN";
    public static String CHECK_USER_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/auth?access_token=%s&openid=%s";
    public static String JS_TICKET_URL = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=%s&type=jsapi";
    public static String WEIXIN_RSA_API = "https://fraud.mch.weixin.qq.com/risk/getpublickey";

    // 套餐详情
    public static String JYZX_INDEX_URL =  "https://www.jingyizaixian.com?recommend=%s";
    // 套餐详情
    public static String OFFER_DETAIL_URL =  "https://www.jingyizaixian.com/packageDetail?id=%s";
    // 活动详情
    public static String ACTIVE_DETAIL_URL =  "https://www.jingyizaixian.com/activityDetail?id=%s";
    // 课程详情
    public static String CLASS_DETAIL_URL =  "https://www.jingyizaixian.com/lessonDetail?id=%s";

    // 微信菜单
    // 创建
    public static String WEIXIN_MENU_CREATE = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=%s";
    // 自定义菜单查询接口
    public static String WEIXIN_MENU_GET = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=%s";
    // 自定义菜单删除接口
    public static String WEIXIN_MENU_DEL = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=%s";
    // 获取自定义菜单配置接口
    public static String WEIXIN_MENU_SELFMENU = "https://api.weixin.qq.com/cgi-bin/get_current_selfmenu_info?access_token=%s";

    // 微信付款-微信
    public static String WEIXIN_PAY_OPENID = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";
    // 微信付款-银行
    public static String WEIXIN_PAY_BANK = "https://api.mch.weixin.qq.com/mmpaysptrans/pay_bank";
}
