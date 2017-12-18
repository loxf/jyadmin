package org.loxf.jyadmin.biz.thread;

import org.apache.commons.collections.CollectionUtils;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.bean.Pager;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.client.dto.CustCashDto;
import org.loxf.jyadmin.client.service.CustCashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Timer;

/**
 * 提现
 */
public class TakeCashJob extends JOB {
    private static Logger logger = LoggerFactory.getLogger(TakeCashJob.class);

    private int expireLockMSecd = 60000;
    private int lockTimeout;
    private int period;

    @Autowired
    private CustCashService custCashService;

    /**
     * @param expireLockMSecd 锁失效时间
     * @param lockTimeout     获取锁的等待时间
     * @param period          业务执行间隔时间
     */
    public TakeCashJob(int expireLockMSecd, int lockTimeout, int period) {
        this.expireLockMSecd = expireLockMSecd;
        this.lockTimeout = lockTimeout;
        this.period = period;
    }

    public void start() {
        Timer timer = new Timer();
        timer.schedule(new Task(prefix + this.getClass().getSimpleName(), expireLockMSecd, lockTimeout, new Runnable() {
            @Override
            public void run() {
                // 获取待处理的体现订单
                CustCashDto custCashDto = new CustCashDto();
                custCashDto.setStatus(1);
                custCashDto.setLock(0);// 可以执行的
                custCashDto.setPager(new Pager(1, 200));
                PageResult<CustCashDto> pageResult = custCashService.queryCustCash(custCashDto);
                if (pageResult.getCode()== BaseConstant.SUCCESS &&  CollectionUtils.isNotEmpty(pageResult.getData())) {
                    for (CustCashDto cashDto : pageResult.getData()) {
                        custCashService.takeCashDeal(cashDto);
                    }
                }
            }
        }), 5000l, period);
    }

}
