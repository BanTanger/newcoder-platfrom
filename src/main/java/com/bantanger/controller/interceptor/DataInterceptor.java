package com.bantanger.controller.interceptor;

import com.bantanger.entity.User;
import com.bantanger.service.DataService;
import com.bantanger.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Description
 * @Date 2022/9/4 21:01
 */

@Component
public class DataInterceptor implements HandlerInterceptor {

    @Autowired
    private DataService dateService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获得用户的ip地址
        String ip = request.getRemoteAddr();
        // 存放在ua里
        dateService.recordUV(ip);
        // 存放在dau里
        User user = hostHolder.getUser();
        if (user != null) {
            dateService.recordDAU(user.getId());
        }

        return true;
    }
}
