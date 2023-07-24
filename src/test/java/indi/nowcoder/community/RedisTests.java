package indi.nowcoder.community;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTests {
    @Autowired
    private RedisTemplate redisTemplate;

//    /**
//     * 访问String类型
//     */
//    @Test
//    public void testStrings() {
//        String redisKey = "test:count";
//        redisTemplate.opsForValue().set(redisKey, 1);
//        System.out.println(redisTemplate.opsForValue().get(redisKey));
//        System.out.println(redisTemplate.opsForValue().increment(redisKey));
//        System.out.println(redisTemplate.opsForValue().decrement(redisKey));
//    }
//
//    /**
//     * 访问Hash类型
//     */
//    @Test
//    public void testHashes() {
//        String redisKey = "test:user";
//        redisTemplate.opsForHash().put(redisKey, "id", 1);
//        redisTemplate.opsForHash().put(redisKey, "username", "zhangsan");
//        System.out.println(redisTemplate.opsForHash().get(redisKey, "id"));
//        System.out.println(redisTemplate.opsForHash().get(redisKey, "username"));
//    }
//
//
//    /**
//     * 测试List
//     */
//    @Test
//    public void testLists() {
//        String redisKey = "test:ids";
//        redisTemplate.opsForList().leftPush(redisKey, 101);
//        redisTemplate.opsForList().leftPush(redisKey, 102);
//        redisTemplate.opsForList().leftPush(redisKey, 103);
//        System.out.println(redisTemplate.opsForList().size(redisKey));
//        System.out.println(redisTemplate.opsForList().index(redisKey, 0));
//        System.out.println(redisTemplate.opsForList().range(redisKey, 0, 2));
//        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
//        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
//        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
//    }
//
//    /**
//     * 测试set
//     */
//    @Test
//    public void testSets() {
//        String redisKey = "test:teachers";
//        redisTemplate.opsForSet().add(redisKey, "刘备", "关羽", "张飞", "赵云", "诸葛亮");
//        System.out.println(redisTemplate.opsForSet().size(redisKey));
//        System.out.println(redisTemplate.opsForSet().pop(redisKey));
//        System.out.println(redisTemplate.opsForSet().members(redisKey));
//    }

    /**
     * 测试有序集合
     */
    @Test
    public void testSortedSets() {
        String redisKey = "test:students";
        redisTemplate.opsForZSet().add(redisKey, "唐僧", 80);
        redisTemplate.opsForZSet().add(redisKey, "悟空", 90);
        redisTemplate.opsForZSet().add(redisKey, "八戒", 50);
        redisTemplate.opsForZSet().add(redisKey, "沙僧", 70);
        redisTemplate.opsForZSet().add(redisKey, "白龙马", 60);
        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));
        System.out.println(redisTemplate.opsForZSet().score(redisKey, "八戒"));
        System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey, "八戒"));
        System.out.println(redisTemplate.opsForZSet().reverseRange(redisKey, 0, 2));
    }
//
//    /**
//     * key的操作
//     */
//    @Test
//    public void testKeys() {
//        redisTemplate.delete("test:user");
//        System.out.println(redisTemplate.hasKey("test:user"));
//        redisTemplate.expire("test:students", 10, TimeUnit.SECONDS);
//    }
//    // 批量发送命令,节约网络开销.
//    @Test
//    public void testBoundOperations() {
//        String redisKey = "test:count";
//        // BoundHashOperations,BoundListOperations等...
//        //提前绑定，这样不需要每次都传入key
//        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);
//        operations.increment();
//        operations.increment();
//        operations.increment();
//        operations.increment();
//        operations.increment();
//        System.out.println(operations.get());
//    }

    // 编程式事务
    @Test
    public void testTransaction() {
        Object result = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String redisKey = "text:tx";

                // 启用事务
                redisOperations.multi();
                // 相关处理
                redisOperations.opsForSet().add(redisKey, "zhangsan");
                redisOperations.opsForSet().add(redisKey, "lisi");
                redisOperations.opsForSet().add(redisKey, "wangwu");
                // 事务期间查询，无效
                System.out.println(redisOperations.opsForSet().members(redisKey)); //[]

                // 提交事务
                return redisOperations.exec();
            }
        });
        // 事务提交后查询，有效
        System.out.println(result); //[1, 1, 1, [wangwu, zhangsan, lisi]]
    }

}
