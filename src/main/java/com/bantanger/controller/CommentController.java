package com.bantanger.controller;

import com.bantanger.annotation.LoginRequired;
import com.bantanger.entity.Comment;
import com.bantanger.entity.DiscussPost;
import com.bantanger.entity.Event;
import com.bantanger.event.EventProducer;
import com.bantanger.service.CommentService;
import com.bantanger.service.DiscussPostService;
import com.bantanger.util.CommunityConstant;
import com.bantanger.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Description
 * @Date 2022/8/19 18:06
 */

@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private DiscussPostService discussPostService;

    @LoginRequired
    @PostMapping("/add/{discussPostId}")
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment) {
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0); // 有效
        comment.setCreateTime(new Date());

        commentService.addComment(comment);

        // 触发评论事件
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId", discussPostId);
        // 获取对象id
        if (comment.getEntityId() == ENTITY_TYPE_POST) {
            // 消息的内容如果是帖子，那得到的就是帖子作者的对象id
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        } else if (comment.getEntityId() == ENTITY_TYPE_COMMENT) {
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        eventProducer.fireEvent(event); // 异步处理，效率高

        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            // 触发发帖事件
            event = new Event()
                    .setTopic(TOPIC_PUBLISH)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(ENTITY_TYPE_POST)
                    .setEntityId(discussPostId);
            eventProducer.fireEvent(event);
        }

        return "redirect:/discuss/detail/" + discussPostId;
    }

}
