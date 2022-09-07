package com.bantanger.actuator;

import com.bantanger.controller.advice.ExceptionAdvice;
import com.bantanger.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Description 数据库自定义端点
 * @Date 2022/9/6 20:09
 */

@Component
@Endpoint(id = "database")
public class DatabaseEndpoints {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @Autowired
    private DataSource dataSource;

    /**
     * 查看当前数据库连接池连接情况
     * @return
     */
    @ReadOperation
    public String checkConnection() {
        try (
                Connection connection = dataSource.getConnection();
        ){
            return CommunityUtil.getJSONString(0, "获取连接成功!");
        } catch (SQLException e) {
            logger.error("获取连接失败：" + e.getMessage());
            return CommunityUtil.getJSONString(1, "获取连接失败!");
        }
    }
}
