package indi.nowcoder.community.dao;

import indi.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    // 使用@Param()取别名，如果方法只有一个参数，并且在动态标签<if>中使用，就必须加别名，多个参数不需要
    int selectDiscussPostRows(@Param("userId") int userId);


}
