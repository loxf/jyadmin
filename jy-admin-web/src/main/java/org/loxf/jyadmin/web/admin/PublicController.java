package org.loxf.jyadmin.web.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/public")
public class PublicController {
    @RequestMapping("/binduserNoticePage")
    public String toBindUserPage(){
        return "publicPage/binduser/binduser";
    }
}
