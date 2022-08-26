package com.bantanger.dao;

import com.bantanger.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Description
 * @Date 2022/8/20 13:02
 */

@Mapper
public interface MessageMapper {

    /* 查询当前用户的所有对话列表，针对每个会话只返回一条最新的私信 */
    List<Message> selectConversations(int userId, int offset, int limit); // 加上分页参数

    /* 查询当前用户的会话数量 */
    int selectConversationCount(int userId);

    /* 查询某个对话所包含的私信列表 */
    List<Message> selectLetters(String conversationId, int offset, int limit);

    /* 查询某个会话所包含的私信数量 count函数*/
    int selectLetterCount(String conversationId);

    /* 查询未读私信数量 status
    *  注意这里的 ‘conversationId’ 是动态拼接的。
    *  如果前端传入，那就查询该会话的未读数量
    *  如果不传入，那就动态查询用户所有未读数量
    * */
    int selectLetterUnreadCount(int userId, String conversationId);

    /* 增加信息 */
    int insertMessage(Message message);

    /* 修改消息状态，涉及到多个消息变为已读，所以要传入消息id集合 */
    int updateStatus(List<Integer> ids, int status);

    /* 查询某个主题下的最新通知 */
    Message selectLatestNotice(int userId, String topic);

    /* 查询某个主题所包含的通知数量 */
    int selectNoticeCount(int userId, String topic);

    /* 查询未读的通知的数量 */
    int selectNoticeUnreadCount(int userId, String topic);

    /* 查询某个主题所包含的通知列表 */
    List<Message> selectNotices(int userId, String topic, int offset, int limit);

}
