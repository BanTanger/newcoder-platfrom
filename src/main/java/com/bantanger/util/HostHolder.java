package com.bantanger.util;

import com.bantanger.entity.User;
import org.springframework.stereotype.Component;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Date 2022/7/27 17:41
 */

/**
 * 持有用户数据，用于替代session对象
 */
@Component
public class HostHolder {
    // 使用ThreadLocal 存放用户数据
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }
}
