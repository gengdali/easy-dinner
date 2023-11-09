package com.huizi.easydinner.wechat.dto;

public class CertificateDto {

    private String effective_time;
    private String expire_time;
    private String serial_no;
    private EncryptCertificate encrypt_certificate;


    public String getEffective_time() {
        return effective_time;
    }

    public void setEffective_time(String effective_time) {
        this.effective_time = effective_time;
    }

    public String getExpire_time() {
        return expire_time;
    }

    public void setExpire_time(String expire_time) {
        this.expire_time = expire_time;
    }

    public String getSerial_no() {
        return serial_no;
    }

    public void setSerial_no(String serial_no) {
        this.serial_no = serial_no;
    }

    public EncryptCertificate getEncrypt_certificate() {
        return encrypt_certificate;
    }

    public void setEncrypt_certificate(EncryptCertificate encrypt_certificate) {
        this.encrypt_certificate = encrypt_certificate;
    }
}
