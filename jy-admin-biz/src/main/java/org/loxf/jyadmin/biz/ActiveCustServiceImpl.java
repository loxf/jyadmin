package org.loxf.jyadmin.biz;

import org.apache.commons.collections.CollectionUtils;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.client.dto.ActiveCustListDto;
import org.loxf.jyadmin.client.service.ActiveCustService;
import org.loxf.jyadmin.dal.dao.ActiveCustListMapper;
import org.loxf.jyadmin.dal.dao.CityMapper;
import org.loxf.jyadmin.dal.dao.ProvinceMapper;
import org.loxf.jyadmin.dal.po.ActiveCustList;
import org.loxf.jyadmin.dal.po.City;
import org.loxf.jyadmin.dal.po.Province;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("activeCustService")
public class ActiveCustServiceImpl implements ActiveCustService {
    @Autowired
    private ActiveCustListMapper activeCustListMapper;
    @Autowired
    private ProvinceMapper provinceMapper;
    @Autowired
    private CityMapper cityMapper;

    @Override
    public PageResult<ActiveCustListDto> pager(String custId, int page, int size) {
        if(custId==null){
            return new PageResult<>(BaseConstant.FAILED, "入参不能为空");
        }
        int total = activeCustListMapper.countByCustId(custId);
        List<ActiveCustListDto> dtos = new ArrayList<>();
        if(total>0) {
            int start = (page-1)*size;
            List<ActiveCustList> activeList = activeCustListMapper.queryListByCustId(custId, start, size);
            if(CollectionUtils.isNotEmpty(activeList)) {
                for (ActiveCustList po : activeList) {
                    ActiveCustListDto tmp = new ActiveCustListDto();
                    BeanUtils.copyProperties(po, tmp);
                    Province p = provinceMapper.selectProvince(tmp.getProvince());
                    if (p != null) {
                        tmp.setProvince(p.getProvince());
                    }
                    City c = cityMapper.selectCity(tmp.getCity());
                    if (c != null) {
                        tmp.setCity(c.getCity());
                    }
                    tmp.setActiveStatus();
                    dtos.add(tmp);
                }
            }
        }
        int totalPage = total/size + (total%size==0?0:1);
        return new PageResult<ActiveCustListDto>(totalPage, page, total, dtos);
    }
}
