package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.client.dto.WatchRecordDto;

public interface WatchRecordService {
    BaseResult<String> watch(WatchRecordDto watchRecordDto);
    PageResult<WatchRecordDto> pager(WatchRecordDto watchRecordDto);

}
