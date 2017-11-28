package org.loxf.jyadmin.biz;

import org.apache.commons.collections.CollectionUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.biz.util.RandomUtils;
import org.loxf.jyadmin.client.dto.ActiveCustListDto;
import org.loxf.jyadmin.client.service.ActiveCustListService;
import org.loxf.jyadmin.dal.dao.ActiveCustListMapper;
import org.loxf.jyadmin.dal.po.ActiveCustList;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("activeCustListService")
public class ActiveCustListServiceImpl implements ActiveCustListService {
    @Autowired
    private ActiveCustListMapper activeCustListMapper;

    @Override
    public BaseResult<List<ActiveCustListDto>> queryList(String activeId) {
        List<ActiveCustList> list = activeCustListMapper.queryList(activeId);
        if (CollectionUtils.isNotEmpty(list)) {
            List<ActiveCustListDto> result = new ArrayList<>();
            for (ActiveCustList cust : list) {
                ActiveCustListDto dto = new ActiveCustListDto();
                BeanUtils.copyProperties(cust, dto);
                result.add(dto);
            }
            return new BaseResult<>(result);
        }
        return new BaseResult<>();
    }

    @Override
    @Transactional
    public BaseResult addCustByActive(ActiveCustListDto activeCustListDto) {
        ActiveCustList tmp = new ActiveCustList();
        BeanUtils.copyProperties(activeCustListDto, tmp);
        // 生成票号
        String ticketNo = System.currentTimeMillis() + RandomUtils.getRandomStr(3);
        tmp.setActiveTicketNo(ticketNo);
        if (activeCustListMapper.insert(tmp) > 0) {
            return new BaseResult();
        } else {
            return new BaseResult(BaseConstant.FAILED, "新增活动参与人失败");
        }
    }
}
