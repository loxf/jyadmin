package org.loxf.jyadmin.biz;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.util.IdGenerator;
import org.loxf.jyadmin.client.dto.CertifyConfigDto;
import org.loxf.jyadmin.client.service.CertifyConfigService;
import org.loxf.jyadmin.dal.dao.CertifyConfigMapper;
import org.loxf.jyadmin.dal.po.CertifyConfig;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("certifyConfigService")
public class CertifyConfigServiceImpl implements CertifyConfigService {
    @Autowired
    private CertifyConfigMapper certifyConfigMapper;

    @Override
    public PageResult<CertifyConfigDto> pager(CertifyConfigDto certifyConfigDto) {
        if(certifyConfigDto==null){
            return new PageResult<>(BaseConstant.FAILED, "参数为空");
        }
        CertifyConfig certifyConfig = new CertifyConfig();
        BeanUtils.copyProperties(certifyConfigDto, certifyConfig);
        int total = certifyConfigMapper.selectCount(certifyConfig);
        List<CertifyConfigDto> result = new ArrayList<>();
        if(total>0) {
            List<CertifyConfig> list = certifyConfigMapper.selectList(certifyConfig);
            if(CollectionUtils.isNotEmpty(list)) {
                for (CertifyConfig config : list) {
                    CertifyConfigDto dto = new CertifyConfigDto();
                    BeanUtils.copyProperties(config, dto);
                    result.add(dto);
                }
            }
        }
        int totalPage = total/certifyConfigDto.getPager().getSize() + (total%certifyConfigDto.getPager().getSize()==0?0:1);
        return new PageResult(totalPage, certifyConfigDto.getPager().getPage(), total, result);
    }

    @Override
    @Transactional
    public BaseResult insert(CertifyConfigDto certifyConfigDto) {
        if(certifyConfigDto==null){
            return new BaseResult(BaseConstant.FAILED, "参数为空");
        }
        CertifyConfig certifyConfig = new CertifyConfig();
        BeanUtils.copyProperties(certifyConfigDto, certifyConfig);
        certifyConfig.setCertifyId(IdGenerator.generate("CERT"));
        if(certifyConfigMapper.insert(certifyConfig)>0) {
            return new BaseResult();
        } else {
            return new BaseResult(BaseConstant.FAILED, "新增证书失败");
        }
    }

    @Override
    @Transactional
    public BaseResult update(CertifyConfigDto certifyConfigDto) {
        if(certifyConfigDto==null){
            return new BaseResult(BaseConstant.FAILED, "参数为空");
        }
        CertifyConfig certifyConfig = new CertifyConfig();
        BeanUtils.copyProperties(certifyConfigDto, certifyConfig);
        if(certifyConfigMapper.update(certifyConfig)>0) {
            return new BaseResult();
        } else {
            return new BaseResult(BaseConstant.FAILED, "更新证书失败");
        }
    }

    @Override
    @Transactional
    public BaseResult delete(String certifyId) {
        if(StringUtils.isBlank(certifyId)){
            return new BaseResult(BaseConstant.FAILED, "参数为空");
        }
        if(certifyConfigMapper.deleteByPrimaryKey(certifyId)>0) {
            return new BaseResult();
        } else {
            return new BaseResult(BaseConstant.FAILED, "删除证书失败");
        }
    }

    @Override
    public BaseResult<CertifyConfigDto> queryById(String certifyId) {
        if(StringUtils.isBlank(certifyId)){
            return new BaseResult(BaseConstant.FAILED, "参数为空");
        }
        CertifyConfig certifyConfig = certifyConfigMapper.selectByPrimaryKey(certifyId);
        if(certifyConfig==null){
            return new BaseResult<>(BaseConstant.FAILED, "不存在配置");
        } else {
            CertifyConfigDto dto = new CertifyConfigDto();
            BeanUtils.copyProperties(certifyConfig, dto);
            return new BaseResult<>(dto);
        }
    }
}
