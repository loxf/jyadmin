package org.loxf.jyadmin.web.admin;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.client.constant.BaseConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/admin/login")
public class LoginController {
    private static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping("/index")
    public String index(Model model){
        return "login";
    }

    @RequestMapping("/login")
    @ResponseBody
    public BaseResult login(String username, String password, HttpServletRequest request, HttpServletResponse response){
        if("admin".equals(username) && "admin".equals(password)){
            return new BaseResult(BaseConstant.SUCCESS, "登录成功", request.getContextPath() + "/admin/index.html");
        } else {
            return new BaseResult(BaseConstant.FAILED, "用户或密码错误");
        }
    }
}
