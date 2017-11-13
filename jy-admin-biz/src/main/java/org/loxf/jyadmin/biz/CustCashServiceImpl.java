package org.loxf.jyadmin.biz;

import org.apache.commons.collections.CollectionUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.biz.util.ConfigUtil;
import org.loxf.jyadmin.client.dto.ConfigDto;
import org.loxf.jyadmin.client.dto.CustCashDto;
import org.loxf.jyadmin.client.service.AccountService;
import org.loxf.jyadmin.client.service.CustCashService;
import org.loxf.jyadmin.dal.dao.CustCashMapper;
import org.loxf.jyadmin.dal.po.CustCash;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("custCashService")
public class CustCashServiceImpl implements CustCashService {
    @Autowired
    private CustCashMapper custCashMapper;
    @Autowired
    private AccountService accountService;

    @Override
    public PageResult<CustCashDto> queryCustCash(CustCashDto custCashDto) {
        if(custCashDto==null) {
            return new PageResult<>(BaseConstant.FAILED, "参数为空");
        }
        CustCash custCash = new CustCash();
        BeanUtils.copyProperties(custCashDto, custCash);
        int total = custCashMapper.count(custCash);
        List<CustCashDto> result = new ArrayList<>();
        if(total>0){
            List<CustCash> list = custCashMapper.pager(custCash);
            if(CollectionUtils.isNotEmpty(list)) {
                for (CustCash tmp : list) {
                    CustCashDto dto = new CustCashDto();
                    BeanUtils.copyProperties(tmp, dto);
                    result.add(dto);
                }
            }
        }
        int totalPage = total/custCashDto.getPager().getSize() + (total%custCashDto.getPager().getSize()==0?0:1);
        return new PageResult(totalPage, custCashDto.getPager().getPage(), total, result);
    }

    @Override
    @Transactional
    public BaseResult<Boolean> addCustCashRecord(CustCashDto custCashDto, String password) {
        if(custCashDto==null){
            return new BaseResult<>(BaseConstant.FAILED, "参数为空");
        }
        ConfigDto configDto = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_PAY, "CASH_MIN_NBR");
        int cash_min_nbr = 10;//最低提现10元
        if(configDto!=null){
            cash_min_nbr = Integer.valueOf(configDto.getConfigValue());
        }
        if(custCashDto.getBalance().intValue()<cash_min_nbr){
            return new BaseResult<>(BaseConstant.FAILED, "提现金额不能低于" + cash_min_nbr + "元");
        }
        // 扣减余额
        BaseResult<Boolean> reduce = accountService.reduce(custCashDto.getCustId(),
                password, custCashDto.getBalance(), null, "提现");

        if(reduce.getCode()==BaseConstant.FAILED || !reduce.getData()){
            return new BaseResult<>(BaseConstant.FAILED, reduce.getMsg());
        }
        // 插入提现记录
        CustCash custCash = new CustCash();
        BeanUtils.copyProperties(custCashDto, custCash);
        return new BaseResult<>(custCashMapper.insert(custCash)>0);
    }

    @Override
    @Transactional
    public BaseResult<Boolean> pendingCustCashRecord(Long recordId, Integer status, String remark) {
        // 状态：1：未打款 3：已打款 -3:拒绝打款
        if(recordId==null || status==null){
            return new BaseResult<>(BaseConstant.FAILED, "参数为空");
        }
        if(status!=3 && status!=-3){
            return new BaseResult<>(BaseConstant.FAILED, "审核状态不正确");
        }
        CustCash custCash = custCashMapper.selectById(recordId);
        if(custCash==null){
            return new BaseResult<>(BaseConstant.FAILED, "提现记录不存在");
        }
        if(custCash.getStatus()==3||custCash.getStatus()==-3){
            return new BaseResult<>(BaseConstant.FAILED, "当前记录已处理");
        }
        // 如果审核未通过，补余额
        if(status==-3) {
            BaseResult<Boolean> increase = accountService.increase(custCash.getCustId(),
                    custCash.getBalance(), null, "拒绝提现退款");
            if(increase.getCode()==BaseConstant.FAILED || !increase.getData()){
                return new BaseResult<>(BaseConstant.FAILED, increase.getMsg());
            }
        }
        return new BaseResult<>(custCashMapper.pendingCustCash(recordId, status, remark)>0);
    }
}
