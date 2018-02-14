package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.ClassQuestion;

import java.util.List;

public interface ClassQuestionMapper {
    int deleteByPrimaryKey(String questionId);

    int insert(ClassQuestion record);

    int update(ClassQuestion record);

    List<ClassQuestion> selectListByOfferId(String offerId);
}