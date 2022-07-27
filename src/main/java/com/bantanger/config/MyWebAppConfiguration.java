package com.bantanger.config;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Date 2022/7/27 22:22
 */
public class MyWebAppConfiguration implements WebMvcConfigurer {
    //定制资源映射
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //意思是：url中读取到/upload时，就会自动将/upload解析成D:/idea/java_workspace/image/upload
        registry.addResourceHandler("/upload/**").addResourceLocations("file:E:\\work\\data\\upload");
        /**
         * Linux系统
         * registry.addResourceHandler("/upload/**").addResourceLocations("file:/home/image/upload/");
         */
    }
}
