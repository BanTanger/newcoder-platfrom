package com.bantanger.controller;

import com.bantanger.config.kaptchaConfig;
import com.bantanger.entity.User;
import com.bantanger.service.UserService;
import com.bantanger.util.CommunityConstant;
import com.bantanger.util.CommunityUtil;
import com.bantanger.util.RedisKeyUtil;
import com.google.code.kaptcha.Producer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Date 2022/7/1 22:51
 */

@Controller
public class LoginController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaConfig;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${server.servlet.context-path")
    private String contextPath;

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login";
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user) {
        Map<String, Object> map = userService.register(user);
        if(map == null || map.isEmpty()) {
            model.addAttribute("msg", "注册成功,我们已经向您的邮箱发送了一封激活邮件,请尽快激活!");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        } else {
            if(map.containsKey("usernameMsg")) model.addAttribute("usernameMsg", map.get("usernameMsg"));
            if(map.containsKey("passwordMsg")) model.addAttribute("passwordMsg", map.get("passwordMsg"));
            if(map.containsKey("emailMsg")) model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }
    }

    // http://localhost:8080/community/activation/101/code
    @RequestMapping(path = "/activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {
        int activation = userService.activation(userId, code);
        if(activation == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "激活成功！");
            model.addAttribute("target", "/login");
        } else if (activation == ACTIVATION_REPEAT){
            model.addAttribute("msg", "无效操作，该账号已经成功激活!");
            model.addAttribute("target", "/index");
        } else {
            model.addAttribute("msg", "激活失败，提供激活码不正确!");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }


    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response /* , HttpSession session 重构 */) {
        // 通过kaptchaConfig配置类生成验证码
        String text = kaptchaConfig.createText();
        BufferedImage image = kaptchaConfig.createImage(text);

        /* 重构 --> 使用redis
        // 将验证码存入session中
        session.setAttribute("kaptcha", text);
        */

        // 验证码的归属
        String kaptchaOwner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie); // response 将 cookie 添加
        // 将验证码存入Redis中
        String kaptchaKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(kaptchaKey, text,
                // 超时时间为 60 单位为秒
                60, TimeUnit.SECONDS);

        // 将图片返回给浏览器
        response.setContentType("image/png");

        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            logger.error("响应验证码失败:" + e.getMessage());
        }
    }

    // 用户登录的逻辑书写
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(String username, String password, String code, boolean rememberme,
                        Model model, /* HttpSession session, */ HttpServletResponse response,
                        @CookieValue("kaptchaOwner") String kaptchaOwner) {
        /* 重构 --> 使用redis获取
        String kaptcha = (String) session.getAttribute("kaptcha");
        */
        String kaptcha = null;
        if(StringUtils.isNotBlank(kaptchaOwner)) {
            String kaptchaKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(kaptchaKey);
        }

        if(StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code)
                || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg", "验证码不正确！");
            return "/site/login";
        }

        // 检查账号密码
        int expiredSeconds = rememberme ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        if(map.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "site/login";
        }
    }

    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/login";
    }

    @RequestMapping(path = "/forget", method = RequestMethod.GET)
    public String getForgetPage() {
        return "/site/forget";
    }

    //忘记密码之后获取验证码
    @GetMapping(path = "/verifycode/{email}")
    @ResponseBody
    public String getCode(@PathVariable("email") String email, Model model,HttpSession session) {
        Map<String, Object> map = userService.getCode(email);
        if (map.containsKey("emailMsg")) {//有错误的情况下
            model.addAttribute("emailMsg", map.get("emailMsg"));
        } else {//正确的情况下，向邮箱发送了验证码
            model.addAttribute("msg", "验证码已经发送到您的邮箱，5分钟内有效！");
            //将验证码存放在 session 中，后序和用户输入的信息进行比较
            session.setAttribute("vericode",map.get("vericode"));
            //后序判断用户输入验证码的时候验证码是否已经过期
            session.setAttribute("expirationTime",map.get("expirationTime"));
        }
        return CommunityUtil.getJSONString(200, "邮件发送成功!");
    }

    @RequestMapping(path = "/forget/password", method = RequestMethod.POST)
    public String forgetPost(Model model, String email, String vericode,
                             String newpasswd, HttpSession session) {
        String code =(String) session.getAttribute("vericode");
        // 特判
        if(StringUtils.isBlank(code) || StringUtils.isBlank(vericode)
                || !vericode.equals(code)) {
            model.addAttribute("vericodeMsg", "验证码不正确");
            return "/site/forget";
        }
        //验证码是否过期
        if (LocalDateTime.now().isAfter((LocalDateTime) session.getAttribute("expirationTime"))) {
            model.addAttribute("vericodeMsg", "输入的验证码已过期，请重新获取验证码！");
            return "site/forget";
        }
        Map<String, Object> map = userService.forget(email, vericode, newpasswd, session);
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg", "密码修改成功，可以使用新密码登录了!");
            model.addAttribute("target", "/login");
            return "site/operate-result";
        } else {
            model.addAttribute("emailMsg", map.get("emailMsg"));
            model.addAttribute("vericodeMsg", map.get("vericodeMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/forget";
        }
    }
}
