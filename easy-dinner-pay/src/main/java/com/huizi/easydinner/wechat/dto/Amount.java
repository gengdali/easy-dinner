package com.huizi.easydinner.wechat.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author
 */
@Data
public class Amount {

    @ApiModelProperty(value = "总金额")
    private Integer total;

    @ApiModelProperty(value = "货币类型")
    private String currency;
}
