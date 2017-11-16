package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.client.dto.FriendLinkDto;

public interface FriendLinkService {
    BaseResult addLink(FriendLinkDto dto);
    BaseResult updateLink(FriendLinkDto dto);
    BaseResult rmLink(Long id);
    BaseResult<FriendLinkDto> queryLink(Long id);
    PageResult<FriendLinkDto> queryAllLink(FriendLinkDto dto, Integer page, Integer size);
}
