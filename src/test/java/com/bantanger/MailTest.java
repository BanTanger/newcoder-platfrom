package com.bantanger;

import com.bantanger.util.MailClient;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Date 2022/6/29 16:04
 */

@SpringBootTest
public class MailTest {
    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void test(){
        mailClient.sendMail("1290288968@qq.com","Test","Welcome.");
    }

    @Test
    public void HTMLtest(){
        Context context = new Context();
        context.setVariable("username", "bantanger半糖先生");
        String process = templateEngine.process("/mail/demo", context);
        System.out.println(process);
        mailClient.sendMail("1290288968@qq.com","HTML", process);
    }
}
