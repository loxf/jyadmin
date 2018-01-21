package org.loxf.jyadmin.biz;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.util.IdGenerator;
import org.loxf.jyadmin.client.dto.ClassQuestionDto;
import org.loxf.jyadmin.client.service.ClassQuestionService;
import org.loxf.jyadmin.dal.dao.ClassQuestionMapper;
import org.loxf.jyadmin.dal.po.ClassQuestion;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("classQuestionService")
public class ClassQuestionServiceImpl implements ClassQuestionService {
    @Autowired
    ClassQuestionMapper classQuestionMapper;
    @Override
    public BaseResult settingQuestion(List<ClassQuestionDto> classQuestionDtoList) {
        for(int seq=1; seq<=classQuestionDtoList.size(); seq++){
            ClassQuestionDto dto = classQuestionDtoList.get(seq-1);
            ClassQuestion classQuestion = new ClassQuestion();
            BeanUtils.copyProperties(dto, classQuestion);
            classQuestion.setSeq(seq);
            if(StringUtils.isBlank(dto.getQuestionId())){
                classQuestion.setQuestionId(IdGenerator.generate("QT"));
                // 新增的考题
                classQuestionMapper.insert(classQuestion);
            } else {
                classQuestionMapper.update(classQuestion);
            }
        }
        return new BaseResult();
    }

    @Override
    public BaseResult<List<ClassQuestionDto>> queryQuestions(String offerId) {
        List<ClassQuestion> classQuestions = classQuestionMapper.selectListByOfferId(offerId);
        if(CollectionUtils.isNotEmpty(classQuestions)){
            List<ClassQuestionDto> classQuestionDtos = new ArrayList<>();
            for(ClassQuestion classQuestion : classQuestions){
                ClassQuestionDto dto = new ClassQuestionDto();
                BeanUtils.copyProperties(classQuestion, dto);
                classQuestionDtos.add(dto);
            }
            return new BaseResult(classQuestionDtos);
        }
        return new BaseResult(BaseConstant.FAILED, "未设置考题");
    }
}
