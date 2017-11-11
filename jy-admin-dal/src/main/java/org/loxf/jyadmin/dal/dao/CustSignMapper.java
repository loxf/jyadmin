package org.loxf.jyadmin.dal.dao;

import org.apache.ibatis.annotations.Param;

public interface CustSignMapper {
    int insert(@Param("custId") String custId, @Param("signDate") String signDate);

    int selectByCustAndSignDate(@Param("custId") String custId, @Param("signDate") String signDate);

}