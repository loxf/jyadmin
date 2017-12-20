package org.loxf.jyadmin.biz.thread;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.biz.util.SendWeixinMsgUtil;
import org.loxf.jyadmin.client.service.CustService;
import org.loxf.jyadmin.dal.dao.AgentInfoMapper;
import org.loxf.jyadmin.dal.dao.VipInfoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;

/**
 * 代理商/会员/将过期提醒
 */
public class DeadlineNoticeJob extends JOB {
    private static Logger logger = LoggerFactory.getLogger(DeadlineNoticeJob.class);

    private int expireLockMSecd = 60000;
    private int lockTimeout;
    private int period;

    @Autowired
    private VipInfoMapper vipInfoMapper;
    @Autowired
    private AgentInfoMapper agentInfoMapper;
    @Autowired
    private CustService custService;

    /**
     * @param expireLockMSecd 锁失效时间
     * @param lockTimeout     获取锁的等待时间
     * @param period          业务执行间隔时间
     */
    public DeadlineNoticeJob(int expireLockMSecd, int lockTimeout, int period) {
        this.expireLockMSecd = expireLockMSecd;
        this.lockTimeout = lockTimeout;
        this.period = period;
    }

    public void start() {
        Timer timer = new Timer();
        timer.schedule(new Task(prefix + this.getClass().getSimpleName(), expireLockMSecd, lockTimeout, new Runnable() {
            @Override
            public void run() {
                // 获取失效VIP
                List<HashMap> vipInfoList = vipInfoMapper.queryWillExpireInfo();
                if (CollectionUtils.isNotEmpty(vipInfoList)) {
                    for (HashMap vipInfo : vipInfoList) {
                        SendWeixinMsgUtil.sendVipExpNotice((String)vipInfo.get("openid"), (String)vipInfo.get("nick_name"),
                                (String)vipInfo.get("type") + "会员" , (String)vipInfo.get("exp_date"));
                    }
                }
                // 获取失效代理
                List<HashMap> agentInfoList = agentInfoMapper.queryWillExpireInfo();
                if (CollectionUtils.isNotEmpty(agentInfoList)) {
                    for (HashMap agentInfo : agentInfoList) {
                        int type = (int)agentInfo.get("type");
                        String typeStr = "";
                        if(type==1){
                            typeStr = "代理商";
                        } else if(type==2){
                            typeStr = "合伙人";
                        } else if(type==3){
                            typeStr = "分公司";
                        }
                        if(StringUtils.isNotBlank(typeStr)) {
                            SendWeixinMsgUtil.sendVipExpNotice((String) agentInfo.get("openid"), (String) agentInfo.get("nick_name"),
                                    typeStr + "身份", (String) agentInfo.get("exp_date"));
                        }
                    }
                }
            }
        }), 5000l, period);
    }

}
