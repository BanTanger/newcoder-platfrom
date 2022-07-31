package com.bantanger;

import com.bantanger.dao.CommentMapper;
import com.bantanger.dao.DiscussPostMapper;
import com.bantanger.dao.LoginTicketMapper;
import com.bantanger.dao.UserMapper;
import com.bantanger.entity.*;
import com.bantanger.service.DiscussPostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Date 2022/6/23 15:14
 */

@SpringBootTest
public class MapperTest {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Test
    public void UserMapperTest() {
        System.out.println(userMapper.selectById(1));
    }


    @Test
    public void DiscussPostMapperTest() {
//        System.out.println(discussPostMapper.selectDiscussPostRows(0));
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(0, 0, 10);
        for (DiscussPost discussPost : discussPosts) {
            System.out.println(discussPost);
        }
    }

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Test
    public void insertTest() {
        LoginTicket loginTicket = new LoginTicket();
         loginTicket.setUserId(1);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));
        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void selectCommentTest() {
        // 帖子
        DiscussPost post = discussPostMapper.selectDiscussPostById(275);
        // 作者
        User user = userMapper.selectById(post.getUserId());

        // 评论分页信息
        Page page = new Page();
        page.setLimit(5);
        page.setPath("/discuss/detail/" + 1);
        page.setRows(post.getCommentCount());

        // 评论：给帖子的评论
        // 回复：给评论的评论
        // 评论列表
        List<Comment> commentList = commentMapper.selectCommentsByEntity(
                1, post.getId(), page.getOffset(), page.getLimit());

        for (Comment comment : commentList) {
            System.out.println(comment);
        }
    }
}
