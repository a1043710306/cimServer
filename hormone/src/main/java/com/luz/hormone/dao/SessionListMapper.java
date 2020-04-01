package com.luz.hormone.dao;

import com.luz.hormone.model.SessionList;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionListMapper {
    public List<SessionList>  getSessionListByUserId(@Param("userId")int userId);
    public void insert(SessionList sessionList);
    public void delSessionByUserIdAndFriendId(@Param("userId")int userId,@Param("friendId")int friendId);
    public void updateMessageIdByUserIdAndfriendId(@Param("userId")int userId,@Param("friendId")int friendId,
                                @Param("lastMessageId")int lastMessageId);
    public SessionList getSessionByUserIdAndFriendId(@Param("userId")int userId,@Param("friendId")int friendId);
}
