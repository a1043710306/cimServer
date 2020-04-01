package com.luz.hormone.dao;

import com.luz.hormone.model.MessageListModel;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MessageListMapper {
    public List<MessageListModel> getHistory(@Param("userId") int userId, @Param("firendId")int firendId,
                                             @Param("startTime") int startTime, @Param("endTime") int endTime);

    public List<MessageListModel> getHistoryByUserIdAndCircleId(@Param("circleId")int circleId,
                                                                @Param("startTime") int startTime, @Param("endTime") int endTime);

    public void insert(MessageListModel messageListModel);
    public List<MessageListModel> getHistoryNotByTime(@Param("userId") int userId, @Param("firendId")int firendId);
}
