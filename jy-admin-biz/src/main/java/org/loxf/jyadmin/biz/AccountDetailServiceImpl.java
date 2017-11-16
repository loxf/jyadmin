package org.loxf.jyadmin.biz;

import org.apache.commons.collections.CollectionUtils;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.client.dto.AccountDetailDto;
import org.loxf.jyadmin.client.service.AccountDetailService;
import org.loxf.jyadmin.dal.dao.AccountDetailMapper;
import org.loxf.jyadmin.dal.po.AccountDetail;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("accountDetailService")
public class AccountDetailServiceImpl implements AccountDetailService {
    @Autowired
    private AccountDetailMapper accountDetailMapper;
    @Override
    public PageResult<AccountDetailDto> queryDetails(AccountDetailDto accountDetailDto) {
        if(accountDetailDto==null){
            return new PageResult<>(BaseConstant.FAILED, "参数不全");
        }
        AccountDetail accountDetail = new AccountDetail();
        BeanUtils.copyProperties(accountDetailDto, accountDetail);
        int total = accountDetailMapper.count(accountDetail);
        List<AccountDetailDto> accountDetailDtos = new ArrayList<>();
        if(total>0){
            List<AccountDetail> list = accountDetailMapper.pager(accountDetail);
            if(CollectionUtils.isNotEmpty(list)){
                for(AccountDetail accountDetail1 : list){
                    AccountDetailDto detailDto = new AccountDetailDto();
                    BeanUtils.copyProperties(accountDetail1, detailDto);
                    accountDetailDtos.add(detailDto);
                }
            }
        }
        int totalPage = total/accountDetailDto.getPager().getSize() + (total%accountDetailDto.getPager().getSize()==0?0:1);
        return new PageResult<>(totalPage, accountDetailDto.getPager().getPage(), total, accountDetailDtos);
    }
}
