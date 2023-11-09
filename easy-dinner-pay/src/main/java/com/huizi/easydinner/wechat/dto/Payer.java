package com.huizi.easydinner.wechat.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author
 */
@Data
public class Payer {

    @ApiModelProperty(value = "用户标识")
    private String openid;

}
