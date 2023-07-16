package indi.nowcoder.community.controller;

import indi.nowcoder.community.entity.DiscussPost;
import indi.nowcoder.community.entity.User;
import indi.nowcoder.community.service.DiscussPostService;
import indi.nowcoder.community.service.UserService;
import indi.nowcoder.community.util.CommunityUtils;
import indi.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
@Controller
@RequestMapping("/discuss")
public class DiscussPostController{
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
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
        // 报错的情况,将来统一处理.
        return CommunityUtils.getJSONString(0, "发布成功!");
    }

    @GetMapping("/detail/{discussPostId}")
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model){
        // 帖子
        DiscussPost post = discussPostService.findDisCussPostById(discussPostId);
        model.addAttribute("post",post);
        // 查询作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);
        return "/site/discuss-detail";
    }
}
