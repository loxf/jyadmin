package org.loxf.jyadmin.biz.thread.event;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.dal.po.Event;

public interface IEvent {
    BaseResult run(Event event);
}
