package org.loxf.jyadmin.biz;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.client.service.CustSignService;
import org.loxf.jyadmin.dal.dao.CustSignMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("custSignService")
public class CustSignServiceImpl implements CustSignService {
    @Autowired
    private CustSignMapper custSignMapper;

    @Override
    public BaseResult<Boolean> sign(String custId, String signDate) {
        if(custSignMapper.selectByCustAndSignDate(custId, signDate)>0){
            return new BaseResult<>(true);
        }
        return new BaseResult<>(custSignMapper.insert(custId, signDate)>0);
    }

    @Override
    public BaseResult<Boolean> hasSign(String custId, String signDate) {
        return new BaseResult<>(custSignMapper.selectByCustAndSignDate(custId, signDate)>0);
    }
}
