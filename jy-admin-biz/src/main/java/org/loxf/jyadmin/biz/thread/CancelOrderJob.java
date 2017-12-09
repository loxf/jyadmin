package org.loxf.jyadmin.biz.thread;

import org.apache.commons.collections.CollectionUtils;
import org.loxf.jyadmin.dal.dao.OrderMapper;
import org.loxf.jyadmin.dal.po.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Timer;

/**
 * 取消订单
 */
public class CancelOrderJob extends JOB {
    private static Logger logger = LoggerFactory.getLogger(CancelOrderJob.class);

    private int expireLockMSecd = 60000;
    private int lockTimeout;
    private int period;

    @Autowired
    private OrderMapper orderMapper;

    /**
     * @param expireLockMSecd 锁失效时间
     * @param lockTimeout     获取锁的等待时间
     * @param period          业务执行间隔时间
     */
    public CancelOrderJob(int expireLockMSecd, int lockTimeout, int period) {
        this.expireLockMSecd = expireLockMSecd;
        this.lockTimeout = lockTimeout;
        this.period = period;
    }

    public void start() {
        Timer timer = new Timer();
        timer.schedule(new Task(prefix + this.getClass().getName(), expireLockMSecd, lockTimeout, new Runnable() {
            @Override
            public void run() {
                // 获取15分钟超时订单
                List<Order> list = orderMapper.queryTimeoutOrder();
                if (CollectionUtils.isNotEmpty(list)) {
                    for (Order order : list) {
                        orderMapper.updateByOrderId(order.getOrderId(), 9, "订单超时关闭");
                    }
                }
            }
        }), 5000l, period);
    }

}
