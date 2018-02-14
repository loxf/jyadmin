package org.loxf.jyadmin.client.service;

import com.alibaba.fastjson.JSONObject;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.client.dto.ClassQuestionDto;

import java.util.List;

public interface ClassQuestionService {
    /**
     * 设置考题
     * @param classQuestionDtoList
     * @return
     */
    BaseResult settingQuestion(List<ClassQuestionDto> classQuestionDtoList);

    /**
     * 获取考题
     * @param offerId
     * @return
     */
    BaseResult<List<ClassQuestionDto>> queryQuestions(String offerId);
}
