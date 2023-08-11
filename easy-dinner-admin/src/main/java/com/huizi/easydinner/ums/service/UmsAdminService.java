package com.huizi.easydinner.ums.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.huizi.easydinner.ums.entity.UmsAdmin;
import com.huizi.easydinner.ums.entity.UmsResource;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * <p>
 * 账号表 服务类
 * </p>
 *
 * @author gw
 * @since 2023-05-19
 */
public interface UmsAdminService extends IService<UmsAdmin> {

    /**
     * 根据用户名获取后台管理员
     */
    UmsAdmin getAdminByUsername(String username);

    /**
     * 获取用户信息
     */
    UserDetails loadUserByUsername(String username);

    /**
     * 获取指定用户的可访问资源
     */
    List<UmsResource> getResourceList(Long adminId);


}
