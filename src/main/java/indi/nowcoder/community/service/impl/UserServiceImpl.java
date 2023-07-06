package indi.nowcoder.community.service.impl;

import indi.nowcoder.community.dao.UserMapper;
import indi.nowcoder.community.entity.User;
import indi.nowcoder.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public User findUserById(int id) {
        return userMapper.selectById(id);
    }
}
