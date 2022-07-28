package com.bantanger.interceptor;

import com.bantanger.annotation.LoginRequired;
import com.bantanger.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.invoke.MethodHandle;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Date 2022/7/28 16:17
 */

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    /**
     * 在每次页面请求前检测当前用户是否登陆
     * @param request 客户端请求
     * @param response 服务端响应
     * @param handler 前端请求后端代码的类型：方法，类，静态资源
     * @return 是否拦截
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 先判断当前前端请求后端资源是否位一个方法（静态资源已经通过过滤器过滤掉了）
        if(handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            // 查看当前方法是否标有LoginRequired注解
            LoginRequired loginRequired = handlerMethod.getMethodAnnotation(LoginRequired.class);
            if(loginRequired != null && hostHolder.getUser() == null) {
                // 该方法标有注解但是当前没有用户 （游客非法访问页面，拦截）
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }
        return true;
    }
}
