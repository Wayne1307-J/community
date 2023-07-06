package indi.nowcoder.community.controller;
import indi.nowcoder.community.entity.DiscussPost;
import indi.nowcoder.community.entity.Page;
import indi.nowcoder.community.entity.User;
import indi.nowcoder.community.service.DiscussService;
import indi.nowcoder.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    private DiscussService discussService;
    @Autowired
    private UserService userService;
    @GetMapping("/index")
    public String getIndexPage(Model model, Page page){
        // 方法调用之前，会SpringMVC会自动实例化Model和Page并将Page注入给Model
        // 所以在thymeleaf 中可以直接访问Page对象中的数据
        page.setRows(discussService.findDiscussPostRows(0));
        page.setPath("/index");
        List<DiscussPost> lists=discussService.findDiscussPosts(0, page.getOffset(), page.getLimit());
        List<Map<String, Object>> discussposts = new ArrayList<>();
        if(lists!=null){
            for(DiscussPost list:lists){
                Map<String, Object> map=new HashMap<>();
                map.put("post", list);
                User user = userService.findUserById(list.getUserId());
                map.put("user",user);
                discussposts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussposts);
        return "/index";
    }
}

