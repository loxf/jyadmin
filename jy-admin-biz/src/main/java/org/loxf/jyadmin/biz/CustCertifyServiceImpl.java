package org.loxf.jyadmin.biz;

import org.apache.commons.collections.CollectionUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.util.IdGenerator;
import org.loxf.jyadmin.client.dto.CustCertifyDto;
import org.loxf.jyadmin.client.dto.CustCertifyDto;
import org.loxf.jyadmin.client.service.CustCertifyService;
import org.loxf.jyadmin.dal.dao.CustCertifyMapper;
import org.loxf.jyadmin.dal.po.CustCertify;
import org.loxf.jyadmin.dal.po.CustCertify;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("custCertifyService")
public class CustCertifyServiceImpl implements CustCertifyService {
    @Autowired
    private CustCertifyMapper custCertifyMapper;

    @Override
    public PageResult<CustCertifyDto> pager(CustCertifyDto custScoreDto) {
        if(custScoreDto==null){
            return new PageResult<>(BaseConstant.FAILED, "入参不能为空");
        }
        CustCertify custCertify = new CustCertify();
        BeanUtils.copyProperties(custScoreDto, custCertify);
        int total = custCertifyMapper.count(custCertify);
        List<CustCertifyDto> dtos = new ArrayList<>();
        if(total>0) {
            List<CustCertify> custList = custCertifyMapper.list(custCertify);
            if(CollectionUtils.isNotEmpty(custList)) {
                for (CustCertify po : custList) {
                    CustCertifyDto tmp = new CustCertifyDto();
                    BeanUtils.copyProperties(po, tmp);
                    dtos.add(tmp);
                }
            }
        }
        int totalPage = total/custScoreDto.getPager().getSize() + (total%custScoreDto.getPager().getSize()==0?0:1);
        return new PageResult<CustCertifyDto>(totalPage, custScoreDto.getPager().getPage(), total, dtos);
    }

    @Override
    @Transactional
    public BaseResult addCertify(CustCertifyDto custScoreDto) {
        if(custScoreDto==null){
            return new BaseResult(BaseConstant.FAILED, "入参不能为空");
        }
        CustCertify custCertify = new CustCertify();
        BeanUtils.copyProperties(custScoreDto, custCertify);
        if(custCertifyMapper.insert(custCertify)>0)
            return new BaseResult();
        else
            return new BaseResult(BaseConstant.FAILED, "新增证书失败");
    }
}
