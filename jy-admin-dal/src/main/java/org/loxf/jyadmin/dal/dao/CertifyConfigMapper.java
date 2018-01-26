package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.CertifyConfig;

import java.util.List;

public interface CertifyConfigMapper {
    int selectCount(CertifyConfig certifyConfig);

    List<CertifyConfig> selectList(CertifyConfig certifyConfig);

    int deleteByPrimaryKey(String id);

    int insert(CertifyConfig record);

    CertifyConfig selectByPrimaryKey(String id);

    int update(CertifyConfig record);

    List<CertifyConfig> selectCertifyByOfferId(String offerId);
}