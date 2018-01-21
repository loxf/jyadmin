package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.CertifyConfig;

public interface CertifyConfigMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CertifyConfig record);

    int insertSelective(CertifyConfig record);

    CertifyConfig selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CertifyConfig record);

    int updateByPrimaryKey(CertifyConfig record);
}