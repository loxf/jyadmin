package org.loxf.jyadmin.biz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.util.IdGenerator;
import org.loxf.jyadmin.client.dto.WatchRecordDto;
import org.loxf.jyadmin.client.service.AccountService;
import org.loxf.jyadmin.client.service.WatchRecordService;
import org.loxf.jyadmin.dal.dao.WatchRecordMapper;
import org.loxf.jyadmin.dal.po.Account;
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
    public BaseResult<String> watch(WatchRecordDto watchRecordDto) {
        if (StringUtils.isBlank(watchRecordDto.getWatchId())) {
            WatchRecord watchRecord = new WatchRecord();
            BeanUtils.copyProperties(watchRecordDto, watchRecord);
            String watchId = IdGenerator.generate("WATCH");
            watchRecord.setWatchId(watchId);
            if (watchRecordMapper.insert(watchRecord) > 0) {
                return new BaseResult(watchId);
            } else {
                return new BaseResult<>(BaseConstant.FAILED, "新增观看记录失败");
            }
        } else {
            watchRecordMapper.updateMinutes(watchRecordDto.getWatchId());
            return new BaseResult<>(watchRecordDto.getWatchId());
        }
    }

    @Override
    public PageResult<WatchRecordDto> pager(WatchRecordDto watchRecordDto) {
        WatchRecord watchRecord = new WatchRecord();
        BeanUtils.copyProperties(watchRecordDto, watchRecord);
        int count = watchRecordMapper.count(watchRecord);
        List<WatchRecordDto> result = null;
        if (count > 0) {
            List<WatchRecord> list = watchRecordMapper.list(watchRecord);
            if (CollectionUtils.isNotEmpty(list)) {
                result = new ArrayList<>();
                for (WatchRecord tmp : list) {
                    WatchRecordDto dto = new WatchRecordDto();
                    BeanUtils.copyProperties(tmp, dto);
                    String metaData = tmp.getMetaData();
                    if (StringUtils.isNotBlank(metaData)) {
                        JSONObject metaDataJson = JSON.parseObject(metaData);
                        JSONArray teachers = metaDataJson.getJSONArray("TEACHER");
                        String teacherStr = "";
                        if (CollectionUtils.isNotEmpty(teachers)) {
                            for (Object teacher : teachers) {
                                if(StringUtils.isNotBlank(teacherStr)){
                                    teacherStr += ",";
                                }
                                teacherStr += ((JSONObject)teacher).get("name");
                            }
                        }
                        dto.setTeachers(teacherStr);
                        dto.setVideoName(dto.getOfferName());
                    }
                    result.add(dto);
                }
            }
        }
        int page = count / watchRecordDto.getPager().getSize() + (count % watchRecordDto.getPager().getSize() > 0 ? 1 : 0);
        return new PageResult<>(page, watchRecordDto.getPager().getPage(), count, result);
    }
}
