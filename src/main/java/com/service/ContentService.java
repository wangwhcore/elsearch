package com.service;

import com.Util.Jsousn;
import com.alibaba.fastjson.JSON;
import com.pojo.Content;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.directory.SearchResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ContentService {
    @Autowired
    private Jsousn jsousn;
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    //增加数据
    public Boolean ParesHtml(String key) throws IOException {
        List<Content> contents = jsousn.contentList(key);
        System.out.println(contents.toString());
        BulkRequest bulkRequest = new BulkRequest();
        for (Content content : contents) {
            bulkRequest.add(
                    new IndexRequest("goods").source(JSON.toJSONString(content), XContentType.JSON)
            );
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulk.hasFailures();
    }
    //查宿数据
    public List<Map<String, Object>> searchPage(String key, int start, int size) throws IOException {
        SearchRequest searchRequest = new SearchRequest(key);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.from(start);
        builder.size(size);
        TermQueryBuilder queryBuilder = QueryBuilders.termQuery("title", key);
        builder.query(queryBuilder);
        searchRequest.source(builder);
        ArrayList<Map<String, Object>> maps = new ArrayList<>();
        try {
            SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            for (SearchHit hit : search.getHits()) {
                maps.add(hit.getSourceAsMap());
            }
            return maps;
        }catch (Exception e){
            if (maps.size()==0){
                ParesHtml(key);
                return maps;
            }else {return maps;}
        }finally {
            return maps;
        }
    }
}
