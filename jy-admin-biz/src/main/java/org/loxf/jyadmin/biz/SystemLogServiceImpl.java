package org.loxf.jyadmin.biz;

import org.loxf.jyadmin.client.dto.SystemLogDto;
import org.loxf.jyadmin.client.service.SystemLogService;
import org.loxf.jyadmin.dal.dao.SystemLogMapper;
import org.loxf.jyadmin.dal.po.SystemLog;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("systemLogService")
public class SystemLogServiceImpl implements SystemLogService {
    @Autowired
    private SystemLogMapper systemLogMapper;
    @Override
    public void log(SystemLogDto systemLogDto) {
        SystemLog systemLog = new SystemLog();
        BeanUtils.copyProperties(systemLogDto, systemLog);
        systemLogMapper.insert(systemLog);
    }
}
