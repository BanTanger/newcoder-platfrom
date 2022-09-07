package com.bantanger.controller;

import com.bantanger.annotation.LoginRequired;
import com.bantanger.entity.Message;
import com.bantanger.entity.User;
import com.bantanger.service.FollowService;
import com.bantanger.service.LikeService;
import com.bantanger.service.MessageService;
import com.bantanger.service.UserService;
import com.bantanger.util.CommunityConstant;
import com.bantanger.util.CommunityUtil;
import com.bantanger.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Date 2022/7/27 20:47
 */

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Value("${community.path.domain}")
    private String domain;

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @Autowired
    private MessageService messageService;

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片");
            return "/site/setting";
        }
        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件格式错误");
            return "/site/setting";
        }

        // 生成随机文件名
        fileName = CommunityUtil.generateUUID() + suffix;
        // 确定文件存放的路径名
        File dest = new File(uploadPath + "/" + fileName);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败", e.getMessage());
            throw new RuntimeException("上传文件路径失败，服务器出现异常！", e);
        }

        // 更新用户当前的头像路径(Web访问路径)
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeaderUrl(user.getId(), headerUrl);

        return "redirect:/index";
    }

    @GetMapping(path = "/header/{fileName}")
    public void getHeader(@PathVariable("fileName") String fileName,
                          HttpServletResponse response) {
        // 服务器存放路径
        fileName = uploadPath + "/" + fileName;
        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        // 响应图片
        response.setContentType("image/" + suffix);
        try (
                // jdk7特殊语法，关闭需要关闭的资源
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream ops = response.getOutputStream()
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                ops.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败: ", e.getMessage());
        }
    }

    @LoginRequired
    @RequestMapping(path = "/update", method = RequestMethod.POST)
    public String updatePassword(String oldPasswd, String newPasswd, String confirmPasswd
            , Model model, @CookieValue("ticket") String ticket) {
        if (oldPasswd == null || newPasswd == null || confirmPasswd == null) {
            model.addAttribute("passwordMsg", "请将字段填写完整");
            return "/site/setting";
        }
        // 从hostHolder拿到当前user用户
        User user = hostHolder.getUser();
        if (!user.getPassword().equals(CommunityUtil.md5(oldPasswd + user.getSalt()))) {
            model.addAttribute("oldPasswordMsg", "原密码错误！");
            return "/site/setting";
        }
        if (user.getPassword().equals(CommunityUtil.md5(newPasswd + user.getSalt()))) {
            model.addAttribute("newPasswordMsg", "新密码与原密码相同！");
            return "/site/setting";
        }
        if (!newPasswd.equals(confirmPasswd)) {
            model.addAttribute("confirmPasswordMsg", "两次输入密码不一致！");
            return "/site/setting";
        }
        userService.updatePassword(user.getId(), CommunityUtil.md5(newPasswd + user.getSalt()));
        // 调用logout退出登陆，再次登陆
        userService.logout(ticket);
        return "redirect:/login";
    }

    @LoginRequired
    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在！");
        }
        // 用户
        model.addAttribute("user", user);
        // 点赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);

        // 关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        // 粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);
        // 是否已关注
        boolean hasFollowed = false;
        if (hostHolder.getUser() != null) {
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);

        return "/site/profile";
    }

    @PostMapping(path = "/send")
    @ResponseBody /* 异步请求需要 ResponseBody */
    public String sendLetter(String toName, String content) {
        User target = userService.findUserByName(toName);
        if (target == null) {
            return CommunityUtil.getJSONString(1, "目标用户不存在！");
        }
        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        if (message.getFromId() > message.getToId()) {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        } else {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());
        message.setStatus(0);
        messageService.addMessage(message);

        return CommunityUtil.getJSONString(0, "操作成功！");
    }
}
