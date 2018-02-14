package org.loxf.jyadmin.biz.thread;

import org.apache.commons.collections.CollectionUtils;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.client.dto.EventDto;
import org.loxf.jyadmin.client.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Timer;

/**
 * 事件处理
 */
public class EventJob extends JOB {
    private static Logger logger = LoggerFactory.getLogger(EventJob.class);

    private int expireLockMSecd = 30000;
    private int lockTimeout;
    private int period;

    @Autowired
    private EventService eventService;

    /**
     * @param expireLockMSecd 锁失效时间
     * @param lockTimeout     获取锁的等待时间
     * @param period          业务执行间隔时间
     */
    public EventJob(int expireLockMSecd, int lockTimeout, int period) {
        this.expireLockMSecd = expireLockMSecd;
        this.lockTimeout = lockTimeout;
        this.period = period;
    }

    public void start() {
        Timer timer = new Timer();
        timer.schedule(new Task(prefix + this.getClass().getSimpleName(), expireLockMSecd, lockTimeout, new Runnable() {
            @Override
            public void run() {
                // 获取待处理事件
                EventDto event = new EventDto();
                event.setStatus(1);
                PageResult<EventDto> pageResult = eventService.pager(event, 1, 100);
                if (pageResult.getCode()==BaseConstant.SUCCESS && CollectionUtils.isNotEmpty(pageResult.getData())) {
                    for (EventDto eventDto : pageResult.getData()) {
                        eventService.runEvent(eventDto);
                    }
                }
            }
        }), 5000l, period);
    }

}
