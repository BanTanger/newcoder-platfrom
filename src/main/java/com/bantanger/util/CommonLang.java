package com.bantanger.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Date 2022/7/3 20:56
 */
public class CommonLang {
    // 生成随机字符串
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    // MD5不可逆加盐操作
    public static String md5 (String key) {
        if(StringUtils.isBlank(key)) return null;
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
