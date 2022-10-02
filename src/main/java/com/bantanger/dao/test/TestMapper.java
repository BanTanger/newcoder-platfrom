package com.bantanger.dao.test;

import com.bantanger.entity.test.TTest;

import java.util.List;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Description
 * @Date 2022/9/21 15:18
 */

public interface TestMapper {
    List<TTest> selectByUnique(int id);
}
