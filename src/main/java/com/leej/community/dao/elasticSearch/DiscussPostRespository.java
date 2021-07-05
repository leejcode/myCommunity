package com.leej.community.dao.elasticSearch;

import com.leej.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.web.bind.annotation.ResponseBody;

@ResponseBody
public interface DiscussPostRespository extends ElasticsearchRepository<DiscussPost,Integer> {

}
