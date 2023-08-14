package indi.nowcoder.community.service;

import indi.nowcoder.community.entity.DiscussPost;

import java.util.List;

public interface DiscussPostService {
    /**
     * 查询页面数据，分页显示
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<DiscussPost> findDiscussPosts(int userId, int offset, int limit, int orderMode);

    /**
     * 查询指定userId的条目数量
     * @param userId
     * @return
     */
    int findDiscussPostRows(int userId);

    /**
     * 添加评论
     * @param post
     * @return
     */
    int addDiscussPost(DiscussPost post);

    /**
     * 根据id查询帖子
     * @param id
     * @return
     */
    DiscussPost findDiscussPostById(int id);

    /**
     * 更新评论数量
     * @param id
     * @param commentCount
     * @return
     */
    int updateCommentCount(int id, int commentCount);

    /**
     * 更新类型
     * @param id
     * @param type
     * @return
     */
    int updateType(int id, int type);

    /**
     * 更新状态
     * @param id
     * @param status
     * @return
     */
    int updateStatus(int id, int status);

    int updateScore(int id, double score);

}
