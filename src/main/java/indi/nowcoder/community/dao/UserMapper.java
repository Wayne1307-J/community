package indi.nowcoder.community.dao;

import indi.nowcoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    /**
     * 根据id查询
     * @param id
     * @return 返回查询到的用户
     */
    User selectById(int id);

    /**
     * 根据用户名查询
     * @param username
     * @return 返回查询到的用户
     */
    User selectByName(String username);

    /**
     * 根据邮箱查询用户
     * @param email
     * @return 返回查询到的用户
     */
    User selectByEmail(String email);

    /**
     * 插入用户
     * @param user
     * @return 返回插入的行数
     */
    int insertUser(User user);

    /**
     * 根据用户id更新状态
     * @param id
     * @param status
     * @return 返回更新状态
     */
    int updateStatus(int id, int status);

    /**
     * 更新路径
     * @param id
     * @param headerUrl
     * @return 返回更新状态
     */
    int updateHeader(int id, String headerUrl);

    /**
     * 更新密码
     * @param id
     * @param password
     * @return
     */
    int updatePassword(int id, String password);






}
