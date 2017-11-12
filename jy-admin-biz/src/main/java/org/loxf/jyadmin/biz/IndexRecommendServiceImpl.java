package org.loxf.jyadmin.biz;

import org.apache.commons.collections.CollectionUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.client.dto.IndexRecommendDto;
import org.loxf.jyadmin.client.service.IndexRecommendService;
import org.loxf.jyadmin.dal.dao.IndexRecommendMapper;
import org.loxf.jyadmin.dal.po.IndexRecommend;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("indexRecommendService")
public class IndexRecommendServiceImpl implements IndexRecommendService {
    @Autowired
    private IndexRecommendMapper indexRecommendMapper;

    @Override
    public BaseResult<List<IndexRecommendDto>> selectShow() {
        List<IndexRecommend> list = indexRecommendMapper.selectShow();
        if(CollectionUtils.isNotEmpty(list)){
            List<IndexRecommendDto> result = new ArrayList<>();
            for(IndexRecommend indexRecommend : list){
                IndexRecommendDto dto = new IndexRecommendDto();
                BeanUtils.copyProperties(indexRecommend, dto);
                result.add(dto);
            }
            return new BaseResult<>(result);
        }
        return new BaseResult<>(BaseConstant.FAILED, "没有数据");
    }

    @Override
    public BaseResult<Boolean> insert(String type, String objId) {
        return new BaseResult<>(indexRecommendMapper.insert(type, objId)>0);
    }

    @Override
    public BaseResult<Boolean> update(String type, String objId) {
        return new BaseResult<>(indexRecommendMapper.updateByPrimaryKey(type, objId)>0);
    }
}
