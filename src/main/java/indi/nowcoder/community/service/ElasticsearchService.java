package indi.nowcoder.community.service;

import indi.nowcoder.community.entity.DiscussPost;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.SearchPage;

public interface ElasticsearchService {
    void saveDiscussPost(DiscussPost post);
    void deleteDiscussPost(int id);
    SearchPage<DiscussPost> searchDiscussPost(String keyword, int current, int limit);
}
