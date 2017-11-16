package org.loxf.jyadmin.biz;

import org.apache.commons.collections.CollectionUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.client.dto.FriendLinkDto;
import org.loxf.jyadmin.client.service.FriendLinkService;
import org.loxf.jyadmin.dal.dao.FriendLinkMapper;
import org.loxf.jyadmin.dal.po.FriendLink;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("friendLinkService")
public class FriendLinkServiceImpl implements FriendLinkService {
    @Autowired
    private FriendLinkMapper friendLinkMapper;

    @Override
    public BaseResult addLink(FriendLinkDto dto) {
        if(dto==null){
            return new BaseResult(BaseConstant.FAILED, "参数不能为空");
        }
        FriendLink friendLink = new FriendLink();
        BeanUtils.copyProperties(dto, friendLink);
        if(friendLink.getOrder()==null){
            friendLink.setOrder(50);
        }
        friendLinkMapper.insert(friendLink);
        return new BaseResult();
    }

    @Override
    public BaseResult updateLink(FriendLinkDto dto) {
        if(dto==null){
            return new BaseResult(BaseConstant.FAILED, "参数不能为空");
        }
        FriendLink friendLink = new FriendLink();
        BeanUtils.copyProperties(dto, friendLink);
        friendLinkMapper.updateByPrimaryKey(friendLink);
        return new BaseResult();
    }

    @Override
    public BaseResult rmLink(Long id) {
        friendLinkMapper.deleteByPrimaryKey(id);
        return new BaseResult();
    }

    @Override
    public BaseResult<FriendLinkDto> queryLink(Long id) {
        FriendLink friendLink = friendLinkMapper.selectByPrimaryKey(id);
        if(friendLink==null){
            return new BaseResult<>(BaseConstant.FAILED, "链接不存在");
        }
        FriendLinkDto dto = new FriendLinkDto();
        BeanUtils.copyProperties(friendLink, dto);
        return new BaseResult(dto);
    }

    @Override
    public PageResult<FriendLinkDto> queryAllLink(FriendLinkDto dto, Integer page, Integer size) {
        if(page==null || page<=0){
            page = 1;
        }
        if(size==null){
            size = 30;
        }
        FriendLink friendLink = new FriendLink();
        BeanUtils.copyProperties(dto, friendLink);
        Integer count = friendLinkMapper.count(friendLink);
        List<FriendLinkDto> friendLinkDtos = new ArrayList<>();
        if(count!=null && count>0){
            List<FriendLink> list = friendLinkMapper.list(friendLink, (page-1)*size, size);
            if(CollectionUtils.isNotEmpty(list)) {
                for (FriendLink po : list) {
                    FriendLinkDto dto1 = new FriendLinkDto();
                    BeanUtils.copyProperties(po, dto1);
                    friendLinkDtos.add(dto1);
                }
            }
        }
        int totalPage = count/size + (count%size==0?0:1);
        return new PageResult<>(totalPage, page, count, friendLinkDtos);
    }
}
