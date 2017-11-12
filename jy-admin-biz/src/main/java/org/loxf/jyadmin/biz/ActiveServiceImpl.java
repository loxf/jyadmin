package org.loxf.jyadmin.biz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.util.IdGenerator;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.client.dto.ActiveDto;
import org.loxf.jyadmin.client.service.ActiveService;
import org.loxf.jyadmin.dal.dao.*;
import org.loxf.jyadmin.dal.po.Active;
import org.loxf.jyadmin.dal.po.City;
import org.loxf.jyadmin.dal.po.Province;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("activeService")
public class ActiveServiceImpl implements ActiveService {
    private static String prefix = "ACT";
    @Autowired
    private ActiveMapper activeMapper;
    @Autowired
    private ActiveCustListMapper activeCustListMapper;
    @Autowired
    private ProvinceMapper provinceMapper;
    @Autowired
    private CityMapper cityMapper;
    @Autowired
    private IndexRecommendMapper indexRecommendMapper;
    
    @Override
    public PageResult<ActiveDto> pager(ActiveDto activeDto) {
        if(activeDto==null){
            return new PageResult<>(BaseConstant.FAILED, "入参不能为空");
        }
        Active active = new Active();
        BeanUtils.copyProperties(activeDto, active);
        int total = activeMapper.count(active);
        List<ActiveDto> dtos = new ArrayList<>();
        if(total>0) {
            List<Active> custList = activeMapper.pager(active);
            for(Active po : custList){
                ActiveDto tmp = new ActiveDto();
                BeanUtils.copyProperties(po, tmp);
                // 查询当前活动的参与人数
                int studentsNbr = activeCustListMapper.countByActiveId(tmp.getActiveId());
                tmp.setStudentsNbr(studentsNbr);
                //设置活动状态 0:进行中 1:即将开始 2:报名中 3:已经结束
                if(tmp.getActiveStartTime()!=null) {
                    long diff = tmp.getActiveStartTime().getTime() - System.currentTimeMillis();
                    Province p = provinceMapper.selectProvince(tmp.getProvince());
                    if (p != null) {
                        tmp.setProvince(p.getProvince());
                    }
                    City c = cityMapper.selectCity(tmp.getCity());
                    if (c != null) {
                        tmp.setCity(c.getCity());
                    }
                    if (diff >= 48 * 60 * 1000 * 1000) {
                        // 报名中 2天以上的
                        tmp.setActiveStatus(2);
                    } else if (diff >= 0 && diff < 48 * 60 * 1000 * 1000) {
                        // 1:即将开始
                        tmp.setActiveStatus(1);
                    } else if (tmp.getActiveEndTime()!=null && diff < 0 && tmp.getActiveEndTime().getTime() - System.currentTimeMillis() >= 0) {
                        // 进行中
                        tmp.setActiveStatus(0);
                    } else {
                        // 已结束
                        tmp.setActiveStatus(3);
                    }
                }
                dtos.add(tmp);
            }
        }
        int tatalPage = total/activeDto.getPager().getSize() + (total%activeDto.getPager().getSize()==0?0:1);
        return new PageResult<ActiveDto>(tatalPage, activeDto.getPager().getPage(), total, dtos);
    }

    @Override
    public ActiveDto queryActive(String activeId) {
        Active active = activeMapper.selectByActiveId(activeId);
        ActiveDto dto = new ActiveDto();
        BeanUtils.copyProperties(active, dto);
        return dto;
    }

    @Override
    @Transactional
    public BaseResult newActive(ActiveDto activeDto) {
        Active active = new Active();
        BeanUtils.copyProperties(activeDto, active);
        String activeId = IdGenerator.generate(prefix);
        active.setActiveId(activeId);
        activeMapper.insert(active);
        return new BaseResult();
    }

    @Override
    @Transactional
    public BaseResult updateActive(ActiveDto activeDto){
        Active againPO = activeMapper.selectByActiveId(activeDto.getActiveId());
        if(againPO.getStatus()==1){
            return new BaseResult(BaseConstant.FAILED, "活动已发布，不能修改");
        }
        Active active = new Active();
        BeanUtils.copyProperties(activeDto, active);
        activeMapper.updateByActiveId(active);
        return new BaseResult();
    }

    @Override
    @Transactional
    public BaseResult deleteActive(String activeId){
        Active active = activeMapper.selectByActiveId(activeId);
        if(active==null){
            return new BaseResult(BaseConstant.FAILED, "活动不存在");
        }
        if(active.getStatus()==1){
            // 查询当前活动的参与人数
            int studentsNbr = activeCustListMapper.countByActiveId(activeId);
            if(studentsNbr>0){
                return new BaseResult(BaseConstant.FAILED, "活动已发布且有同学参加，不能删除");
            }
        }
        return new BaseResult(activeMapper.deleteByActiveId(activeId));
    }

    @Override
    @Transactional
    public BaseResult onOrOffActive(String activeId, Integer status){
        if(status==0){
            // 查询当前活动的参与人数
            int studentsNbr = activeCustListMapper.countByActiveId(activeId);
            if(studentsNbr>0) {
                return new BaseResult(BaseConstant.FAILED, "活动已有同学参加,不能取消");
            }
        }
        return new BaseResult(activeMapper.onOrOffActive(activeId, status));
    }

    @Override
    @Transactional
    public BaseResult sendIndexRecommend(String activeId){
        Active active = activeMapper.selectByActiveId(activeId);
        if(active==null){
            return new BaseResult(BaseConstant.FAILED, "活动不存在");
        }
        String metaData = active.getMetaData();
        JSONObject metaJSON = null;
        if(StringUtils.isBlank(metaData)){
            metaJSON = new JSONObject();
        } else {
            metaJSON = JSON.parseObject(metaData);
        }
        if(metaJSON.containsKey("INDEX")){
            indexRecommendMapper.updateByPrimaryKey("ACTIVE", activeId);
        } else {
            indexRecommendMapper.insert("ACTIVE", activeId);
            metaJSON.put("INDEX", "on");
            Active activeRefresh = new Active();
            activeRefresh.setActiveId(activeId);
            activeRefresh.setMetaData(metaJSON.toJSONString());
            activeMapper.updateByActiveId(activeRefresh);
        }
        return new BaseResult();
    }
}
