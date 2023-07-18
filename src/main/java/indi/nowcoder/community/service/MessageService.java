package indi.nowcoder.community.service;

import indi.nowcoder.community.entity.Message;

import java.util.List;

public interface MessageService {
    List<Message> findConversations(int userId, int offset, int limit);

    int findConversationCount(int userId);

    List<Message> findLetters(String conversationId, int offset, int limit);

    int findLetterCount(String conversationId);

    int findLetterUnreadCount(int userId, String conversationId);

    int addMessage(Message message);

    int readMessage(List<Integer> ids);

}
