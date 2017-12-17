package org.loxf.jyadmin.biz.thread;

import org.apache.commons.collections.CollectionUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.client.service.TradeService;
import org.loxf.jyadmin.dal.dao.OrderMapper;
import org.loxf.jyadmin.dal.dao.TradeMapper;
import org.loxf.jyadmin.dal.po.Order;
import org.loxf.jyadmin.dal.po.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Timer;

/**
 * 交易处理
 */
public class TradeJob extends JOB {
    private static Logger logger = LoggerFactory.getLogger(TradeJob.class);

    private int expireLockMSecd = 60000;
    private int lockTimeout;
    private int period;

    @Autowired
    private TradeMapper tradeMapper;
    @Autowired
    private TradeService tradeService;

    /**
     * @param expireLockMSecd 锁失效时间
     * @param lockTimeout     获取锁的等待时间
     * @param period          业务执行间隔时间
     */
    public TradeJob(int expireLockMSecd, int lockTimeout, int period) {
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
                while (true) {
                    List<Trade> list = tradeMapper.selectList(1, 200);
                    if (CollectionUtils.isNotEmpty(list)) {
                        for (Trade trade : list) {
                            BaseResult baseResult = tradeService.completeTrade(trade.getOrderId(), 3, null);
                            if(baseResult.getCode()== BaseConstant.FAILED){
                                logger.error("订单交易处理失败[" + trade.getOrderId() + "]：" + baseResult.getMsg());
                            }
                        }
                    } else {
                        break;
                    }
                }
            }
        }), 5000l, period);
    }

}
