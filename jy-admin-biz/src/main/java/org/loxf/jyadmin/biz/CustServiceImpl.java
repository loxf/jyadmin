package org.loxf.jyadmin.biz;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.client.constant.BaseConstant;
import org.loxf.jyadmin.client.dto.CustDto;
import org.loxf.jyadmin.client.service.CustService;
import org.loxf.jyadmin.dal.dao.CustMapper;
import org.loxf.jyadmin.dal.po.Cust;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("custService")
public class CustServiceImpl implements CustService {
    @Autowired
    private CustMapper custMapper;

    @Override
    public PageResult<CustDto> pager(CustDto custDto) {
        if(custDto==null){
            return new PageResult<>(BaseConstant.FAILED, "入参不能为空");
        }
        Cust cust = new Cust();
        BeanUtils.copyProperties(custDto, cust);
        int total = custMapper.count(cust);
        List<CustDto> dtos = new ArrayList<>();
        if(total>0) {
            List<Cust> custList = custMapper.pager(cust);
            for(Cust po : custList){
                CustDto tmp = new CustDto();
                BeanUtils.copyProperties(po, tmp);
                dtos.add(tmp);
            }
        }
        int tatalPage = total/custDto.getPager().getSize() + (total%custDto.getPager().getSize()==0?0:1);
        return new PageResult<CustDto>(tatalPage, custDto.getPager().getSize(), total, dtos);
    }


    @Override
    public BaseResult<CustDto> queryCust(int isChinese, String phoneOrEmail){
        Cust cust = custMapper.selectByPhoneOrEmail(isChinese, phoneOrEmail);
        if(cust==null){
            return new BaseResult<>(BaseConstant.FAILED, "会员不存在");
        }
        CustDto tmp = new CustDto();
        BeanUtils.copyProperties(cust, tmp);
        return new BaseResult<>(tmp);
    }


    @Override
    @Transactional
    public BaseResult updateCust(CustDto custDto){
        if(custDto==null || StringUtils.isBlank(custDto.getCustId())){
            return new BaseResult<>(BaseConstant.FAILED, "参数不全");
        }
        Cust tmp = new Cust();
        BeanUtils.copyProperties(custDto, tmp);
        custMapper.updateByCustId(tmp);
        return new BaseResult();
    }

    @Override
    public PageResult<CustDto> queryChildList(int type, String custId, int page, int size){
        List<String> recommends = new ArrayList<>();
        // 获取自己
        Cust cust = custMapper.selectByCustId(custId);
        if(cust.getIsChinese()==1) {
            recommends.add(cust.getPhone());
        } else {
            recommends.add(cust.getEmail());
        }
        if(type==2){
            // 获取所有的一级同学
            List<Cust> firstList = custMapper.queryChildList(recommends, 0, 10000);
            if(CollectionUtils.isEmpty(firstList)){
                return new PageResult(1, page, 0, null);
            } else {
                recommends.clear();
                for(Cust tmp : firstList){
                    if(tmp.getIsChinese()==1) {
                        recommends.add(tmp.getPhone());
                    } else {
                        recommends.add(tmp.getEmail());
                    }
                }
            }
        }
        int total = custMapper.queryChildListCount(recommends);
        List<CustDto> resultList = new ArrayList<>();
        if(total>0){
            if(page<=0){
                page = 1;
            }
            int start = (page-1)*size;
            List<Cust> list = custMapper.queryChildList(recommends, start, size);
            for(Cust tmp : list){
                CustDto dto = new CustDto();
                BeanUtils.copyProperties(tmp, dto);
                resultList.add(dto);
            }
        }
        int tatalPage = total/size + (total%size==0?0:1);
        return new PageResult<>(tatalPage, page, total, resultList);
    }

    @Override
    public BaseResult delCust(String custId) {
        return new BaseResult(custMapper.deleteCust(custId));
    }
}
