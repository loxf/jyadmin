package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.client.dto.EventDto;

public interface EventService {
    BaseResult addEvent(String code, String key, String metaData);
    BaseResult refreshEvent(String code, String key, Integer status, String remark);
    PageResult<EventDto> pager(EventDto eventDto, Integer page, Integer size);
    void runEvent(EventDto eventDto);
}
