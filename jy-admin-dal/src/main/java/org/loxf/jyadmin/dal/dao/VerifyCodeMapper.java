package org.loxf.jyadmin.dal.dao;

import org.apache.ibatis.annotations.Param;
import org.loxf.jyadmin.dal.po.VerifyCode;

public interface VerifyCodeMapper {
    int selectCount(@Param("obj") String obj, @Param("sendType") Integer sendType);

    int insert(VerifyCode record);

}