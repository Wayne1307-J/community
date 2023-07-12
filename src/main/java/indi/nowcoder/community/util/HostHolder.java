package indi.nowcoder.community.util;

import indi.nowcoder.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * 容器对象，用于持有对象，代替session对象
 */
@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }
}
