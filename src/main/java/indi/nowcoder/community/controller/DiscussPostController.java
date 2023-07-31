package indi.nowcoder.community.controller;

import indi.nowcoder.community.entity.*;
import indi.nowcoder.community.event.EventProducer;
import indi.nowcoder.community.service.CommentService;
import indi.nowcoder.community.service.DiscussPostService;
import indi.nowcoder.community.service.LikeService;
import indi.nowcoder.community.service.UserService;
import indi.nowcoder.community.util.CommunityConstant;
import indi.nowcoder.community.util.CommunityUtils;
import indi.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LikeService likeService;

    @Autowired
    private EventProducer eventProducer;



    /**
     * 发布帖子
     * @param title
     * @param content
     * @return
     */
    @PostMapping("/add")
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser(); // 登录验证
        if (user == null) {   // 403表示没有权限
            return CommunityUtils.getJSONString(403, "你还没有登录哦!");
        }
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        // 触发发帖事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(post.getId());
        eventProducer.fireEvent(event);

        // 报错的情况,将来统一处理.
        return CommunityUtils.getJSONString(0, "发布成功!");
    }


    /**
     * 显示评论
     * @param discussPostId
     * @param model
     * @param page
     * @return
     */
    @GetMapping("/detail/{discussPostId}")
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {
        // 根据discussPostId获取当前帖子的内容
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post", post);
        // 获取当前帖子的作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);
        // 点赞
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeCount",likeCount);
        // 点赞数量
        int likeStatus =hostHolder.getUser()==null?0:likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_POST,discussPostId);
        model.addAttribute("likeStatus",likeStatus);
        // 评论分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount());

        // 评论: 给帖子的评论 类型：ENTITY_TYPE_POST
        // 回复: 给评论的评论 类型：ENTITY_TYPE_COMMENT
        // 查询当前页的评论列表
        List<Comment> commentList = commentService.findCommentsByEntity( // 分页查询
                ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> commentVoList = new ArrayList<>();  // // 存储每条评论及其回复的列表
        if (commentList != null) {
            // 对于每一条评论进行如下操作
            for (Comment comment : commentList) {
                Map<String, Object> commentVo = new HashMap<>(); // 存储（评论/回复，作者）的键值对
                commentVo.put("comment", comment); // 当前评论
                commentVo.put("user", userService.findUserById(comment.getUserId())); // 当前评论的作者
                // 点赞
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeCount",likeCount);
                // 点赞数量
                likeStatus =hostHolder.getUser()==null?0:likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("likeStatus",likeStatus);
                // 查询当前评论的回复列表
                List<Comment> replyList = commentService.findCommentsByEntity(
                        ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                List<Map<String, Object>> replyVoList = new ArrayList<>(); // 存储当前评论下的所有回复的列表
                if (replyList != null) {
                    // 对于每一条回复的操作
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>(); // 存储（回复，作者）的键值对
                        replyVo.put("reply", reply); // 回复
                        replyVo.put("user", userService.findUserById(reply.getUserId())); // 作者
                        // 点赞
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeCount",likeCount);
                        // 点赞数量
                        likeStatus =hostHolder.getUser()==null?0:likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_COMMENT,reply.getId());
                        replyVo.put("likeStatus",likeStatus);

                        // 回复目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);
                        replyVoList.add(replyVo); // 将当前回复放到回复列表
                    }
                }
                commentVo.put("replys", replyVoList);
                // 回复数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);
                commentVoList.add(commentVo); // 把每条评论和其回复放到列表
            }
        }
        model.addAttribute("comments", commentVoList);
        return "/site/discuss-detail";
    }

}
