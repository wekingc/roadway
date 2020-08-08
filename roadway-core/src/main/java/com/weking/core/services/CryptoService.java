package com.weking.core.services;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Jim Cen
 * @date 2020/7/14 13:47
 */
public class CryptoService {
    /**
     * MD5加密服务
     * @param plainText 明文
     * @return 密文
     */
    public String md5Encode(String plainText) throws NoSuchAlgorithmException {
        byte[] secretBytes ;
        int md5Length = 32;
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(plainText.getBytes());
        secretBytes = md.digest();
        StringBuilder md5code = new StringBuilder(new BigInteger(1, secretBytes).toString(16));
        for (int i = 0; i < md5Length - md5code.length(); i++) {
            md5code.insert(0, "0");
        }
        return md5code.toString();
    }
}
