package com.huizi.easydinner.wechat.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author wuzhenzhong
 * 合单body
 */
@Data
public class JoinBodyText {
    @ApiModelProperty(value = "合单商户appid")
    private String combine_appid;
    @ApiModelProperty(value = "合单商户号")
    private String combine_mchid;
    @ApiModelProperty(value = "合单商户订单号")
    private String combine_out_trade_no;
    @ApiModelProperty(value = "通知地址")
    private String notify_url;
    @ApiModelProperty(value = "支付者")
    private Payer combine_payer_info;
    @ApiModelProperty(value = "子单信息")
    private List<SubOrders> sub_orders;


}
