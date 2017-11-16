package org.loxf.jyadmin.web.admin;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.client.dto.FriendLinkDto;
import org.loxf.jyadmin.client.service.FriendLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin/friendLink")
public class FriendLinkController {
    @Autowired
    private FriendLinkService friendLinkService;

    @RequestMapping("/index")
    public String index(Model model){
        return "friendLink/friendLink_list";
    }

    @RequestMapping("/list")
    @ResponseBody
    public PageResult<FriendLinkDto> list(FriendLinkDto friendLinkDto, Integer page, Integer size){
        return friendLinkService.queryAllLink(friendLinkDto, page, size);
    }

    @RequestMapping("/toAddFriendLink")
    public String toAddFriendLink(Model model){
        return "main/friendLink/addLink";
    }

    @RequestMapping("/toEditFriendLink")
    public String toAddFriendLink(Model model, Long id){
        BaseResult<FriendLinkDto> baseResult = friendLinkService.queryLink(id);
        if(baseResult.getCode()== BaseConstant.FAILED || baseResult.getData()==null||baseResult.getData().getStatus()==1){
            model.addAttribute("errorMsg", "链接不存在或已发布");
            return "main/error";
        }
        FriendLinkDto dto = baseResult.getData();
        model.addAttribute("friendLink", dto);
        return "main/friendLink/editLink";
    }

    @RequestMapping("/addFriendLink")
    @ResponseBody
    public BaseResult addFriendLink(FriendLinkDto friendLinkDto){
        return friendLinkService.addLink(friendLinkDto);
    }

    @RequestMapping("/editFriendLink")
    @ResponseBody
    public BaseResult editFriendLink(FriendLinkDto friendLinkDto){
        return friendLinkService.updateLink(friendLinkDto);
    }

    @RequestMapping("/rmFriendLink")
    @ResponseBody
    public BaseResult rmFriendLink(Long id){
        return friendLinkService.rmLink(id);
    }

    @RequestMapping("/onOrOffFriendLink")
    @ResponseBody
    public BaseResult onOrOffFriendLink(Long id, Integer status){
        FriendLinkDto friendLinkDto = new FriendLinkDto();
        friendLinkDto.setStatus(status);
        friendLinkDto.setId(id);
        return friendLinkService.updateLink(friendLinkDto);
    }
}
