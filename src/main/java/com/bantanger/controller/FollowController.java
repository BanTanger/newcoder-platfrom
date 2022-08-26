package com.bantanger.controller;

import com.bantanger.entity.Event;
import com.bantanger.entity.Page;
import com.bantanger.entity.User;
import com.bantanger.event.EventProducer;
import com.bantanger.service.FollowService;
import com.bantanger.service.UserService;
import com.bantanger.util.CommunityConstant;
import com.bantanger.util.CommunityUtil;
import com.bantanger.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Description
 * @Date 2022/8/22 16:27
 */

@Controller
public class FollowController implements CommunityConstant {

    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private EventProducer eventProducer;

    @PostMapping("/follow")
    @ResponseBody
    public String follow(int entityType, int entityId) {
        User user = hostHolder.getUser();

        followService.follow(user.getId(), entityType, entityId);

        // 触发关注事件
        Event event = new Event()
                .setTopic(TOPIC_FOLLOW)
                .setUserId(user.getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0, "已关注！");
    }

    @PostMapping("/unfollow")
    @ResponseBody
    public String unfollow(int entityType, int entityId) {
        User user = hostHolder.getUser();

        followService.unfollow(user.getId(), entityType, entityId);

        return CommunityUtil.getJSONString(0, "已取消关注！");
    }

    /**
     * 查看别人的关注列表
     * @param userId
     * @param page
     * @param model
     * @return
     */
    @GetMapping("/followees/{userId}")
    public String getFollowees(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在！");
        }
        model.addAttribute("user", user);

        page.setLimit(5);
        page.setPath("/followees/" + userId);
        // 设置总数
        page.setRows((int) followService.findFolloweeCount(userId, ENTITY_TYPE_USER));

        List<Map<String, Object>> userList = followService.findFollowees(
                userId, page.getOffset(), page.getLimit());
        if(userList != null) {
            for (Map<String, Object> map : userList) {
                User u =(User) map.get("user");
                // 检查该用户是否被当前用户所关注
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", userList);

        return "/site/followee";
    }

    /**
     * 查看别人的粉丝列表
     * @param userId
     * @param page
     * @param model
     * @return
     */
    @GetMapping("/followers/{userId}")
    public String getFollowers(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在！");
        }
        model.addAttribute("user", user);

        page.setLimit(5);
        page.setPath("/followers/" + userId);
        // 设置总数
        page.setRows((int) followService.findFollowerCount(ENTITY_TYPE_USER, userId));

        List<Map<String, Object>> userList = followService.findFollowers(
                userId, page.getOffset(), page.getLimit());
        if(userList != null) {
            for (Map<String, Object> map : userList) {
                User u =(User) map.get("user");
                // 检查该用户是否被当前用户所关注
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", userList);

        return "/site/follower";
    }

    private boolean hasFollowed(int userId) {
        if(hostHolder.getUser() == null) return false;
        return followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
    }

}
