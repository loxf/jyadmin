package org.loxf.jyadmin.dal.dao;

import org.apache.ibatis.annotations.Param;
import org.loxf.jyadmin.dal.po.FriendLink;

import java.util.List;

public interface FriendLinkMapper {
    int deleteByPrimaryKey(Long id);

    int insert(FriendLink record);

    FriendLink selectByPrimaryKey(Long id);

    int updateByPrimaryKey(FriendLink record);

    int count(@Param("link")FriendLink friendLink);

    List<FriendLink> list(@Param("link") FriendLink friendLink, @Param("start") Integer start, @Param("size")Integer size);

}