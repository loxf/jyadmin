package org.loxf.jyadmin.biz.thread;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.biz.msg.EmailSender;
import org.loxf.jyadmin.biz.msg.ISender;
import org.loxf.jyadmin.biz.msg.SmsSender;
import org.loxf.jyadmin.biz.msg.WeixinSender;
import org.loxf.jyadmin.client.service.TradeService;
import org.loxf.jyadmin.dal.dao.NoticeMapper;
import org.loxf.jyadmin.dal.dao.TradeMapper;
import org.loxf.jyadmin.dal.po.Notice;
import org.loxf.jyadmin.dal.po.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

/**
 * 通知处理
 */
public class NoticeJob extends JOB {
    private static Logger logger = LoggerFactory.getLogger(NoticeJob.class);

    private int expireLockMSecd = 60000;
    private int lockTimeout;
    private int period;

    @Autowired
    private NoticeMapper noticeMapper;

    /**
     * @param expireLockMSecd 锁失效时间
     * @param lockTimeout     获取锁的等待时间
     * @param period          业务执行间隔时间
     */
    public NoticeJob(int expireLockMSecd, int lockTimeout, int period) {
        this.expireLockMSecd = expireLockMSecd;
        this.lockTimeout = lockTimeout;
        this.period = period;
    }

    public void start() {
        Timer timer = new Timer();
        timer.schedule(new Task(prefix + this.getClass().getSimpleName(), expireLockMSecd, lockTimeout, new Runnable() {
            @Override
            public void run() {
                // 获取交易订单
                List<Notice> list = noticeMapper.queryNeedSend();
                if (CollectionUtils.isNotEmpty(list)) {
                    for (Notice notice : list) {
                        // 发送消息
                        ISender sender = null;
                        if (notice.getNoticeType().equals("WX")) {
                            sender = new WeixinSender();
                        } else if (notice.getNoticeType().equals("SMS")) {
                            sender = new SmsSender();
                        } else if (notice.getNoticeType().equals("EMAIL")) {
                            sender = new EmailSender();
                        } else {
                            notice.setStatus(-3);
                            notice.setRemark("发送方式不存在");
                        }
                        if (sender != null) {
                            String metaData = notice.getMetaData();
                            HashMap metaDataMap = null;
                            if (StringUtils.isNotBlank(metaData)) {
                                metaDataMap = JSON.parseObject(metaData, HashMap.class);
                            }
                            BaseResult sendResult = sender.send(metaDataMap, notice.getNoticeObj());
                            if (sendResult.getCode()==BaseConstant.SUCCESS) {
                                notice.setStatus(3);
                            } else {
                                notice.setStatus(-3);
                                notice.setRemark(sendResult.getMsg());
                            }
                        }
                        noticeMapper.updateByPrimaryKey(notice);
                    }
                }
            }
        }), 5000l, period);
    }

}
