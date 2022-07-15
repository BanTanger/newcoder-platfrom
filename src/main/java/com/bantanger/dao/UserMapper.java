package com.bantanger.dao;

import com.bantanger.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Date 2022/6/23 15:49
 */

@Mapper
public interface UserMapper {
    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    int insertUser(User user);

    int updateStatus(int id, int status);

    int updateHeader(int id, String headerUrl);

    int updatePassword(int id, String password);
}
