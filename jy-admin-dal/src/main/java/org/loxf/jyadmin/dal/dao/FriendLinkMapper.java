package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.FriendLink;

import java.util.List;

public interface FriendLinkMapper {
    int deleteByPrimaryKey(Long id);

    int insert(FriendLink record);

    FriendLink selectByPrimaryKey(Long id);

    int updateByPrimaryKey(FriendLink record);

    int count(FriendLink friendLink);

    List<FriendLink> list(FriendLink friendLink, Integer start, Integer size);

}