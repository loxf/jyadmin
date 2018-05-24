package org.loxf.jyadmin.biz.thread;

import org.apache.commons.collections.CollectionUtils;
import org.loxf.jyadmin.base.exception.BizException;
import org.loxf.jyadmin.client.dto.OrderDto;
import org.loxf.jyadmin.client.service.OrderService;
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
    private OrderService orderService;

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
        timer.schedule(new Task(prefix + this.getClass().getSimpleName(), expireLockMSecd, lockTimeout, new Runnable() {
            @Override
            public void run() {
                // 获取15分钟超时订单
                List<OrderDto> list = orderService.queryTimeoutOrder().getData();
                if (CollectionUtils.isNotEmpty(list)) {
                    for (OrderDto order : list) {
                        try {
                            orderService.cancelOrder(order.getOrderId(), "订单超时关闭");
                        } catch (BizException e) {
                            logger.error("订单超时取消失败：", e);
                        } catch (Exception e){
                            logger.error("订单超时取消异常：", e);
                        }
                    }
                }
            }
        }), 5000L, period);
    }

}
