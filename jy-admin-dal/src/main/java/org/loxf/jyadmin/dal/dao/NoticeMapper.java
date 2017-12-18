package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.Notice;

import java.util.List;

public interface NoticeMapper {

    int insert(Notice record);

    List<Notice> queryNeedSend();

    int updateByPrimaryKey(Notice record);
}