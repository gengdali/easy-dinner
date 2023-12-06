package com.huizi.easydinner.controller;


import com.huizi.easydinner.api.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
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
@Api(tags = "账号管理")
@RestController
@RequestMapping("/crawler")
public class crawlerController {

    @ApiOperation(value = "爬取数据")
    @PostMapping(value = "/crawldata")
    public CommonResult crawldata() {
        

        return CommonResult.success("爬取成功");
    }

}
