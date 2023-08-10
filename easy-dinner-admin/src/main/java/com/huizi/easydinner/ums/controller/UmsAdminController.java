package com.huizi.easydinner.ums.controller;


import com.huizi.easydinner.ums.entity.UmsAdmin;
import com.huizi.easydinner.ums.service.UmsAdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 账号表 前端控制器
 * </p>
 *
 * @author gw
 * @since 2023-05-19
 */
@Api(tags = "账号管理")
@RestController
@RequestMapping("/admin")
public class UmsAdminController {
    
    @Autowired
    private UmsAdminService umsAdminService;

    @ApiOperation(value = "用户列表")
    @PostMapping("/adminList")
    @ResponseBody
    public List<UmsAdmin> getUserInfoList() {
        List<UmsAdmin> list = umsAdminService.list();
        return list;

    }


}
