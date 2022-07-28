package com.bantanger.interceptor;

import com.bantanger.entity.LoginTicket;
import com.bantanger.entity.User;
import com.bantanger.service.UserService;
import com.bantanger.util.CommunityUtil;
import com.bantanger.util.CookiesUtil;
import com.bantanger.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Date 2022/7/27 17:13
 */

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        // 从cookies中获取凭证
        String ticket = CookiesUtil.getValue(request, "ticket");
//        System.out.println(ticket);
        if(ticket != null) {
            // 查询凭证有效性
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
//            System.out.println(loginTicket.getStatus());
            if(loginTicket != null && loginTicket.getStatus() == 0
                    && loginTicket.getExpired().after(new Date())) {
                // 根据凭证查询用户
                User user = userService.findUserById(loginTicket.getUserId());
                // 在当前线程生命周期（用户请求到服务器回应）中持有用户信息,
                // 在代码中体现为调用controller之后，模板引擎之后
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user != null && modelAndView != null) {
            // 对所有请求做处理，将loginUser放入模板解析器里，再对前端代码进行特判，如果没有这个信息，就不显示
            modelAndView.addObject("loginUser", user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
