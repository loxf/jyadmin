package org.loxf.jyadmin.biz;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.client.dto.AdminDto;
import org.loxf.jyadmin.client.service.AdminService;
import org.loxf.jyadmin.dal.dao.AdminMapper;
import org.loxf.jyadmin.dal.po.Admin;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("")
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminMapper adminMapper;

    @Override
    public BaseResult<AdminDto> login(String username, String password) {
        Admin admin = adminMapper.login(username, password);
        if(admin!=null) {
            AdminDto dto = new AdminDto();
            BeanUtils.copyProperties(admin, dto);
            return new BaseResult(dto);
        } else {
            return new BaseResult<>(BaseConstant.FAILED, "管理员不存在或密码错误");
        }
    }

    @Override
    public BaseResult modifyPassword(String username, String password, String oldPassword) {
        Admin admin = adminMapper.login(username, oldPassword);
        if(admin!=null) {
            return new BaseResult(adminMapper.modifyPassword(username, password));
        } else {
            return new BaseResult<>(BaseConstant.FAILED, "管理员不存在或密码错误");
        }
    }
}
