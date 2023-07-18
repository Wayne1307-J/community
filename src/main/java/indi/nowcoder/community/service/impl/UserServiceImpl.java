package indi.nowcoder.community.service.impl;

import indi.nowcoder.community.dao.LoginTicketMapper;
import indi.nowcoder.community.dao.UserMapper;
import indi.nowcoder.community.entity.LoginTicket;
import indi.nowcoder.community.entity.User;
import indi.nowcoder.community.service.UserService;
import indi.nowcoder.community.util.CommunityConstant;
import indi.nowcoder.community.util.CommunityUtils;
import indi.nowcoder.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService, CommunityConstant {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
    @Value("${community.path.domain}")
    public String domain;
    @Value("${server.servlet.context-path}")
    public String contextPath ;
    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Override
    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

    /**
     * 注册
     * @param user
     * @return
     */
    @Override
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();
        // 空值处理
        if(user==null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空");
            return map;
        }
        // 验证账号
        User u = userMapper.selectByName(user.getUsername());
        if(u!=null){
            map.put("usernameMsg","该账号已存在");
            return map;
        }
        // 验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if(u!=null){
            map.put("emailMsg","该邮箱已被注册");
            return map;
        }
        // 注册用户
        user.setSalt(CommunityUtils.generateUUID().substring(0,5)); //生成随机字符串
        user.setPassword(CommunityUtils.md5(user.getPassword()+user.getSalt())); // 密码加密
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtils.generateUUID());
        user.setHeaderUrl(String.format("https://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user); // 将新用户信息插入到表中
        // 激活邮件
        Context context=new Context();
        context.setVariable("email", user.getEmail());
        // http://localhost:8080/community/activation/101/code
        String url=domain+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode(); // 拼接激活地址
        context.setVariable("url",url);
        String content = templateEngine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(),"激活账号", content); // 发送邮件
        return map;
    }

    /**
     * 激活信息
     * @param userId
     * @param code
     * @return
     */
    @Override
    public int activation(int userId, String code){
        User user = userMapper.selectById(userId);
        if(user.getStatus()==1){
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAILURE;
        }
    }

    /**
     * 登录操作
     * @param username
     * @param password
     * @return
     */
    @Override
    public Map<String, Object>login(String username, String password, int expiredSeconds){
        Map<String, Object> map= new HashMap<>();
        // 空值处理
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg", "账号不能为空");
            return map;
        }
        // 验证账号是否合法
        User user = userMapper.selectByName(username);
        if(user==null){
            map.put("usernameMsg", "该账号不存在");
            return map;
        }
        //判断账号状态
        if(user.getStatus()==0){
            map.put("usernameMsg", "该账号未激活");
            return map;
        }
        //验证密码
        password=CommunityUtils.md5(password+user.getSalt());
        if(!user.getPassword().equals(password)){
            map.put("passwordMsg", "密码错误");
            return map;
        }
        // 生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtils.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expiredSeconds * 1000));
        loginTicketMapper.insertLoginTicket(loginTicket);
        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    /**
     * 退出
     * @param ticket
     */
    @Override
    public void logout(String ticket) {
        loginTicketMapper.updateStatus(ticket,1);
    }

    @Override
    public LoginTicket findLoginTicket(String ticket) {
        return loginTicketMapper.selectByTicket(ticket);
    }

    @Override
    public int updateHeader(int userId, String headerUrl){
        return userMapper.updateHeader(userId,headerUrl);
    }

    @Override
    public User findUserByName(String username) {
        return userMapper.selectByName(username);
    }


}
