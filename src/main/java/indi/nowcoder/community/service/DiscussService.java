package indi.nowcoder.community.service;

import indi.nowcoder.community.entity.DiscussPost;

import java.util.List;

public interface DiscussService {
    /**
     * 查询页面数据，分页显示
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<DiscussPost> findDiscussPosts(int userId, int offset, int limit);

    /**
     * 查询指定userId的条目数量
     * @param userId
     * @return
     */
    int findDiscussPostRows(int userId);
}
