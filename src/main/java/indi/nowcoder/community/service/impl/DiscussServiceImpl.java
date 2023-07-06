package indi.nowcoder.community.service.impl;

import indi.nowcoder.community.dao.DiscussPostMapper;
import indi.nowcoder.community.entity.DiscussPost;
import indi.nowcoder.community.service.DiscussService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussServiceImpl implements DiscussService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    /**
     * 查询页面数据，分页显示
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    @Override
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit) {
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }

    /**
     * 查询指定userId的条目数量
     * @param userId
     * @return
     */
    @Override
    public int findDiscussPostRows(int userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }

}
