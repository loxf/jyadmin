package org.loxf.jyadmin.biz.thread;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.util.weixin.WeixinUtil;
import org.loxf.jyadmin.base.util.weixin.bean.UserAccessToken;
import org.loxf.jyadmin.biz.msg.EmailSender;
import org.loxf.jyadmin.biz.msg.ISender;
import org.loxf.jyadmin.biz.msg.SmsSender;
import org.loxf.jyadmin.biz.msg.WeixinSender;
import org.loxf.jyadmin.biz.util.ConfigUtil;
import org.loxf.jyadmin.dal.dao.NoticeMapper;
import org.loxf.jyadmin.dal.dao.WxUserTokenMapper;
import org.loxf.jyadmin.dal.po.Notice;
import org.loxf.jyadmin.dal.po.WxUserToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;

/**
 * 通知处理
 */
public class CustTokenRefreshJob extends JOB {
    private static Logger logger = LoggerFactory.getLogger(CustTokenRefreshJob.class);

    private int expireLockMSecd = 60000;
    private int lockTimeout;
    private int period;

    @Autowired
    private WxUserTokenMapper wxUserTokenMapper;

    /**
     * @param expireLockMSecd 锁失效时间
     * @param lockTimeout     获取锁的等待时间
     * @param period          业务执行间隔时间
     */
    public CustTokenRefreshJob(int expireLockMSecd, int lockTimeout, int period) {
        this.expireLockMSecd = expireLockMSecd;
        this.lockTimeout = lockTimeout;
        this.period = period;
    }

    public void start() {
        Timer timer = new Timer();
        timer.schedule(new Task(prefix + this.getClass().getSimpleName(), expireLockMSecd, lockTimeout, new Runnable() {
            @Override
            public void run() {
                // 获取用户token
                String WX_APPID = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_RUNTIME, "WX_APPID").getConfigValue();
                List<WxUserToken> list = wxUserTokenMapper.queryNeedRefreshToken();
                if (CollectionUtils.isNotEmpty(list)) {
                    for (WxUserToken wxUserToken : list) {
                        String refreshToken = wxUserToken.getRefresh_token();
                        UserAccessToken userAccessToken = WeixinUtil.refreshUserAccessToken(WX_APPID, refreshToken);
                        if(userAccessToken!=null) {
                            wxUserToken.setAccess_token(userAccessToken.getAccess_token());
                            wxUserToken.setExpires_in((Integer.valueOf(userAccessToken.getExpires_in()) * 1000 + System.currentTimeMillis()) + "");
                            wxUserToken.setRefresh_token(userAccessToken.getRefresh_token());
                            wxUserTokenMapper.updateByPrimaryKey(wxUserToken);
                        } else {
                            wxUserToken.setExpires_in((3600 * 1000 + System.currentTimeMillis()) + "");
                            wxUserTokenMapper.updateByPrimaryKey(wxUserToken);
                        }
                    }
                }
            }
        }), 5000l, period);
    }

}
