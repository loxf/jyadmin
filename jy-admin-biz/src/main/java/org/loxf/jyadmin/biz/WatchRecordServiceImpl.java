package org.loxf.jyadmin.biz;

import org.apache.commons.collections.CollectionUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.client.dto.WatchRecordDto;
import org.loxf.jyadmin.client.service.WatchRecordService;
import org.loxf.jyadmin.dal.dao.WatchRecordMapper;
import org.loxf.jyadmin.dal.po.WatchRecord;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("watchRecordService")
public class WatchRecordServiceImpl implements WatchRecordService {
    @Autowired
    private WatchRecordMapper watchRecordMapper;
    @Override
    public BaseResult<Boolean> insert(WatchRecordDto watchRecordDto) {
        WatchRecord watchRecord = new WatchRecord();
        BeanUtils.copyProperties(watchRecordDto, watchRecord);
        return new BaseResult<>(watchRecordMapper.insert(watchRecord)>0);
    }

    @Override
    public PageResult<WatchRecordDto> pager(WatchRecordDto watchRecordDto) {
        WatchRecord watchRecord = new WatchRecord();
        BeanUtils.copyProperties(watchRecordDto, watchRecord);
        int count = watchRecordMapper.count(watchRecord);List<WatchRecordDto> result = null;
        if (count > 0) {
            List<WatchRecord> list = watchRecordMapper.list(watchRecord);
            if (CollectionUtils.isNotEmpty(list)) {
                result = new ArrayList<>();
                for (WatchRecord tmp : list) {
                    WatchRecordDto dto = new WatchRecordDto();
                    BeanUtils.copyProperties(tmp, dto);
                    result.add(dto);
                }
            }
        }
        int page = count / watchRecordDto.getPager().getSize() + (count % watchRecordDto.getPager().getSize() > 0 ? 1 : 0);
        return new PageResult<>(page, watchRecordDto.getPager().getPage(), count, result);
    }
}
