package com.huizi.easydinner.wechat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huizi.easydinner.api.CommonResult;
import com.huizi.easydinner.wechat.common.AppConstant;
import com.huizi.easydinner.wechat.common.AppLog;
import com.huizi.easydinner.wechat.dto.BodyText;
import com.huizi.easydinner.wechat.dto.CertificateDto;
import com.huizi.easydinner.wechat.dto.CertificatesDto;
import com.huizi.easydinner.wechat.util.AppUtil;
import com.huizi.easydinner.wechat.util.WeChatPayAesUtil;
import com.huizi.easydinner.wechat.util.WeChatPayUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import okhttp3.HttpUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @PROJECT_NAME: easy-dinner
 * @DESCRIPTION:小程序微信支付
 * @AUTHOR: 12615
 * @DATE: 2023/11/9 10:52
 */
@Api(tags = "小程序微信支付")
@RestController
@RequestMapping("/pay")
public class SmallProgramPay {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Resource
    private RestTemplate restTemplate;
    @Value("${wx.wechatpay.v3.certificates}")
    private String wxUrl;
    @Value("${wxMini.appId}")
    public String appid;
    @Value("${wxMini.mch_id}")
    public String mchid;
    @Value("${wxMini.base_notify_url}")
    public String notify_url;
    @Value("${wxMini.description}")
    public String description;
    private static final Map<String, Certificate> CERTIFICATE_MAP = new ConcurrentHashMap<>();


    @PostMapping("/Notification/Refund")
    @AppLog(operModule = "微信小程序支付 -- 退款结果通知API", operType = "R", operDesc = "微信支付退款状态改变后，微信会把相关退款结果发送给商户。")
    public Object getRefundNotifications(@RequestHeader MultiValueMap<String, String> reqHeader, @RequestBody Map<String, Object> reqBody) throws GeneralSecurityException, IOException {
        Map<String, Object> mapRtn = new HashMap<>();
        mapRtn.put("code", "FAIL");
        mapRtn.put("message", "失败");
        log.info("退款通知API Header:\n" + reqHeader);
        log.info("退款通知API Body:\n" + reqBody);
        // 1.签名值放在通知的HTTP头Wechatpay-Signature，验证签名以确认请求来自微信
        if (!WeChatPayUtil.checkWeChatSignature(reqHeader, reqBody, CERTIFICATE_MAP)) {
            throw new SignatureException("微信支付签名非法");
        }
        // 2.商户对resource对象进行解密后，得到的资源对象示例
        if (!"REFUND.SUCCESS".equals(String.valueOf(reqBody.get("event_type")))) {
            throw new RuntimeException("退款未成功：" + reqBody.get("summary"));
        }
        Map<String, String> mapRes = (Map<String, String>) reqBody.get("resource");
        WeChatPayAesUtil aesUtil = new WeChatPayAesUtil(AppConstant.WECHAT_PAY_API_V3_KEY.getBytes());
        String content = aesUtil.decryptToString(mapRes.get("associated_data").getBytes(), mapRes.get("nonce").getBytes(), mapRes.get("ciphertext"));
        log.info("退款通知API: " + content);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> resourceMap = objectMapper.readValue(content, Map.class);
        /**
         * 3.当商户系统收到通知进行处理时，先检查对应业务数据的状态，并判断该通知是否已经处理。
         * 如果未处理，则再进行处理；如果已处理，则直接返回结果成功。
         */

        // 4.通知应答
        mapRtn.replace("code", "SUCCESS");
        mapRtn.replace("message", "成功");
        return mapRtn;
    }


