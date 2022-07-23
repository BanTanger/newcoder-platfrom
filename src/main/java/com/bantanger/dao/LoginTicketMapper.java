package com.bantanger.dao;

import com.bantanger.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Date 2022/7/23 21:24
 */

@Mapper
public interface LoginTicketMapper {
    @Insert({
        "insert into login_ticket(user_id, ticket, status, expired) ",
        "values (#{userId}, #{ticket}, #{status}, #{expired})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    @Select({
        "select id, user_id, ticket, status, expired ",
        "from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    @Update({
        "update login_ticket set status=#{status} where ticket=#{ticket}"
    })
    int updateTicket(String ticket, int status);
}
