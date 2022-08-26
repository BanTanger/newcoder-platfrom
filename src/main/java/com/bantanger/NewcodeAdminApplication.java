package com.bantanger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class NewcodeAdminApplication {

    /**
     * 将在main函数启动之后执行
     */
    @PostConstruct // 管理bean的生命周期
    public void init() {
        // 解决netty启动冲突的问题
        // 来源于 Netty4Utils.setAvailableProcessors()
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

    public static void main(String[] args) {
        SpringApplication.run(NewcodeAdminApplication.class, args);
    }

}
