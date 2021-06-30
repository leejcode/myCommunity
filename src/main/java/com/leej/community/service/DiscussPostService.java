package com.leej.community.service;

import com.leej.community.dao.DiscussPostMapper;
import com.leej.community.entity.DiscussPost;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    public List<DiscussPost> findDiscusssPosts(int userId, int offset, int limit){
        return discussPostMapper.selectDiscussPosts(userId,offset,limit);
    }
    public int findDiscussPostsRows(int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }
}
