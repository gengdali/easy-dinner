package com.huizi.easydinner.wechat.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author
 */
@Data
public class BodyText {

    @ApiModelProperty(value = "应用ID")
    private String appid;
    @ApiModelProperty(value = "直连商户号")
    private String mchid;
    @ApiModelProperty(value = "商户订单号")
    private String out_trade_no;
    @ApiModelProperty(value = "通知地址")
    private String notify_url;
    @ApiModelProperty(value = "商品描述")
    private String description;
    @ApiModelProperty(value = "订单金额")
    private Amount amount;
    @ApiModelProperty(value = "支付者")
    private Payer payer;

}
