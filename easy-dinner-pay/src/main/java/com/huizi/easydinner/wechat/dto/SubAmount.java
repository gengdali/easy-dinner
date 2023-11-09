package com.huizi.easydinner.wechat.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wuzhenzhong
 */
@Data
public class SubAmount {

    @ApiModelProperty(value = "标价金额")
    private Integer total_amount;
    @ApiModelProperty(value = "标价币种")
    private String currency;

}
