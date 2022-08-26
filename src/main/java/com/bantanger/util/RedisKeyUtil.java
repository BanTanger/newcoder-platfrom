package com.bantanger.util;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Description
 * @Date 2022/8/22 10:25
 */

public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user";

    /**
     * 某一个实体的赞
     *
     * 定义赞的 key值 名称
     * 数据库名称格式如下：
     * like:entity:entityType:entityId -> set(userId)
     * 将点赞用户的 id 存入到 set 集合中管理
     *
     * @param entityType 实体类类型名称
     * @param entityId   实体类id
     * @return 返回 String类型的 key 值名称
     */
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 某个用户的赞
     * @param userId 用户id
     * @return 指定用户获得的所有赞的 key 值名称
     */
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    /**
     * 某个用户关注的对象实体
     * 格式：followee:userId:entityType -> zset(entityId, now)
     * now为添加时间(作为排序依据，最新关注放在最上面)
     * @param userId 用户id
     * @param entityType 实体类型
     * @return 用户关注列表 key值名称
     */
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    /**
     * 某个实体拥有的粉丝
     * 格式：follower:entityType:entityId -> zset(userId, now)
     * now为添加时间(作为排序依据，最新关注放在最上面)
     * @param entityId 实体id
     * @param entityType 实体类型
     * @return 实体类拥有的粉丝 key值名称
     */
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 获取登陆验证码的 Key
     * 但当前用户并没有登陆，没法通过userId对没有用户设置Key值
     * 我们先传输一个随机字符绑定每一个用户(通过cookies)，
     * 等生成验证码之后再重做Key
     * @param owner 随机字符串
     * @return
     */
    public static String getKaptchaKey(String owner) {
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    /**
     * 登陆凭证
     * 代替LoginTicket表来存储数据。
     * @param ticket
     * @return
     */
    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }

    /**
     * 用户缓存
     * @param userId
     * @return
     */
    public static String getUserKey(int userId) {
        return PREFIX_USER + SPLIT + userId;
    }
}
