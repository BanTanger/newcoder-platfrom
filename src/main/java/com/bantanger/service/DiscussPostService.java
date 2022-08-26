package com.bantanger.service;

import com.bantanger.dao.CommentMapper;
import com.bantanger.dao.DiscussPostMapper;
import com.bantanger.entity.Comment;
import com.bantanger.entity.DiscussPost;
import com.bantanger.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import org.unbescape.html.HtmlEscape;

import java.util.List;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Date 2022/6/23 15:46
 */

@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<DiscussPost> findDiscussPost(int userId, int offset, int limit) {
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }

    public int findDiscussPostRows(int userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    public int addDiscussPost(DiscussPost post) {
        // 判空
        if(post == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        // 标签转义，(标题和内容) setTitle & setContent -> HtmlUtils.htmlEscape
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        // 过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return discussPostMapper.insertDiscussPost(post);
    }

    public DiscussPost findDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    public int updateCommentCount(int id, int comment) {
        return discussPostMapper.updateCommentCount(id, comment);
    }
}
