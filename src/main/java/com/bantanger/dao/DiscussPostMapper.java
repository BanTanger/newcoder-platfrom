package com.bantanger.dao;

import com.bantanger.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Date 2022/6/21 22:39
 */

@Mapper
public interface DiscussPostMapper {

    /**
     * 查询所有帖子
     * @param userId
     * @param offset
     * @param limit
     * @param orderMode 排序模式
     *                  0 按照置顶和精华来排序
     *                  1 按照帖子评分来排序
     * @return
     */
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit, int orderMode);

    /* 这里使用@Param是因为方法有单个参数并且是拼接SQL，所以必须要用，不然会报错 */
    int selectDiscussPostRows(@Param("userId") int userId);

    /* 增加帖子的方法 */
    int insertDiscussPost(DiscussPost discussPost);

    /* 查询帖子 */
    DiscussPost selectDiscussPostById(int DiscussPostId);

    /* 更新评论数量 */
    int updateCommentCount(int id, int commentCount);

    /* 更新帖子类型 */
    int updateType(int id, int type);

    /* 更新帖子状态 */
    int updateStatus(int id, int status);

    /* 更新帖子分数 */
    int updateScore(int id, double score);

}
