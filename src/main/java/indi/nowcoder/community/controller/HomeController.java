package indi.nowcoder.community.controller;
import indi.nowcoder.community.entity.DiscussPost;
import indi.nowcoder.community.entity.Page;
import indi.nowcoder.community.entity.User;
import indi.nowcoder.community.service.DiscussPostService;
import indi.nowcoder.community.service.LikeService;
import indi.nowcoder.community.service.UserService;
import indi.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Controller
public class HomeController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @GetMapping("/index")
    public String getIndexPage(Model model, Page page, @RequestParam(name="orderMode", defaultValue = "0") int orderMode){
        // 方法调用之前，会SpringMVC会自动实例化Model和Page并将Page注入给Model
        // 所以在thymeleaf 中可以直接访问Page对象中的数据
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index?orderMode=" + orderMode);
        List<DiscussPost> lists= discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit(), orderMode);
        List<Map<String, Object>> discussposts = new ArrayList<>();
        if(lists!=null){
            for(DiscussPost list:lists){
                Map<String, Object> map=new HashMap<>();
                map.put("post", list);
                User user = userService.findUserById(list.getUserId());
                map.put("user",user);
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,list.getId());
                map.put("likeCount", likeCount);
                discussposts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussposts);
        model.addAttribute("orderMode", orderMode);
        return "/index";
    }

    @GetMapping("/error")
    public String getErrorPage() {
        return "/error/500";
    }

}