package com.huizi.easydinner.wechat.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @PROJECT_NAME: MeiShi_buyer_java
 * @DESCRIPTION:子单
 * @AUTHOR: 12615
 * @DATE: 2023/11/9 10:16
 */
@Data
public class SubOrders {

    @ApiModelProperty(value = "子单商户号")
    private String mchid;
    @ApiModelProperty(value = "附加数据")
    private String attach;
    @ApiModelProperty(value = "子单商户订单号")
    private String out_trade_no;
    @ApiModelProperty(value = "商品描述")
    private String description;
    @ApiModelProperty(value = "订单金额")
    private SubAmount amount;
}
