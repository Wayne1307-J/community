package indi.nowcoder.community.service.impl;
import indi.nowcoder.community.dao.CommentMapper;
import indi.nowcoder.community.entity.Comment;
import indi.nowcoder.community.service.CommentService;
import indi.nowcoder.community.service.DiscussPostService;
import indi.nowcoder.community.util.CommunityConstant;
import indi.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
@Service
public class CommentServiceImpl implements CommentService, CommunityConstant{
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Autowired
    private DiscussPostService discussPostService;
    @Override
    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentsByEntity(entityType, entityId, offset, limit);
    }
    @Override
    public int findCommentCount(int entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    @Override
    public int addComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        // 添加评论
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent())); // 过滤标签
        comment.setContent(sensitiveFilter.filter(comment.getContent())); // 过滤敏感词
        int rows = commentMapper.insertComment(comment);  //
        // 更新帖子评论数量
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(), count);
        }
        return rows;
    }

    @Override
    public Comment findCommentById(int id){
        return commentMapper.selectCommentById(id);
    }
}
