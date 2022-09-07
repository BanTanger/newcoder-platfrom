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
}
