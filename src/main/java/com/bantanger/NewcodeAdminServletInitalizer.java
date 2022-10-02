package com.bantanger;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class NewcodeAdminServletInitalizer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(NewcodeAdminApplication.class); // 通过这个间接启动java程序
        // 因为在服务器里是通过tomcat来启动，而程序在服务器中是以war文件来存储，所以需要用这个启动类来启动java
    }
}
