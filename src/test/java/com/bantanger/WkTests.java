package com.bantanger;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Description
 * @Date 2022/9/6 12:50
 */

public class WkTests {

    @Value("${wk.image.storage}")
    private String domain;

    @Test
    public void test() {
        File file = new File(domain);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    @Test
    public void test1() {
        double a, b, c;
        a = 0.05; b = 0.5; c = 0.0025;
        double res = (a * b) / (a * b + a * c);
        System.out.println(res);
    }

    @Test
    public void test2() {
        String a = "123";
        String b = new String("123");
        System.out.println(b.intern() == a);
    }
}
