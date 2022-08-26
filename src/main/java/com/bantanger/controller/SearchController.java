package com.bantanger.controller;

import com.bantanger.entity.DiscussPost;
import com.bantanger.entity.Page;
import com.bantanger.service.ElasticsearchService;
import com.bantanger.service.LikeService;
import com.bantanger.service.UserService;
import com.bantanger.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Description
 * @Date 2022/8/26 19:56
 */

@Controller
public class SearchController implements CommunityConstant {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @GetMapping("/search")
    public String search(String keyword, Page page, Model model) {
        // 搜索帖子
        org.springframework.data.domain.Page<DiscussPost> searchResult =
                elasticsearchService.searchDiscussPost(keyword,
                        page.getCurrent() - 1, page.getLimit());
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (searchResult != null) {
            for (DiscussPost discussPost : searchResult) {
                Map<String, Object> map = new HashMap<>();
                // 帖子
                map.put("post", discussPost);
                // 作者
                map.put("user", userService.findUserById(discussPost.getUserId()));
                // 点赞数量
                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPost.getId()));

                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("keyword", keyword);

        // 分页信息
        page.setPath("/search?keyword=" + keyword);
        page.setRows(searchResult != null ? (int) searchResult.getTotalElements() : 0);

        return "/site/search";
    }
}
