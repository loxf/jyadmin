package org.loxf.jyadmin.biz;

import org.apache.commons.collections.CollectionUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.util.IdGenerator;
import org.loxf.jyadmin.client.dto.CustScoreDto;
import org.loxf.jyadmin.client.dto.CustScoreDto;
import org.loxf.jyadmin.client.service.CustScoreService;
import org.loxf.jyadmin.client.service.EventService;
import org.loxf.jyadmin.dal.dao.CustScoreMapper;
import org.loxf.jyadmin.dal.po.CustScore;
import org.loxf.jyadmin.dal.po.CustScore;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("custScoreService")
public class CustScoreServiceImpl implements CustScoreService {
    @Autowired
    private CustScoreMapper custScoreMapper;
    @Autowired
    private EventService eventService;

    @Override
    @Transactional
    public BaseResult addScore(CustScoreDto custScoreDto) {
        CustScore custScore = new CustScore();
        BeanUtils.copyProperties(custScoreDto, custScore);
        custScore.setScoreId(IdGenerator.generate("SCORE"));
        if(custScoreDto.getIsPass()==1){
            // 事件驱动 发送考试通过的事件
            BaseResult baseResult = eventService.addEvent("AddScore", custScore.getScoreId(), null);
            if(baseResult.getCode()==BaseConstant.FAILED){
                return baseResult;
            }
        }
        return new BaseResult(custScoreMapper.insert(custScore));
    }

    @Override
    public BaseResult<Boolean> allPass(List<String> offerList) {
        int passCount = custScoreMapper.selectPassCountByOfferList(offerList);
        if(passCount==offerList.size()) {
            return new BaseResult<>(true);
        } else if(passCount>offerList.size()) {
            return new BaseResult<>(BaseConstant.FAILED, "数据错误，通过数大于课程数");
        } else {
            return new BaseResult<>(false);
        }
    }

    @Override
    public PageResult<CustScoreDto> pager(CustScoreDto custScoreDto) {
        if(custScoreDto==null){
            return new PageResult<>(BaseConstant.FAILED, "入参不能为空");
        }
        CustScore custScore = new CustScore();
        BeanUtils.copyProperties(custScoreDto, custScore);
        int total = custScoreMapper.count(custScore);
        List<CustScoreDto> dtos = new ArrayList<>();
        if(total>0) {
            List<CustScore> custList = custScoreMapper.list(custScore);
            if(CollectionUtils.isNotEmpty(custList)) {
                for (CustScore po : custList) {
                    CustScoreDto tmp = new CustScoreDto();
                    BeanUtils.copyProperties(po, tmp);
                    dtos.add(tmp);
                }
            }
        }
        int totalPage = total/custScoreDto.getPager().getSize() + (total%custScoreDto.getPager().getSize()==0?0:1);
        return new PageResult<CustScoreDto>(totalPage, custScoreDto.getPager().getPage(), total, dtos);
    }

    @Override
    public BaseResult<String[]> getMinMaxScore(String offerId) {
        String minMax = custScoreMapper.getMinMaxScore(offerId);
        return new BaseResult(minMax.split(","));
    }

    @Override
    public BaseResult selectByScoreId(String scoreId) {
        CustScore custScore = custScoreMapper.selectByScoreId(scoreId);
        if(custScore==null){
            return new BaseResult(BaseConstant.FAILED, "不存在成绩");
        }
        CustScoreDto custScoreDto = new CustScoreDto();
        BeanUtils.copyProperties(custScore, custScoreDto);
        return new BaseResult(custScoreDto);
    }
}
