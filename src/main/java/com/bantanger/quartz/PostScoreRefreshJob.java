package com.bantanger.quartz;

import com.bantanger.entity.DiscussPost;
import com.bantanger.service.CommentService;
import com.bantanger.service.DiscussPostService;
import com.bantanger.service.ElasticsearchService;
import com.bantanger.service.LikeService;
import com.bantanger.util.CommunityConstant;
import com.bantanger.util.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Description
 * @Date 2022/9/5 20:44
 */

public class PostScoreRefreshJob implements Job, CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(PostScoreRefreshJob.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    // 牛客纪元
    private static final Date epoch;

    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-08-01 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化牛客纪元失败", e);
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String redisKey = RedisKeyUtil.getPostScoreKey();
        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);

        if (operations.size() == 0) {
            logger.info("[任务取消] 没有需要刷新的帖子！");
            return ;
        }

        logger.info("[任务开始] 正在刷新帖子分数," + operations.size() + "个任务正在执行");
        while(operations.size() > 0) {
            refresh((Integer) operations.pop());
        }
        logger.info("[任务结束] 帖子分数刷新完毕");
    }

    /**
     * 刷新帖子评分
     * 计算公式：log(精华分75 + 评论数 * 10 + 点赞数 * 2) + (发布时间 - 牛客纪元)
     * @param postId
     */
    private void refresh(int postId) {
        DiscussPost discussPost = discussPostService.findDiscussPostById(postId);

        if (discussPost == null || discussPost.getType() == 2) {
            logger.error("该帖子不存在: id = " + postId);
            return ;
        }

        // 是否精华
        boolean isWonderful = discussPost.getStatus() == 1;

        // 评论数量
        int commentCount = discussPost.getCommentCount();

        // 点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, postId);

        // 计算权重
        double w = (isWonderful ? 75 : 0) + commentCount * 2 + likeCount * 2;

        // 分数 = 帖子权重 + 距离天数
        double score = Math.log10(Math.max(w, 1))
                + (discussPost.getCreateTime().getTime() - epoch.getTime())
                / (3600 * 1000 * 24);

        // 更新帖子分数
        discussPostService.updateScore(postId, score);
        // 同步搜索数据
        discussPost.setScore(score);
        elasticsearchService.saveDiscussPost(discussPost);
    }
}
