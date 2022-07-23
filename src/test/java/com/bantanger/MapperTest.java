package com.bantanger;

import com.bantanger.dao.DiscussPostMapper;
import com.bantanger.dao.LoginTicketMapper;
import com.bantanger.dao.UserMapper;
import com.bantanger.entity.DiscussPost;
import com.bantanger.entity.LoginTicket;
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
}
