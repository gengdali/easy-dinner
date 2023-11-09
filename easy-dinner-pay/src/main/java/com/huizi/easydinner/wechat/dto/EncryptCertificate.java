package com.huizi.easydinner.wechat.dto;

public class EncryptCertificate {

    private String algorithm;
    private String associated_data;
    private String ciphertext;
    private String nonce;


    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getAssociated_data() {
        return associated_data;
    }

    public void setAssociated_data(String associated_data) {
        this.associated_data = associated_data;
    }

    public String getCiphertext() {
        return ciphertext;
    }

    public void setCiphertext(String ciphertext) {
        this.ciphertext = ciphertext;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }
}
