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

    /**
     * @param activeId
     * @param type 1: 设置首页轮播 2: 取消
     * @return
     */
    BaseResult sendIndexRecommend(String activeId, Integer type);
}
