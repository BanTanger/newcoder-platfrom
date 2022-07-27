package com.bantanger.service;

import com.bantanger.dao.LoginTicketMapper;
import com.bantanger.dao.UserMapper;
import com.bantanger.entity.LoginTicket;
import com.bantanger.entity.User;
import com.bantanger.util.CommunityUtil;
import com.bantanger.util.CommunityConstant;
import com.bantanger.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import sun.security.krb5.internal.Ticket;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Date 2022/6/23 15:48
 */
@SuppressWarnings("all")
@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    /*发送邮箱的模板生成引擎*/
    @Autowired
    private TemplateEngine templateEngine;

    /*用户注册邮箱的域名*/
    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

    /**
     * 注册用户，发送用户激活码邮件
     * @param user
     * @return
     */
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();
        if (user == null) throw new IllegalArgumentException("参数不能为空");
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空");
            return map;
        }
        User u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "该邮箱已被注册！");
            return map;
        }
        u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "该账号已存在！");
            return map;
        }

        // 注册用户：加盐和一些后端默认操作
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);  // 用户身份类型
        user.setStatus(0); // 创建用户的激活情况，当前是未激活
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        // 经过各种检测之后将用户数据加入到数据库中
        userMapper.insertUser(user);

        // 激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // 生成地址为：http://localhost:8080/community/activation/101/code
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);
        return map;
    }

    /**
     * 用户通过邮箱注册的激活码状态判断
     * @param userId 用户id
     * @param code 激活码
     * @return 激活码情况
     */
    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    /**
     * 用户登陆过程生成凭证依据
     * @param username 用户名称
     * @param password 用户密码（md5不可逆加密）
     * @param expiredSeconds 超时时间（24小时）
     * @return 成败信息
     */
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>(); // 返回到消息信息
        // 空值特判
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }
        // 合法性验证
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "账号不存在！");
            return map;
        }
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该账号未激活！");
            return map;
        }
        // 验证密码
        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码不正确");
            return map;
        }

        // 生成登陆凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicketMapper.insertLoginTicket(loginTicket);

        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    /**
     * 用户通过将设置ticket，退出登陆
     * @param ticket
     */
    public void logout(String ticket) {
        loginTicketMapper.updateTicket(ticket, 1);
    }

    /**
     * 用户忘记密码，通过发送邮件获取验证码
     * @param email 用户邮箱
     * @return 成败信息
     */
    public Map<String, Object> getCode(String email) {
        Map<String, Object> map = new HashMap<>(); // 返回到消息信息
        // 非空判断
        if (StringUtils.isBlank(email)) {
            map.put("emailMsg", "邮箱不能为空！");
            return map;
        }
        // 校验填表信息的合法性
        User user = userMapper.selectByEmail(email);
        if (user == null) {
            map.put("emailMsg", "该邮箱还未注册过，请注册后再使用！");
            return map;
        }
        //该用户还未激活
        if (user.getStatus() == 0) {
            map.put("emailMsg", "该邮箱还未激活，请到邮箱中激活后再使用！");
            return map;
        }
        // 调用邮箱系统给这个用户发送验证码
        Context context = new Context();
        context.setVariable("email", user.getEmail()); // 得到这个用户的邮箱并且发送验证码
        String vericode = CommunityUtil.generateUUID().substring(0, 6); // 生成六位数验证码
        context.setVariable("vericode", vericode); // 将其封装到context内容中，前端通过模板引擎生成页面
        // 模板引擎通过process找到生成邮件的模板，我们的忘记密码邮箱模板地址就是/main/forget
        String content = templateEngine.process("/mail/forget", context);
        mailClient.sendMail(user.getEmail(), "牛客验证码", content); // 将封装好的内容通过邮箱系统发送出去
        map.put("vericode", vericode);
        map.put("expirationTime", LocalDateTime.now().plusMinutes(5L)); // 过期时间
        return map;
    }

    /**
     * 用户忘记密码，通过验证码来更改旧密码
     * @param email 用户邮箱
     * @param vericode 验证码
     * @param newpasswd 新密码
     * @param session 用于保存验证码到controller层验证正确性
     * @return 成败信息
     */
    public Map<String, Object> forget(String email, String vericode, String newpasswd, HttpSession session) {
        Map<String, Object> map = new HashMap<>(); // 返回到消息信息
        // 非空判断
        if (StringUtils.isBlank(email)) {
            map.put("emailMsg", "邮箱不能为空！");
            return map;
        }
        if (StringUtils.isBlank(vericode)) {
            map.put("vericodeMsg", "验证码不能为空！");
            return map;
        }
        if (StringUtils.isBlank(newpasswd)) {
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }
        User user = userMapper.selectByEmail(email);
        if (user == null) {
            map.put("emailMsg", "邮箱输入错误!");
            return map;
        }
        // 验证值的合理性
        if (!vericode.equals(session.getAttribute("vericode"))) {
            map.put("vericodeMsg", "验证码错误！");
            return map;
        }
        newpasswd = CommunityUtil.md5(newpasswd + user.getSalt());
        userMapper.updatePassword(user.getId(), newpasswd);
        return map;
    }

    public LoginTicket findLoginTicket(String ticket) {
        return loginTicketMapper.selectByTicket(ticket);
    }

    public int updateHeaderUrl(int userId, String headerUrl) {
        return userMapper.updateHeader(userId, headerUrl);
    }
}
