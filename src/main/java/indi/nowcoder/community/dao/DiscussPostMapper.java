package indi.nowcoder.community.dao;

import indi.nowcoder.community.entity.Comment;
import indi.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    /**
     * 根据id查询数据
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    // 使用@Param()取别名，如果方法只有一个参数，并且在动态标签<if>中使用，就必须加别名，多个参数不需要
    // 统计数据数目
    int selectDiscussPostRows(@Param("userId") int userId);
    // 插入数据
    int insertDiscussPost(DiscussPost discussPost);
    // 根据id查询帖子
    DiscussPost selectDiscussPostById(int id);
    // 更新评论数量
    int updateCommentCount(int id, int commentCount);
}
