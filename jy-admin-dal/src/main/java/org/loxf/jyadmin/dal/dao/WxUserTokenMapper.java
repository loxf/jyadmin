package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.WxUserToken;

import java.util.List;

public interface WxUserTokenMapper {
    int deleteByPrimaryKey(String openid);

    int insert(WxUserToken record);

    WxUserToken selectByPrimaryKey(String openid);

    int updateByPrimaryKey(WxUserToken record);

    List<WxUserToken> queryNeedRefreshToken();
}