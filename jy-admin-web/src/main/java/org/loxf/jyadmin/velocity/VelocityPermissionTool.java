package org.loxf.jyadmin.velocity;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class VelocityPermissionTool {

    /*public String getUserName() {
        UserDto user = LoginFilter.getUser(getRequest());
        if(user!=null)
            return user.getUserName();
        else
            return "未登录";
    }

    public boolean hasPermission(String permissionId) {
        return LoginFilter.hasPermission(getRequest(),permissionId);
    }

    public boolean equalPermission(String permissionId1,String permissionId2){
        return permissionId1.equalsIgnoreCase(permissionId2);
    }*/

    public HttpServletRequest getRequest(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return  request;
    }
}
