package org.loxf.jyadmin.biz;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.client.dto.CustBpDetailDto;
import org.loxf.jyadmin.client.dto.OrderDto;
import org.loxf.jyadmin.client.service.CustBpDetailService;
import org.loxf.jyadmin.dal.dao.CustBpDetailMapper;
import org.loxf.jyadmin.dal.po.Cust;
import org.loxf.jyadmin.dal.po.CustBpDetail;
import org.loxf.jyadmin.dal.po.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("custBpDetailService")
public class CustBpDetailServiceImpl implements CustBpDetailService {
    @Autowired
    private CustBpDetailMapper custBpDetailMapper;

    @Override
    public BaseResult<Boolean> insert(CustBpDetailDto custBpDetailDto) {
        if(custBpDetailDto==null){
            return new BaseResult<>(BaseConstant.FAILED, "参数为空");
        }
        CustBpDetail custBpDetail = new CustBpDetail();
        BeanUtils.copyProperties(custBpDetailDto, custBpDetail);
        return new BaseResult<>(custBpDetailMapper.insert(custBpDetail)>0);
    }

    @Override
    public PageResult<CustBpDetailDto> pager(CustBpDetailDto custBpDetailDto) {
        if (custBpDetailDto == null) {
            return new PageResult(BaseConstant.FAILED, "参数为空");
        }
        CustBpDetail custBpDetail = new CustBpDetail();
        BeanUtils.copyProperties(custBpDetailDto, custBpDetail);

        int count = custBpDetailMapper.count(custBpDetail);
        List<CustBpDetailDto> result = null;
        if (count > 0) {
            List<CustBpDetail> list = custBpDetailMapper.list(custBpDetail);
            if (CollectionUtils.isNotEmpty(list)) {
                result = new ArrayList<>();
                for (CustBpDetail tmp : list) {
                    CustBpDetailDto dto = new CustBpDetailDto();
                    BeanUtils.copyProperties(tmp, dto);
                    result.add(dto);
                }
            }
        }
        int page = count / custBpDetailDto.getPager().getSize() + count % custBpDetailDto.getPager().getSize() > 0 ? 1 : 0;
        return new PageResult<>(page, custBpDetailDto.getPager().getPage(), count, result);
    }
}
