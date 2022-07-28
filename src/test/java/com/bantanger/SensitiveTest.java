package com.bantanger;

import com.bantanger.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Date 2022/7/28 20:43
 */

@SpringBootTest
public class SensitiveTest {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter() {
        String text = "嫖娼，赌。博， 吸烟，喝酒杀人";
        text = sensitiveFilter.filter(text);
        System.out.println(text);
    }
}
