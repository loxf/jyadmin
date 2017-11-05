package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.client.dto.AdminDto;

public interface AdminService {
    BaseResult<AdminDto> login(String username, String password);
    BaseResult modifyPassword(String username, String password, String oldPassword);
}
