package com.leej.community.dao;

import com.leej.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    List<DiscussPost> selectDiscussPosts(int userId,int offset,int limit);//分页显示
    //param注解用于参数取别名，如果只有一个参数，并且在if中使用则必须加别名
    int selectDiscussPostRows(@Param("userId") int userId);
    int insertDiscussPost(DiscussPost discussPost);
    DiscussPost selectDiscussPostById(Integer id);
    int updateCommentCount(Integer id,int commentCount);
    int updateType(int id,int type);
    int updateStatus(int id,int status);
}
