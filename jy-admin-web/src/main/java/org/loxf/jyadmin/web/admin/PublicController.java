package org.loxf.jyadmin.web.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/public")
public class PublicController {
    @RequestMapping("/binduserNoticePage")
    public String toBindUserPage(Model model){
        model.addAttribute("sysTitle", "静怡雅学文化");
        return "publicPage/binduser/binduser";
    }
    @RequestMapping("/player")
    public String toPlayVideo(Model model){
        model.addAttribute("sysTitle", "静怡雅学文化");
        return "publicPage/video/playVideo";
    }
}
