package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.client.dto.ActiveDto;

public interface ActiveService {
    PageResult<ActiveDto> pager(ActiveDto activeDto);
    BaseResult<ActiveDto> queryActive(String activeId);
    BaseResult newActive(ActiveDto activeDto);
    BaseResult updateActive(ActiveDto activeDto);
    BaseResult deleteActive(String activeId);
    BaseResult onOrOffActive(String activeId, Integer status);
    BaseResult sendIndexRecommend(String activeId);
}
