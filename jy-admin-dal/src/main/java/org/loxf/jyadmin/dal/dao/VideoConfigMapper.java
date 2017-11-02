package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.VideoConfig;

import java.util.List;

public interface VideoConfigMapper {
    int deleteByPrimaryKey(String id);

    int insert(VideoConfig record);

    VideoConfig selectByPrimaryKey(String id);

    int updateByPrimaryKey(VideoConfig record);

    int count(VideoConfig record);

    List<VideoConfig> list(VideoConfig record);

    int updateProgress(VideoConfig record);
}