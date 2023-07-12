package indi.nowcoder.community.service;

import indi.nowcoder.community.entity.LoginTicket;
import indi.nowcoder.community.entity.User;

import java.util.Map;

public interface UserService {
    /**
     * 根据id查询用户
     * @param id
     * @return
     */
    User findUserById(int id);

    Map<String, Object> register(User user);

    int activation(int userId, String code);

    Map<String, Object>login(String username, String password, int expiredSeconds);

    void logout(String ticket);

    LoginTicket findLoginTicket(String ticket);

    int updateHeader(int userId, String headerUrl);


}
