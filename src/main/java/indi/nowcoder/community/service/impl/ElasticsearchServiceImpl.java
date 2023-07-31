package indi.nowcoder.community.service.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import indi.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
import indi.nowcoder.community.entity.DiscussPost;
import indi.nowcoder.community.service.ElasticsearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class ElasticsearchServiceImpl implements ElasticsearchService {
    @Autowired
    private DiscussPostRepository discussRepository;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    // 将帖子保存至Elasticsearch服务器
    @Override
    public void saveDiscussPost(DiscussPost post) {
        discussRepository.save(post);
    }

    // 从Elasticsearch服务器删除帖子。
    @Override
    public void deleteDiscussPost(int id) {
        discussRepository.deleteById(id);
    }


    // 从Elasticsearch服务器搜索帖子
    @Override
    public SearchPage<DiscussPost> searchDiscussPost(String keyword, int current, int limit){
        HighlightField titleHighlightField = new HighlightField("title");
        HighlightField contentHighlightField = new HighlightField("content");
        Highlight titleHighlight = new Highlight(List.of(titleHighlightField,contentHighlightField));
        // 查询条件
        NativeQuery searchQuery =new NativeQueryBuilder()
                // 查询这里直接使用了lambda表达式，不用写实现类
                .withQuery(Query.of(q -> q.multiMatch(mq -> mq.query(keyword).fields("title","content"))))
                // 倒序直接用Sort.by后面加descending()就行
                .withSort(Sort.by("type","score","createTime").descending())
                .withPageable(PageRequest.of(0, 10)) // 分页
                .withHighlightQuery( // 高亮显示，添加<em></em>标签
                        new HighlightQuery(titleHighlight,DiscussPost.class)
                )
                .build();
        SearchHits<DiscussPost> search = elasticsearchTemplate.search(searchQuery, DiscussPost.class);
        SearchPage<DiscussPost> page = SearchHitSupport.searchPageFor(search, searchQuery.getPageable());
        if (!page.isEmpty()){
            List<DiscussPost> list = new ArrayList<>();
            for (SearchHit<DiscussPost> hit : page) {
                DiscussPost discussPost = hit.getContent();
                // 获取高亮部分
                List<String> title = hit.getHighlightFields().get("title");
                if (title!=null){ discussPost.setTitle(title.get(0)); }
                List<String> content = hit.getHighlightFields().get("content");
                if (content!=null){ discussPost.setContent(content.get(0)); }
                System.out.println(hit.getContent());
                list.add(discussPost);
            }
        }
        return page;
    }
}
