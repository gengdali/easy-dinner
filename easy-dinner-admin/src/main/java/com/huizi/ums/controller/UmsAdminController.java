package com.huizi.ums.controller;


import com.huizi.ums.service.UmsAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 账号表 前端控制器
 * </p>
 *
 * @author gw
 * @since 2023-05-19
 */
@RestController
@RequestMapping("/ums-admin")
public class UmsAdminController {
    
    @Autowired
    private UmsAdminService umsAdminService;


}
