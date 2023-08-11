package com.huizi.easydinner.ums.service.impl;



import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huizi.easydinner.bo.AdminUserDetails;
import com.huizi.easydinner.ums.entity.UmsAdmin;
import com.huizi.easydinner.ums.entity.UmsResource;
import com.huizi.easydinner.ums.mapper.UmsAdminMapper;
import com.huizi.easydinner.ums.mapper.UmsMenuMapper;
import com.huizi.easydinner.ums.mapper.UmsResourceMapper;
import com.huizi.easydinner.ums.service.UmsAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 账号表 服务实现类
 * </p>
 *
 * @author gw
 * @since 2023-05-19
 */
@Service
public class UmsAdminServiceImpl extends ServiceImpl<UmsAdminMapper, UmsAdmin> implements UmsAdminService {

    @Autowired
    private UmsAdminMapper adminMapper;
    @Autowired

    private UmsMenuMapper umsMenuMapper;
    @Autowired
    private UmsResourceMapper resourceMapper;

    @Override
    public UserDetails loadUserByUsername(String username) {
        //获取用户信息
        UmsAdmin admin = getAdminByUsername(username);
        if (admin != null) {
            List<UmsResource> resourceList = getResourceList(admin.getId());
            return new AdminUserDetails(admin, resourceList);
        }
        throw new UsernameNotFoundException("用户名或密码错误");
    }

    @Override
    public UmsAdmin getAdminByUsername(String username) {
        UmsAdmin admin;
        QueryWrapper<UmsAdmin> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(UmsAdmin::getUsername, username);
        List<UmsAdmin> adminList = list(wrapper);
        if (adminList != null && adminList.size() > 0) {
            admin = adminList.get(0);
            return admin;
        }
        return null;
    }

    @Override
    public List<UmsResource> getResourceList(Long adminId) {
        List<UmsResource> resourceList = new ArrayList<>();
        if (CollUtil.isNotEmpty(resourceList)) {
            return resourceList;
        }
        return resourceList;
    }
}
