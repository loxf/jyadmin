package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.VideoConfig;

public interface VideoConfigMapper {
    int deleteByPrimaryKey(Long id);

    int insert(VideoConfig record);

    int insertSelective(VideoConfig record);

    VideoConfig selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(VideoConfig record);

    int updateByPrimaryKey(VideoConfig record);
}