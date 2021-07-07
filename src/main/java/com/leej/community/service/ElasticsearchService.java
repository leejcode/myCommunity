package com.leej.community.service;

import com.leej.community.dao.DiscussPostMapper;
import com.leej.community.dao.elasticSearch.DiscussPostRespository;
import com.leej.community.entity.DiscussPost;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ElasticsearchService {
    @Autowired
    private DiscussPostRespository discussPostRespository;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    public void saveDiscussPost(DiscussPost post){
        System.out.println(post);
        discussPostRespository.save(post);
    }
    public void deleteDiscussPost(int id){
        discussPostRespository.deleteById(id);
    }
    public Page<DiscussPost> searchDiscussPost(String keyword,int current,int limit){
        SearchQuery searchQuery=new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword,"title","content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(current,limit))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();

        return elasticsearchTemplate.queryForPage(searchQuery, DiscussPost.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                SearchHits hits = searchResponse.getHits();
                if(hits.getTotalHits()<=0){
                    return null;
                }
                List<DiscussPost> list = new ArrayList<>();
                for(SearchHit h:hits){
                    DiscussPost post = new DiscussPost();
                    String id =h.getSourceAsMap().get("id").toString();
                    post.setId(Integer.valueOf(id));
                    String userId =h.getSourceAsMap().get("userId").toString();
                    post.setUserId(Integer.valueOf(userId));
                    String title =h.getSourceAsMap().get("title").toString();
                    post.setTitle(title);
                    String content =h.getSourceAsMap().get("content").toString();
                    post.setTitle(content);
                    String status =h.getSourceAsMap().get("status").toString();
                    post.setStatus(Integer.valueOf(status));
                    String createTime =h.getSourceAsMap().get("createTime").toString();
                    post.setCreateTime(new Date(Long.valueOf(createTime)));
                    String commentCount =h.getSourceAsMap().get("commentCount").toString();
                    post.setCommentCount(Integer.valueOf(commentCount));
                    //处理高亮显示结果
                    HighlightField title1 = h.getHighlightFields().get("title");
                    if (title1!=null){
                        post.setTitle(title1.getFragments()[0].toString());
                    }
                    HighlightField title2 = h.getHighlightFields().get("content");
                    if (title2!=null){
                        post.setContent(title2.getFragments()[0].toString());
                    }
                    list.add(post);
                }

                return new AggregatedPageImpl(list,pageable,
                        hits.getTotalHits(),searchResponse.getAggregations(),searchResponse.getScrollId(),hits.getMaxScore());
            }

            @Override
            public <T> T mapSearchHit(SearchHit searchHit, Class<T> aClass) {
                return null;
            }
        });
    }
}
