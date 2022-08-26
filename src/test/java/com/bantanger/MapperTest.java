package com.bantanger;

import com.bantanger.dao.*;
import com.bantanger.entity.*;
import com.bantanger.service.CommentService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

    @Autowired
    private CommentService commentService;

    @Test
    public void test() {
//        Comment comment = new Comment();
//        String s = new String();
//        System.out.println(s.equals(null));
//        System.out.println(comment == null);
        commentService.addComment(null);
    }


    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void selectConversationsTest() {
        List<Message> messages = messageMapper.selectConversations(111, 0, 5);
        messages.forEach(System.out::println);
    }

    @Test
    public void selectConversationCountTest() {
        int i = messageMapper.selectConversationCount(111);
        System.out.println(i);
    }

    @Test
    public void selectLettersTest() {
        List<Message> messages = messageMapper.selectLetters("111_112", 0, 5);
        messages.forEach(System.out::println);
    }

    @Test
    public void selectLetterCountTest() {
        int i = messageMapper.selectLetterCount("111_112");
        System.out.println(i);
    }

    @Test
    public void selectLetterUnreadCountTest() {
        int i = messageMapper.selectLetterUnreadCount(131, "111_131");
        System.out.println(i);
    }
}
