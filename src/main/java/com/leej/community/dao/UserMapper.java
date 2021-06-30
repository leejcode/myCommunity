package com.leej.community.dao;

import com.leej.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper//不需要再写mapper映射文件
public interface UserMapper {
    User selectById(int id);
    User selectByName(String name);
    User selectByEmail(String email);
    int insertUser(User user);
    int updateStatus(int id,int status);
    int updateHeader(int id,String headerUrl);
    int updatePassword(int id,String password);
}
