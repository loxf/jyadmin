package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.client.dto.SystemLogDto;

public interface SystemLogService {
    void log(SystemLogDto systemLogDto);
}
