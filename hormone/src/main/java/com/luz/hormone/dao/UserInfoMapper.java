package com.luz.hormone.dao;

import com.luz.hormone.model.UserModel;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UserInfoMapper {
    public UserModel getUserInfoByUserId(@Param("userId") int userId);
    public Set<Integer> getUsersByUserType(@Param("userType") int userType);
}
