package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.client.dto.ConfigDto;

public interface ConfigService {
    PageResult<ConfigDto> pager(ConfigDto configDto);
    BaseResult newConfig(ConfigDto configDto);
    BaseResult updateConfig(ConfigDto configDto);
    BaseResult deleteConfig(String catalog, String configCode);
    BaseResult onOrOffConfig(Long id, Integer status);
    BaseResult<ConfigDto> queryConfig(String catalog, String configCode);
}
