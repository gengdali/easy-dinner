package com.huizi.easydinner.wechat.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huizi.easydinner.wechat.common.AppConstant;
import okhttp3.HttpUrl;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.util.MultiValueMap;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * 微信支付生成请求的签名
 */
public class WeChatPayUtil {
    private static final String schema = "WECHATPAY2-SHA256-RSA2048 ";

    /**
     * 商户生成请求的签名。
     * Authorization: <schema> <token>
     * GET - getToken("GET", httpurl, "")
     * POST - getToken("POST", httpurl, json)
     *
     * @param method GET/POST
     * @param url    httpurl
     * @param body   ""/json
     * @return Token string
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws SignatureException
     * @throws InvalidKeyException
     * @throws Exception
     */
    public static String getToken(String method, HttpUrl url, String body) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException, UnsupportedEncodingException {
        String nonceStr = AppUtil.createUUID();
        long timestamp = System.currentTimeMillis() / 1000;
        String message = buildMessage(method, url, timestamp, nonceStr, body);
        String signature = sign(message.getBytes("utf-8"));

        return schema + "mchid=\"" + AppConstant.WECHAT_MERCHANT_ID + "\","
                + "nonce_str=\"" + nonceStr + "\","
                + "timestamp=\"" + timestamp + "\","
                + "serial_no=\"" + AppConstant.WECHAT_CERTIFICATE_SERIAL_NO + "\","
                + "signature=\"" + signature + "\"";
    }


    public static String sign(byte[] message) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        KeyPairFactory keyPairFactory = new KeyPairFactory();
        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initSign(keyPairFactory.createPKCS12("Tenpay Certificate", AppConstant.WECHAT_MERCHANT_ID).getPrivate());
        sign.update(message);
        return Base64.getEncoder().encodeToString(sign.sign());
    }


    private static String buildMessage(String method, HttpUrl url, long timestamp, String nonceStr, String body) {
        String canonicalUrl = url.encodedPath();
        if (url.encodedQuery() != null) {
            canonicalUrl += "?" + url.encodedQuery();
        }

        return method + "\n"
                + canonicalUrl + "\n"
                + timestamp + "\n"
                + nonceStr + "\n"
                + body + "\n";
    }


    /**
     * 签名验证
     * 如果验证商户的请求签名正确，微信支付会在应答的HTTP头部中包括应答签名。我们建议商户验证应答签名。
     * 同样的，微信支付会在回调的HTTP头部中包括回调报文的签名。商户必须 验证回调的签名，以确保回调是由微信支付发送。
     */
    public static boolean checkWeChatSignature(ResponseEntity<Map> respEntity, Map<String, Certificate> certificateMap) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, JsonProcessingException {
        Certificate wxCert;
        // 检查平台证书序列号
        List wxSerials = respEntity.getHeaders().get("Wechatpay-Serial");
        if (null == wxSerials || wxSerials.size() == 0) {
            return false;
        } else {
            if (!certificateMap.containsKey(wxSerials.get(0))) {
                return false;
            } else {
                wxCert = certificateMap.get(wxSerials.get(0));
            }
        }
        // 应答时间戳
        String wxRespTimestamp = respEntity.getHeaders().get("Wechatpay-Timestamp").get(0);
        // 应答随机串
        String wxRespNonce = respEntity.getHeaders().get("Wechatpay-Nonce").get(0);
        // 应答报文主体
        String wxRespBody = new ObjectMapper().writeValueAsString(respEntity.getBody());
        System.out.println(wxRespBody);
        // 构造验签名串
        String wxSign = new StringBuilder()
                .append(wxRespTimestamp).append("\n")
                .append(wxRespNonce).append("\n")
                .append(wxRespBody.replace(" ", "")).append("\n")
                .toString();
        System.out.println(wxSign);
        // 获取应答签名，对Wechatpay-Signature的字段值使用Base64进行解码，得到应答签名。
        byte[] wxRespSign = Base64Utils.decodeFromString(respEntity.getHeaders().get("Wechatpay-Signature").get(0));
        // 验证签名
        Signature sign = Signature.getInstance("SHA256withRSA");
        // 使用证书初始化签名对象
        sign.initVerify(wxCert);
        // 更新"验签名串"到签名器中
        sign.update(wxSign.getBytes(StandardCharsets.UTF_8));
        return sign.verify(wxRespSign);
    }


    /**
     * 签名验证
     * 如果验证商户的请求签名正确，微信支付会在应答的HTTP头部中包括应答签名。我们建议商户验证应答签名。
     * 同样的，微信支付会在回调的HTTP头部中包括回调报文的签名。商户必须 验证回调的签名，以确保回调是由微信支付发送。
     */
    public static boolean checkWeChatSignature(MultiValueMap<String, String> headers, Map body, Map<String, Certificate> certificateMap) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, JsonProcessingException {
        Certificate wxCert;
        // 检查平台证书序列号
        List<String> wxSerials = headers.get("wechatpay-serial");
        if (null == wxSerials || wxSerials.size() == 0) {
            return false;
        } else {
            if (!certificateMap.containsKey(wxSerials.get(0))) {
                return false;
            } else {
                wxCert = certificateMap.get(wxSerials.get(0));
            }
        }
        // 应答时间戳
        String wxRespTimestamp = headers.get("wechatpay-timestamp").get(0);
        // 应答随机串
        String wxRespNonce = headers.get("wechatpay-nonce").get(0);
        // 应答报文主体
        String wxRespBody = new ObjectMapper().writeValueAsString(body);
        System.out.println(wxRespBody);
        // 构造验签名串
        String wxSign = new StringBuilder()
                .append(wxRespTimestamp).append("\n")
                .append(wxRespNonce).append("\n")
                .append(wxRespBody.replace(" ", "")).append("\n")
                .toString();
        System.out.println(wxSign);
        // 获取应答签名，对Wechatpay-Signature的字段值使用Base64进行解码，得到应答签名。
        byte[] wxRespSign = Base64Utils.decodeFromString(headers.get("wechatpay-signature").get(0));
        // 验证签名
        Signature sign = Signature.getInstance("SHA256withRSA");
        // 使用证书初始化签名对象
        sign.initVerify(wxCert);
        // 更新"验签名串"到签名器中
        sign.update(wxSign.getBytes(StandardCharsets.UTF_8));
        return sign.verify(wxRespSign);
    }

}
