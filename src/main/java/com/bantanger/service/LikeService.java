package com.bantanger.service;

import com.bantanger.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Description
 * @Date 2022/8/22 10:38
 */

@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 点赞功能，set存储用户id
     * @param userId 操作用户 id
     * @param entityType 实体类的类型
     * @param entityId 实体类的id
     * @param entityUserId 实体类的作者id
     */
    public void like(int userId, int entityType, int entityId, int entityUserId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
                /*System.out.println(entityLikeKey);
                System.out.println(userLikeKey);*/

                boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);

                operations.multi();
                if (isMember) { // 如果redis中key里存在value，那么用户执行的是取消点赞效果，remove 掉 value 值
                    redisTemplate.opsForSet().remove(entityLikeKey, userId);
                    redisTemplate.opsForValue().decrement(userLikeKey);
                } else { // 否则， 用户执行的是点赞功能，add value
                    redisTemplate.opsForSet().add(entityLikeKey, userId);
                    redisTemplate.opsForValue().increment(userLikeKey);
                }

                return operations.exec();
            }
        });
    }

    /**
     * 查询操作实体点赞的数量
     * @param entityType 实体类的类型
     * @param entityId 实体类的id
     * @return 某实体点赞的数量
     */
    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    /**
     * 查询操作用户对操作实体的点赞状态
     * @param userId 操作用户id
     * @param entityType 实体类的类型
     * @param entityId 实体类的id
     * @return 操作用户对操作实体的点赞状态：0.未点 1.已赞
     */
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
    }

    /**
     * 查询某个用户获得的所有赞
     * @param userId 操作用户id
     * @return 某个用户获得的所有赞
     */
    public int findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count =(Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count.intValue();
    }


}