    @PostMapping("/Notification/Payment")
    @AppLog(operModule = "微信小程序支付 -- 支付通知API", operType = "R", operDesc = "微信支付通过支付通知接口将用户支付成功消息通知给商户。")
    public Object getPaymentNotifications(@RequestHeader MultiValueMap<String, String> reqHeader, @RequestBody Map<String, Object> reqBody) throws GeneralSecurityException, IOException {
        Map<String, Object> mapRtn = new HashMap<>();
        System.out.println("调用回调================================================================");
        mapRtn.put("code", "FAIL");
        mapRtn.put("message", "失败");
        log.info("支付通知API Header:\n" + reqHeader);
        log.info("支付通知API Body:\n" + reqBody);
        // 1.签名值放在通知的HTTP头Wechatpay-Signature，验证签名以确认请求来自微信
        if (!WeChatPayUtil.checkWeChatSignature(reqHeader, reqBody, CERTIFICATE_MAP)) {
            throw new SignatureException("微信支付签名非法");
        }
        // 2.商户对resource对象进行解密后，得到的资源对象示例
        Map<String, String> mapResource = (Map<String, String>) reqBody.get("resource");
        WeChatPayAesUtil aesUtil = new WeChatPayAesUtil(AppConstant.WECHAT_PAY_API_V3_KEY.getBytes());
        String content = aesUtil.decryptToString(mapResource.get("associated_data").getBytes(), mapResource.get("nonce").getBytes(), mapResource.get("ciphertext"));
        log.info("支付通知API: " + content);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> resourceMap = objectMapper.readValue(content, Map.class);
        /**
         * 3.当商户系统收到通知进行处理时，先检查对应业务数据的状态，并判断该通知是否已经处理。
         * 如果未处理，则再进行处理；如果已处理，则直接返回结果成功。
         */

        // 4.通知应答
        mapRtn.replace("code", "SUCCESS");
        mapRtn.replace("message", "成功");

        return mapRtn;
    }


    @GetMapping("/Bill/FundFlowBill/{bill_date}")
    @AppLog(operModule = "微信小程序支付 -- 申请资金账单API", operType = "R", operDesc = "微信支付按天提供微信支付账户的资金流水账单文件，商户可以通过该接口获取账单文件的下载地址。")
    public Object getFundFlowBill(@PathVariable("bill_date") String billDate) throws Exception {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https").host("api.mch.weixin.qq.com").addPathSegment("v3")
                .addPathSegment("bill").addPathSegment("fundflowbill")
                .addQueryParameter("bill_date", billDate).build();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", WeChatPayUtil.getToken("GET", url, ""));
        HttpEntity httpEntity = new HttpEntity<>(headers);
        // 申请交易账单API
        ResponseEntity<Map> re = restTemplate.exchange(url.toString(), HttpMethod.GET, httpEntity, Map.class);
        if (200 != re.getStatusCodeValue()) {
            throw new RuntimeException(re.getStatusCodeValue() + re.toString());
        }
        if (!WeChatPayUtil.checkWeChatSignature(re, CERTIFICATE_MAP)) {
            throw new SignatureException("微信支付应答签名非法，验证不通过");
        }
        Map billMap = re.getBody();
        HttpUrl billHttpUrl = HttpUrl.parse(String.valueOf(billMap.get("download_url")));
        HttpHeaders billHeaders = new HttpHeaders();
        billHeaders.add("Authorization", WeChatPayUtil.getToken("GET", billHttpUrl, ""));
        HttpEntity billHttpEntity = new HttpEntity<>(billHeaders);
        return restTemplate.exchange(billHttpUrl.toString(), HttpMethod.GET, billHttpEntity, String.class);
    }

    @GetMapping("/Bill/TradeBill/{bill_date}")
    @AppLog(operModule = "微信小程序支付 -- 申请交易账单API", operType = "R", operDesc = "微信支付按天提供交易账单文件，商户可以通过该接口获取账单文件的下载地址。")
    public Object getTradeBill(@PathVariable("bill_date") String billDate) throws Exception {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https").host("api.mch.weixin.qq.com").addPathSegment("v3")
                .addPathSegment("bill").addPathSegment("tradebill")
                .addQueryParameter("bill_date", billDate).build();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", WeChatPayUtil.getToken("GET", url, ""));
        HttpEntity httpEntity = new HttpEntity<>(headers);
        // 申请交易账单API
        ResponseEntity<Map> re = restTemplate.exchange(url.toString(), HttpMethod.GET, httpEntity, Map.class);
        if (200 != re.getStatusCodeValue()) {
            throw new RuntimeException(re.getStatusCodeValue() + re.toString());
        }
        Map billMap = re.getBody();
        HttpUrl billHttpUrl = HttpUrl.parse(String.valueOf(billMap.get("download_url")));
        HttpHeaders billHeaders = new HttpHeaders();
        billHeaders.add("Authorization", WeChatPayUtil.getToken("GET", billHttpUrl, ""));
        HttpEntity billHttpEntity = new HttpEntity<>(billHeaders);
        return restTemplate.exchange(billHttpUrl.toString(), HttpMethod.GET, billHttpEntity, String.class);
    }

