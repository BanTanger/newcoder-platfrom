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
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    /*这里使用@Param是因为方法有单个参数并且是拼接SQL，所以必须要用，不然会报错*/
    int selectDiscussPostRows(@Param("userId") int userId);
}
