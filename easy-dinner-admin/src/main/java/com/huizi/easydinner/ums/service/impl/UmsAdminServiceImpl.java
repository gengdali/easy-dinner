package com.huizi.easydinner.ums.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huizi.easydinner.ums.entity.UmsAdmin;
import com.huizi.easydinner.ums.mapper.UmsAdminMapper;
import com.huizi.easydinner.ums.service.UmsAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private UmsAdminMapper mapper;

}