    @GetMapping("/OrderState/Refund/{out_refund_no}")
    @AppLog(operModule = "微信小程序支付 -- 查询单笔退款API", operType = "R", operDesc = "提交退款申请后，通过调用该接口查询退款状态。")
    public Object getPayOrderRefundState(@PathVariable("out_refund_no") String refundNo)
            throws InvalidKeyException, SignatureException, NoSuchAlgorithmException, UnsupportedEncodingException {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https").host("api.mch.weixin.qq.com").addPathSegment("v3")
                .addPathSegment("refund").addPathSegment("domestic").addPathSegment("refunds")
                .addPathSegment(refundNo).build();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", WeChatPayUtil.getToken("GET", url, ""));
        return restTemplate.exchange(url.toString(), HttpMethod.GET, new HttpEntity<>(headers), Map.class);
    }

    @PostMapping("/Refund")
    @AppLog(operModule = "微信小程序支付 -- 申请退款API", operType = "C", operDesc = "当交易发生之后一年内，由于买家或者卖家的原因需要退款时，卖家可以通过退款接口将支付金额退还给买家，微信支付将在收到退款请求并且验证成功之后，将支付款按原路退还至买家账号上。")
    public Object getPayOrderRefund(@RequestBody String bodyText) throws Exception {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https").host("api.mch.weixin.qq.com").addPathSegment("v3")
                .addPathSegment("refund").addPathSegment("domestic").addPathSegment("refunds").build();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        headers.add("Authorization", WeChatPayUtil.getToken("POST", url, bodyText));
        HttpEntity httpEntity = new HttpEntity<>(bodyText, headers);
        return restTemplate.postForEntity(url.toString(), httpEntity, Map.class);
    }

    @GetMapping("/OrderState/Merchant/{orderID}")
    @AppLog(operModule = "微信小程序支付 -- 商户订单号查询", operType = "R", operDesc = "商户可以通过查询订单接口主动查询订单状态并完成下一步的业务逻辑")
    public Object getPayOrderStateByTransactionID(@PathVariable("orderID") String orderId) throws Exception {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https").host("api.mch.weixin.qq.com").addPathSegment("v3")
                .addPathSegment("pay").addPathSegment("transactions").addPathSegment("out-trade-no")
                .addPathSegment(orderId).addQueryParameter("mchid", AppConstant.WECHAT_MERCHANT_ID).build();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", WeChatPayUtil.getToken("GET", url, ""));
        HttpEntity httpEntity = new HttpEntity<>(headers);
        return restTemplate.exchange(url.toString(), HttpMethod.GET, httpEntity, Map.class);
    }

    @GetMapping("/OrderState/Wechat/{transactionId}")
    @AppLog(operModule = "微信小程序支付 -- 微信支付订单号查询", operType = "R", operDesc = "商户可以通过查询订单接口主动查询订单状态并完成下一步的业务逻辑")
    public Object getPayOrderStateByOrderID(@PathVariable("transactionId") String transactionId) throws Exception {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https").host("api.mch.weixin.qq.com").addPathSegment("v3")
                .addPathSegment("pay").addPathSegment("transactions").addPathSegment("id")
                .addPathSegment(transactionId).addQueryParameter("mchid", AppConstant.WECHAT_MERCHANT_ID)
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", WeChatPayUtil.getToken("GET", url, ""));
        HttpEntity httpEntity = new HttpEntity<>(headers);
        return restTemplate.exchange(url.toString(), HttpMethod.GET, httpEntity, Map.class);
    }

