package com.bantanger.service;

import com.bantanger.dao.CommentMapper;
import com.bantanger.dao.DiscussPostMapper;
import com.bantanger.entity.Comment;
import com.bantanger.util.CommunityConstant;
import com.bantanger.util.ObjNullUtil;
import com.bantanger.util.SensitiveFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Date 2022/7/31 19:20
 */

@Service
public class CommentService implements CommunityConstant {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentsByEntity(entityType, entityId, offset, limit);
    }

    public int findCountByEntity(int entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        if(comment == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        if (StringUtils.isBlank(comment.getContent())) {
            throw new RuntimeException("内容不能为空");
        }

        // 添加评论, 做好HTML格式转化和敏感词过滤
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int row = commentMapper.insertComment(comment);

        // 更新帖子评论数量
        if(comment.getEntityType() == ENTITY_TYPE_POST) {
            // 查询修改的帖子的所有评论数量
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostMapper.updateCommentCount(comment.getEntityId(), count);
        }

        return row;
    }

    public Comment findCommentById(int id) {
        return commentMapper.selectCommentById(id);
    }
}
