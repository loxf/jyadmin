package org.loxf.jyadmin.web.admin;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.util.JedisUtil;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.client.dto.AdminDto;
import org.loxf.jyadmin.client.service.AdminService;
import org.loxf.jyadmin.util.CookieUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class AdminController {
    private static Logger logger = LoggerFactory.getLogger(AdminController.class);
    @Autowired
    private AdminService adminService;
    @Autowired
    private JedisUtil jedisUtil;

    @RequestMapping("/admin/login")
    @ResponseBody
    public BaseResult login(HttpServletRequest request, HttpServletResponse response, String userName, String password){
        BaseResult<AdminDto> baseResult = adminService.login(userName, password);
        if(baseResult.getCode()== BaseConstant.SUCCESS){
            // 生成TOKEN
            String tmp = CookieUtil.TOKEN_PREFIX + CookieUtil.TOKEN_SPLIT + baseResult.getData().getUserName() + CookieUtil.TOKEN_SPLIT + System.currentTimeMillis();
            try {
                String token = CookieUtil.encrypt(tmp);
                // 设置TOKEN到session
                CookieUtil.setSession(request, BaseConstant.ADMIN_COOKIE_NAME, token);
                // 设置TOKEN到cookie
                CookieUtil.setCookie(response, BaseConstant.ADMIN_COOKIE_NAME, token);
                // 设置用户信息到redis
                jedisUtil.set(token, JSON.toJSONString(baseResult.getData()), 24*60*60);
                return new BaseResult(request.getContextPath() + "/admin/index.html");
            } catch (Exception e){
                logger.error("生成TOKEN失败", e);
                return new BaseResult(BaseConstant.FAILED, "生成TOKEN失败");
            }
        } else {
            return baseResult;
        }
    }

    @RequestMapping("/admin/toModifyPassword")
    public String toModifyPassword(){
        return "main/admin/modifyPasswd";
    }

    @RequestMapping("/admin/modifyPassword")
    @ResponseBody
    public BaseResult modifyPassword(HttpServletRequest request, String oldPassword, String password){
        AdminDto admin = CookieUtil.getAdmin(request);
        if(admin==null){
            return new BaseResult(BaseConstant.NOT_LOGIN, "用户未登录");
        }
        return adminService.modifyPassword(admin.getUserName(), password, oldPassword);
    }

    @RequestMapping("/admin/logout")
    @ResponseBody
    public BaseResult logout(HttpServletRequest request, HttpServletResponse response, String oldPassword, String password){
        String token = CookieUtil.getAdminToken(request);
        if(StringUtils.isBlank(token)){
            return new BaseResult(BaseConstant.FAILED, "TOKEN不存在");
        }
        // 删除session
        request.getSession().removeAttribute(BaseConstant.ADMIN_COOKIE_NAME);
        // 删除cookie
        CookieUtil.rmCookie(request, response, BaseConstant.ADMIN_COOKIE_NAME);
        // 删除redis
        jedisUtil.del(token);
        return new BaseResult();
    }
}
