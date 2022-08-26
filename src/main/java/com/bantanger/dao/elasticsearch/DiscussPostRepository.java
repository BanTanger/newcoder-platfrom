package com.bantanger.dao.elasticsearch;

import com.bantanger.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Description
 * @Date 2022/8/25 20:46
 */

@Repository // 针对所有数据访问层的注解，mapper是针对mysql的数据访问层注解
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {

}
