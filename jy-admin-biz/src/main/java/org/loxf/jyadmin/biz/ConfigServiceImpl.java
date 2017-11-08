package org.loxf.jyadmin.biz;

import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.util.JedisUtil;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.client.dto.ConfigDto;
import org.loxf.jyadmin.client.service.ConfigService;
import org.loxf.jyadmin.dal.dao.ConfigMapper;
import org.loxf.jyadmin.dal.po.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("configService")
public class ConfigServiceImpl implements ConfigService {
    private static Logger logger = LoggerFactory.getLogger(ConfigServiceImpl.class);
    private static String CACHE_NAME = "JY_CACHE_CONFIG";
    @Autowired
    private ConfigMapper configMapper;
    @Autowired
    private JedisUtil jedisUtil;

    @Override
    public PageResult<ConfigDto> pager(ConfigDto configDto) {
        if(configDto==null){
            return new PageResult<>(BaseConstant.FAILED, "入参不能为空");
        }
        Config config = new Config();
        BeanUtils.copyProperties(configDto, config);
        int total = configMapper.count(config);
        List<ConfigDto> dtos = new ArrayList<>();
        if(total>0) {
            List<Config> custList = configMapper.list(config);
            for(Config po : custList){
                ConfigDto tmp = new ConfigDto();
                BeanUtils.copyProperties(po, tmp);
                dtos.add(tmp);
            }
        }
        int tatalPage = total/configDto.getPager().getSize() + (total%configDto.getPager().getSize()==0?0:1);
        return new PageResult<ConfigDto>(tatalPage, configDto.getPager().getPage(), total, dtos);
    }

    @Override
    @Transactional
    public BaseResult newConfig(ConfigDto configDto) {
        if(StringUtils.isBlank(configDto.getConfigCode())||StringUtils.isBlank(configDto.getConfigName())
                ||StringUtils.isBlank(configDto.getConfigValue())||StringUtils.isBlank(configDto.getType())
                ||StringUtils.isBlank(configDto.getCatalog())){
            return new BaseResult(BaseConstant.FAILED, "参数不全");
        }
        Config config = new Config();
        BeanUtils.copyProperties(configDto, config);
        if(configMapper.insert(config)>0) {
            return new BaseResult(configDto.getConfigCode());
        } else {
            return new BaseResult(BaseConstant.FAILED, "新增失败");
        }
    }

    @Override
    @Transactional
    public BaseResult updateConfig(ConfigDto configDto) {
        if(StringUtils.isBlank(configDto.getConfigCode())||StringUtils.isBlank(configDto.getConfigName())
                ||StringUtils.isBlank(configDto.getConfigValue())||StringUtils.isBlank(configDto.getType())
                ||configDto.getId()==null){
            return new BaseResult(BaseConstant.FAILED, "参数不全");
        }
        Config config = new Config();
        BeanUtils.copyProperties(configDto, config);
        if(configMapper.updateByPrimaryKey(config)>0) {
            putCacheById(configDto.getId());
            return new BaseResult(configDto.getConfigCode());
        } else {
            return new BaseResult(BaseConstant.FAILED, "编辑失败");
        }
    }

    @Override
    @Transactional
    public BaseResult deleteConfig(String catalog, String configCode) {
        if(StringUtils.isBlank(catalog)||StringUtils.isBlank(configCode)){
            return new BaseResult(BaseConstant.FAILED, "参数不全");
        }
        if(configMapper.deleteConfig(catalog, configCode)>0) {
            try {
                jedisUtil.delFromMap(CACHE_NAME, catalog + "_" + configCode);
            } catch (Exception e) {
                logger.error("系统配置保存缓存失败", e);
            }
            return new BaseResult(configCode);
        } else {
            return new BaseResult(BaseConstant.FAILED, "编辑失败");
        }
    }

    @Override
    @Transactional
    public BaseResult onOrOffConfig(Long id, Integer status) {
        if(configMapper.onOrOffConfig(id, status)>0) {
            putCacheById(id);
            return new BaseResult();
        } else {
            return new BaseResult(BaseConstant.FAILED, BaseConstant.FAILED_MSG);
        }
    }

    @Override
    public BaseResult<ConfigDto> queryConfig(String catalog, String configCode) {
        ConfigDto dto = (ConfigDto)jedisUtil.getFromMap(CACHE_NAME, catalog + "_" + configCode);
        if(dto==null) {
            Config config = configMapper.selectConfig(catalog, configCode);
            if (config == null) {
                return new BaseResult<>(BaseConstant.FAILED, "配置不存在");
            }
            dto = new ConfigDto();
            BeanUtils.copyProperties(config, dto);
            putCache(dto);
        }
        return new BaseResult<>(dto);
    }

    private void putCacheById(long id){
        Config again = configMapper.selectById(id);
        ConfigDto dto = new ConfigDto();
        BeanUtils.copyProperties(again, dto);
        putCache(dto);
    }

    private void putCache(ConfigDto dto){
        try {
            jedisUtil.putMap(CACHE_NAME, dto.getCatalog()+ "_" + dto.getConfigCode(), dto);
        } catch (Exception e) {
            logger.error("系统配置保存缓存失败", e);
        }
    }
}
