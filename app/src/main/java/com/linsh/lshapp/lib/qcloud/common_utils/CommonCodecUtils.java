package com.linsh.lshapp.lib.qcloud.common_utils;


import com.linsh.lshapp.lib.qcloud.add.Base64;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author chengwu 封装了常用的MD5、SHA1、HmacSha1函数
 */
public class CommonCodecUtils {

    private static final String HMAC_SHA1 = "HmacSHA1";

    /**
     * 对二进制数据进行BASE64编码
     *
     * @param binaryData 二进制数据
     * @return 编码后的字符串
     */
    public static String Base64Encode(byte[] binaryData) {
        String encodedstr = Base64.encodeBase64String(binaryData);
        return encodedstr;
    }

    /**
     * 获取整个文件的SHA1
     * <p/>
     * 文件的输入流
     *
     * @return 文件对应的SHA1值
     * @throws Exception
     */
    public static String getEntireFileSha1(String filePath) throws Exception {
        InputStream fileInputStream = null;
        try {
            fileInputStream = CommonFileUtils.getFileInputStream(filePath);
            String sha1Digest = DigestUtils.sha1Hex(fileInputStream);
            return sha1Digest;
        } catch (Exception e) {
            String errMsg = "getFileSha1 occur a exception, file:" + filePath + ", exception:" + e.toString();
            throw new Exception(errMsg);
        } finally {
            try {
                CommonFileUtils.closeFileStream(fileInputStream, filePath);
            } catch (Exception e) {
                throw e;
            }
        }
    }

    /**
     * 计算数据的Hmac值
     *
     * @param binaryData 二进制数据
     * @param key        秘钥
     * @return 加密后的hmacsha1值
     */
    public static byte[] HmacSha1(byte[] binaryData, String key) throws Exception {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA1);
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1);
            mac.init(secretKey);
            byte[] HmacSha1Digest = mac.doFinal(binaryData);
            return HmacSha1Digest;

        } catch (NoSuchAlgorithmException e) {
            throw e;
        } catch (InvalidKeyException e) {
            throw e;
        } catch (IllegalStateException e) {
            throw e;
        }
    }

    /**
     * 计算数据的Hmac值
     *
     * @param plainText 文本数据
     * @param key       秘钥
     * @return 加密后的hmacsha1值
     */
    public static byte[] HmacSha1(String plainText, String key) throws Exception {
        return HmacSha1(plainText.getBytes(), key);
    }
}
