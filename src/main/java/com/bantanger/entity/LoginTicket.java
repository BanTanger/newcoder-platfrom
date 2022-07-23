package com.bantanger.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Date 2022/7/23 20:34
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LoginTicket {
    private int id;
    private int userId;
    private String ticket;
    private int status;
    private Date expired;
}
