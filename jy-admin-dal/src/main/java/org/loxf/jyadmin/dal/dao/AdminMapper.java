package org.loxf.jyadmin.dal.dao;

import org.apache.ibatis.annotations.Param;
import org.loxf.jyadmin.dal.po.Admin;

public interface AdminMapper {
    Admin login(@Param("username") String username, @Param("password")String password);

    int modifyPassword(@Param("username")String username, @Param("password")String password);
}