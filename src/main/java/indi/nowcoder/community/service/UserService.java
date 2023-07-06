package indi.nowcoder.community.service;

import indi.nowcoder.community.entity.User;

public interface UserService {
    /**
     * 根据id查询用户
     * @param id
     * @return
     */
    User findUserById(int id);
}
