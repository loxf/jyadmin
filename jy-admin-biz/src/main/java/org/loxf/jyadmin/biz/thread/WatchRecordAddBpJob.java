package org.loxf.jyadmin.biz.thread;

import org.apache.commons.collections.CollectionUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.biz.util.ConfigUtil;
import org.loxf.jyadmin.client.service.AccountService;
import org.loxf.jyadmin.dal.dao.OrderMapper;
import org.loxf.jyadmin.dal.dao.WatchRecordMapper;
import org.loxf.jyadmin.dal.po.Order;
import org.loxf.jyadmin.dal.po.WatchRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Timer;

/**
 * 取消订单
 */
public class WatchRecordAddBpJob extends JOB {
    private static Logger logger = LoggerFactory.getLogger(WatchRecordAddBpJob.class);

    private int expireLockMSecd = 60000;
    private int lockTimeout;
    private int period;

    @Autowired
    private WatchRecordMapper watchRecordMapper;

    @Autowired
    private AccountService accountService;

    /**
     * @param expireLockMSecd 锁失效时间
     * @param lockTimeout     获取锁的等待时间
     * @param period          业务执行间隔时间
     */
    public WatchRecordAddBpJob(int expireLockMSecd, int lockTimeout, int period) {
        this.expireLockMSecd = expireLockMSecd;
        this.lockTimeout = lockTimeout;
        this.period = period;
    }

    public void start() {
        Timer timer = new Timer();
        timer.schedule(new Task(prefix + this.getClass().getSimpleName(), expireLockMSecd, lockTimeout, new Runnable() {
            @Override
            public void run() {
                // 获取15分钟超时订单
                List<WatchRecord> list = watchRecordMapper.queryNeedAddBp();
                if (CollectionUtils.isNotEmpty(list)) {
                    for (WatchRecord watchRecord : list) {
                        // 增加bp
                        String preMinBp = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_BP, "BP_WATCH_VIDEO",
                                "0.5").getConfigValue();
                        Double bp = watchRecord.getMinutes() * new Double(preMinBp);
                        if(bp>50.0){// 每个视频 每次观看最多50积分
                            bp = 50d;
                        }
                        try {
                            if(bp>0) {
                                BaseResult<Boolean> baseResult = accountService.increase(watchRecord.getCustId(), null, new BigDecimal(bp), watchRecord.getWatchId(),
                                        "观看视频得积分", "");
                                if (baseResult.getCode() == BaseConstant.FAILED ) {
                                    logger.error("更新观看记录失败" + baseResult.getMsg());
                                }
                            }
                        } catch (Exception e) {
                            logger.error("处理观看积分失败：", e);
                        } finally {
                            watchRecordMapper.updateStatus(watchRecord.getWatchId());
                        }
                    }
                }
            }
        }), 5000l, period);
    }

}