    @GetMapping("/OrderClose/{orderID}")
    @AppLog(operModule = "微信小程序支付 -- 关闭订单API", operType = "U", operDesc = "商户订单支付失败需要生成新单号重新发起支付，要对原订单号调用关单，避免重复支付；系统下单后，用户支付超时，系统退出不再受理，避免用户继续，请调用关单接口")
    public Object closePayOrder(@PathVariable("orderID") String orderId) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https").host("api.mch.weixin.qq.com").addPathSegment("v3")
                .addPathSegment("pay").addPathSegment("transactions").addPathSegment("out-trade-no")
                .addPathSegment(orderId).addPathSegment("close").build();
        String bodyText = "{\"mchid\":\"" + AppConstant.WECHAT_MERCHANT_ID + "\"}";
        headers.add("Authorization", WeChatPayUtil.getToken("POST", url, bodyText));
        headers.add("Content-Type", "application/json; charset=utf-8");
        HttpEntity<String> httpEntity = new HttpEntity<>(bodyText, headers);
        // 关闭订单
        return restTemplate.postForEntity(url.toString(), httpEntity, Map.class);
    }

    /*@PostMapping("/PaySign")
    @ApiOperation("支付时先调用这个接口-->构造小程序调起支付签名串")
    public CommonResult createPaySign(@RequestBody String bodyText) throws Exception {
        String prePayId = getPrepayID(bodyText);
        Map respBody = getRequestPayment(prePayId);
        //return ResponseEntity.ok().body(respBody);
        return CommonResult.success(respBody, "获取成功");
    }*/

    @PostMapping("/PaySignOrder")
    @ApiOperation("订单支付时先调用这个接口-->构造小程序调起支付签名串")
    public CommonResult createPaySignForOrdery(@RequestBody BodyText bodyText) throws Exception {
        bodyText.setAppid(appid);
        bodyText.setMchid(mchid);
        bodyText.setNotify_url(notify_url);
        bodyText.setDescription(description);
        //String substring = UUID.randomUUID().toString().substring(0, 6);
        //bodyText.setOut_trade_no(substring + bodyText.getOut_trade_no());
        String a = new ObjectMapper().writeValueAsString(bodyText);
        String prePayId = getPrepayID(a);
        Map respBody = getRequestPayment(prePayId);
        //String balanceId = UUID.randomUUID().toString().replace("-", "");
        //respBody.put("balanceId", balanceId);
        //respBody.put("out_trade_no", bodyText.getOut_trade_no());
        //userBalanceService.insertToUserBalance(balanceId, bodyText.getOut_trade_no(), bodyText.getAmount().getTotal());
        return CommonResult.success(respBody, "创建订单成功");
    }

    /*@PostMapping("/PayJoinSignOrder")
    @ApiOperation("合单支付时先调用这个接口-->构造小程序调起支付签名串")
    public CommonResult createPayJoinSignForOrdery(@RequestBody JoinBodyText joinBodyText) throws Exception {

        return CommonResult.success("创建订单成功");
    }*/


    @PostMapping("/PaySign")
    @ApiOperation("余额充值支付时先调用这个接口-->构造小程序调起支付签名串")
    public CommonResult createPaySign(@RequestBody BodyText bodyText) throws Exception {
        bodyText.setAppid(appid);
        bodyText.setMchid(mchid);
        bodyText.setNotify_url(notify_url);
        bodyText.setDescription(description);
        String substring = UUID.randomUUID().toString().substring(0, 6);
        bodyText.setOut_trade_no(substring + bodyText.getOut_trade_no());
        String a = new ObjectMapper().writeValueAsString(bodyText);
        String prePayId = getPrepayID(a);
        Map respBody = getRequestPayment(prePayId);
        String balanceId = UUID.randomUUID().toString().replace("-", "");
        respBody.put("balanceId", balanceId);
        respBody.put("out_trade_no", bodyText.getOut_trade_no());
        return CommonResult.success(respBody, "");
    }


    @GetMapping("/Token")
    @AppLog(operModule = "微信小程序支付 -- 生成请求的签名", operType = "R", operDesc = "微信支付API v3 要求商户生成请求的签名")
    public Object getToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", createToken());
        return ResponseEntity.noContent().headers(headers).build();
    }

    @AppLog(operModule = "微信小程序支付 -- 获取平台证书列表", operType = "R", operDesc = "获取商户当前可用的平台证书列表")
    @Scheduled(fixedDelay = 21600000) // 6小时
    public void getCertificates() throws IOException, GeneralSecurityException {
        String wxPaySerial = "";
        String wxPaySignature = "";
        String wxPayTimestamp = "";
        String wxPayNonce = "";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", createToken());
        HttpEntity httpEntity = new HttpEntity(httpHeaders);
        ResponseEntity<String> re = restTemplate.exchange(wxUrl, HttpMethod.GET, httpEntity, String.class);
        httpHeaders = re.getHeaders();
        if (!httpHeaders.get("Wechatpay-Serial").isEmpty())
            wxPaySerial = httpHeaders.get("Wechatpay-Serial").get(0); // 平台证书序列号
        if (!httpHeaders.get("Wechatpay-Signature").isEmpty())
            wxPaySignature = httpHeaders.get("Wechatpay-Signature").get(0); // 获取应答签名
        if (!httpHeaders.get("Wechatpay-Timestamp").isEmpty())
            wxPayTimestamp = httpHeaders.get("Wechatpay-Timestamp").get(0); // 应答时间戳
        if (!httpHeaders.get("Wechatpay-Nonce").isEmpty())
            wxPayNonce = httpHeaders.get("Wechatpay-Nonce").get(0); // 应答随机串

        ObjectMapper objectMapper = new ObjectMapper();
        CertificatesDto data = objectMapper.readValue(re.getBody(), CertificatesDto.class);
        // 清空原证书，准备接受新证书列表
        CERTIFICATE_MAP.clear();
        for (CertificateDto cert : data.getData()) {
            String nonce = cert.getEncrypt_certificate().getNonce();
            String text = cert.getEncrypt_certificate().getCiphertext();
            String ass = cert.getEncrypt_certificate().getAssociated_data();
            WeChatPayAesUtil aesUtil = new WeChatPayAesUtil(AppConstant.WECHAT_PAY_API_V3_KEY.getBytes());
            // 获取“平台证书”公钥
            String wxPublicKey = aesUtil.decryptToString(ass.getBytes(), nonce.getBytes(), text);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(wxPublicKey.getBytes(StandardCharsets.UTF_8));
            final CertificateFactory cf = CertificateFactory.getInstance("X509");
            Certificate certificate = cf.generateCertificate(inputStream);
            // 验证平台证书签名
            // PublicKey pubKey = certificate.getPublicKey();
            // certificate.verify(pubKey);
            // 添加“平台证书”
            CERTIFICATE_MAP.put(wxPaySerial, certificate);
        }
    }

    /**
     * Create Token
     */
    private String createToken() {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https").host("api.mch.weixin.qq.com").addPathSegment("v3")
                .addPathSegment("certificates").build();
        try {
            return WeChatPayUtil.getToken("GET", url, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取预支付交易会话标识，用于后续接口调用中使用
     *
     * @param bodyText 请求JSON文本
     * @return 预支付交易会话标识
     * @throws Exception
     */
    private String getPrepayID(String bodyText) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https").host("api.mch.weixin.qq.com").addPathSegment("v3")
                .addPathSegment("pay").addPathSegment("transactions")
                .addPathSegment("jsapi").build();
        headers.add("Content-Type", "application/json; charset=utf-8");
        headers.add("Authorization", WeChatPayUtil.getToken("POST", url, bodyText));
        HttpEntity<String> httpEntity = new HttpEntity<>(bodyText, headers);
        System.out.println("================" + httpEntity.toString());
        // JSAPI下单
        ResponseEntity<Map> re = restTemplate.postForEntity(url.toString(), httpEntity, Map.class);
        Map mapBody = re.getBody();
        // 预支付交易会话标识
        return String.valueOf(mapBody.get("prepay_id"));
    }

    /*  *//**
     * 获取合单预支付交易会话标识，用于后续接口调用中使用
     *
     * @param bodyText 请求JSON文本
     * @return 预支付交易会话标识
     * @throws Exception
     *//*
    private String getJoinPrepayID(String bodyText) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https").host("api.mch.weixin.qq.com").addPathSegment("v3")
                .addPathSegment("pay").addPathSegment("combine-transactions")
                .addPathSegment("jsapi").build();
        headers.add("Content-Type", "application/json; charset=utf-8");
        headers.add("Authorization", WeChatPayUtil.getToken("POST", url, bodyText));
        HttpEntity<String> httpEntity = new HttpEntity<>(bodyText, headers);
        System.out.println("================" + httpEntity.toString());
        // JSAPI下单
        ResponseEntity<Map> re = restTemplate.postForEntity(url.toString(), httpEntity, Map.class);
        Map mapBody = re.getBody();
        // 预支付交易会话标识
        return String.valueOf(mapBody.get("prepay_id"));
    }*/

    /**
     * 组装“小程序调起支付API”参数列表
     *
     * @param prepayId 预支付交易会话标识
     * @return 参数列表
     */
    private Map getRequestPayment(String prepayId)
            throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        Map mapRtn = new HashMap();
        String nonceStr = AppUtil.createUUID();
        //long timestamp = System.currentTimeMillis() / 1000;
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);

        // 构造签名串
        String signStr = new StringBuilder()
                .append(AppConstant.WECHAT_APPID).append("\n")
                .append(timestamp).append("\n")
                .append(nonceStr).append("\n")
                .append("prepay_id=").append(prepayId).append("\n")
                .toString();

        // 签名，使用字段appId、timeStamp、nonceStr、package计算得出的签名值
        String paySign = WeChatPayUtil.sign(signStr.getBytes(StandardCharsets.UTF_8));
        // 组装列表
        mapRtn.put("appId", AppConstant.WECHAT_APPID);
        mapRtn.put("nonceStr", nonceStr);
        mapRtn.put("timeStamp", timestamp);
        mapRtn.put("package", "prepay_id=" + prepayId);
        mapRtn.put("signType", "RSA");
        mapRtn.put("paySign", paySign);
        return mapRtn;
    }
}
