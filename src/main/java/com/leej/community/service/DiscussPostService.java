package com.leej.community.service;

import com.github.benmanes.caffeine.cache.*;

import com.leej.community.dao.DiscussPostMapper;
import com.leej.community.entity.DiscussPost;
import com.leej.community.utils.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Value("${caffeine.posts.max-size}")
    private int maxSize;
    @Value("${caffeine.posts.expire-seconds}")
    private int expireSeconds;
    // Caffeine核心接口: Cache, LoadingCache, AsyncLoadingCache

    // 帖子列表缓存
    private LoadingCache<String, List<DiscussPost>> postListCache;

    // 帖子总数缓存
    private LoadingCache<Integer, Integer> postRowsCache;
    @PostConstruct
    public void init() {
        // 初始化帖子列表缓存
        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Nullable
                    @Override
                    public List<DiscussPost> load(@NonNull String key) throws Exception {
                        if (key == null || key.length() == 0) {
                            throw new IllegalArgumentException("参数错误!");
                        }

                        String[] params = key.split(":");
                        if (params == null || params.length != 2) {
                            throw new IllegalArgumentException("参数错误!");
                        }

                        int offset = Integer.valueOf(params[0]);
                        int limit = Integer.valueOf(params[1]);
                        // 二级缓存: Redis -> mysql
                        return discussPostMapper.selectDiscussPosts(0, offset, limit, 1);
                    }
                });
        // 初始化帖子总数缓存
        postRowsCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Nullable
                    @Override
                    public Integer load(@NonNull Integer key) throws Exception {
                        return discussPostMapper.selectDiscussPostRows(key);
                    }
                });
    }
    public List<DiscussPost> findDiscusssPosts(int userId, int offset, int limit,int orderModer){
        if (userId == 0 && orderModer == 1) {
            return postListCache.get(offset + ":" + limit);
        }
        return discussPostMapper.selectDiscussPosts(userId,offset,limit,orderModer);
    }

    public int findDiscussPostsRows(int userId){
        if (userId == 0) {
            return postRowsCache.get(userId);
        }
        return discussPostMapper.selectDiscussPostRows(userId);
    }
    public int addDiscussPost(DiscussPost discussPost){
        if(discussPost==null){
            throw new IllegalArgumentException("参数不正确");
        }
        //转移html标记
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));
        //过滤敏感词
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));
        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        return discussPostMapper.insertDiscussPost(discussPost);
    }
    public DiscussPost findDiscussPostById(Integer id){
        return discussPostMapper.selectDiscussPostById(id);
    }
    public int updateCommentCount(int id,int commentCount){
        return discussPostMapper.updateCommentCount(id,commentCount);
    }
    public int updateType(int id,int type){
        return discussPostMapper.updateType(id,type);
    }
    public int updateStatus(int id,int status){
        return discussPostMapper.updateStatus(id,status);
    }
    public int updateScore(int id,double score){
        return discussPostMapper.updateScore(id,score);
    }
}
