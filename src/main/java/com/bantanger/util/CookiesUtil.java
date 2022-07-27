package com.bantanger.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Date 2022/7/27 17:06
 */
public class CookiesUtil {
    /*获取request请求中的Cookies工具类*/

    /**
     * 找到cookies中当前登陆用户的凭证 ticket
     * @param request 前端request请求
     * @param name 登陆用户名称
     * @return 用户登陆凭证 ticket
     */
    public static String getValue(HttpServletRequest request, String name) {
        if(request == null || name == null) {
            throw new IllegalArgumentException("参数为空！");
        }
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for(Cookie cookie : cookies) {
                if(cookie.getName().equals(name)) {
                    return cookie.getValue(); // 返回凭证 ticket
                }
            }
        }
        return null;
    }
}
