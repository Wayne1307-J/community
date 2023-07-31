package indi.nowcoder.community.event;
import com.alibaba.fastjson.JSONObject;
import indi.nowcoder.community.entity.DiscussPost;
import indi.nowcoder.community.entity.Event;
import indi.nowcoder.community.entity.Message;
import indi.nowcoder.community.service.DiscussPostService;
import indi.nowcoder.community.service.ElasticsearchService;
import indi.nowcoder.community.service.MessageService;
import indi.nowcoder.community.util.CommunityConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class EventConsumer implements CommunityConstant {
    @Autowired
    private MessageService messageService;
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW})
    public void handleCommentMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            log.error("消息的内容为空!");
            return;
        }
        // 解析json对象
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            log.error("消息格式错误!");
            return;
        }
        // 发送站内通知
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        // 用户 userId 点赞/评论/回复（entityType）了你的帖子（帖子identityId)
        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());

        // 内容
        if (!event.getData().isEmpty()) {
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }
    // 消费发帖事件
    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePublishMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            log.error("消息的内容为空!");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            log.error("消息格式错误!");
            return;
        }
        // 查询帖子
        DiscussPost post = discussPostService.findDiscussPostById(event.getEntityId());
        // 存入es
        elasticsearchService.saveDiscussPost(post);
    }



}

