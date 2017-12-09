package org.loxf.jyadmin.biz.thread;

import org.apache.commons.collections.CollectionUtils;
import org.loxf.jyadmin.client.service.CustService;
import org.loxf.jyadmin.client.service.TradeService;
import org.loxf.jyadmin.client.service.VipInfoService;
import org.loxf.jyadmin.dal.dao.TradeMapper;
import org.loxf.jyadmin.dal.dao.VipInfoMapper;
import org.loxf.jyadmin.dal.po.Trade;
import org.loxf.jyadmin.dal.po.VipInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Timer;

/**
 * 会员过期
 */
public class CustLevelJob extends JOB {
    private static Logger logger = LoggerFactory.getLogger(CustLevelJob.class);

    private int expireLockMSecd = 60000;
    private int lockTimeout;
    private int period;

    @Autowired
    private VipInfoMapper vipInfoMapper;
    @Autowired
    private CustService custService;

    /**
     * @param expireLockMSecd 锁失效时间
     * @param lockTimeout     获取锁的等待时间
     * @param period          业务执行间隔时间
     */
    public CustLevelJob(int expireLockMSecd, int lockTimeout, int period) {
        this.expireLockMSecd = expireLockMSecd;
        this.lockTimeout = lockTimeout;
        this.period = period;
    }

    public void start() {
        Timer timer = new Timer();
        timer.schedule(new Task(prefix + this.getClass().getName(), expireLockMSecd, lockTimeout, new Runnable() {
            @Override
            public void run() {
                // 获取交易订单
                List<VipInfo> list = vipInfoMapper.queryExpireInfo();
                if (CollectionUtils.isNotEmpty(list)) {
                    for (VipInfo vipInfo : list) {
                        custService.unvalidVip(vipInfo.getCustId());
                    }
                }
            }
        }), 5000l, period);
    }

}
