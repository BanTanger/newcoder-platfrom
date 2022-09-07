package com.bantanger.dao.elasticsearch;

import com.bantanger.NewcodeAdminApplication;
import com.bantanger.dao.DiscussPostMapper;
import com.bantanger.entity.DiscussPost;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * @author bantanger 半糖
 * @version 1.0
 * @Description
 * @Date 2022/8/25 20:56
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = NewcodeAdminApplication.class)
public class ElasticsearchTests {

    @Autowired
    private DiscussPostMapper discussMapper;

    @Autowired
    private DiscussPostRepository discussRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void testInsert() {
        discussRepository.save(discussMapper.selectDiscussPostById(241));
        discussRepository.save(discussMapper.selectDiscussPostById(242));
        discussRepository.save(discussMapper.selectDiscussPostById(243));
    }

//    @Test
//    public void testInsertList() {
//        discussRepository.saveAll(discussMapper.selectDiscussPosts(101, 0, 100));
//        discussRepository.saveAll(discussMapper.selectDiscussPosts(102, 0, 100));
//        discussRepository.saveAll(discussMapper.selectDiscussPosts(103, 0, 100));
//        discussRepository.saveAll(discussMapper.selectDiscussPosts(111, 0, 100));
//        discussRepository.saveAll(discussMapper.selectDiscussPosts(112, 0, 100));
//        discussRepository.saveAll(discussMapper.selectDiscussPosts(131, 0, 100));
//        discussRepository.saveAll(discussMapper.selectDiscussPosts(132, 0, 100));
//        discussRepository.saveAll(discussMapper.selectDiscussPosts(133, 0, 100));
//        discussRepository.saveAll(discussMapper.selectDiscussPosts(134, 0, 100));
//    }

    @Test
    public void testUpdate() {
        DiscussPost discussPost = discussMapper.selectDiscussPostById(231);
        discussPost.setContent("我是新人。使劲灌水");
        discussRepository.save(discussPost);
    }

    @Test
    public void testDelete() {
        discussRepository.delete(discussMapper.selectDiscussPostById(231));
    }

    @Test
    public void testSearchRepository() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("create_time").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").preTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").preTags("</em>")
                ).build(); // 构建

        // 注册
        Page<DiscussPost> page = discussRepository.search(searchQuery);

    }

    @Test
    public void testSearchByTemplate() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("create_time").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").preTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").preTags("</em>")
                ).build(); // 构建

        // 注册
    }

}



