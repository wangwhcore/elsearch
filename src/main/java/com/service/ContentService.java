package com.service;

import com.Util.FileParse;
import com.Util.Jsousn;
import com.alibaba.fastjson.JSON;
import com.pojo.Content;
import com.pojo.RdcComponent;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Service
public class ContentService extends BaseSeachService{
    @Autowired
    private Jsousn jsousn;
    @Autowired
    private FileParse fileParse;

    /**
     * 模糊查询 事件 表中，所有的事件
     * @param name
     * @return
     */
    public List<Map<String, Object>> findEvent(String name) {
        SearchRequest searchRequest = new SearchRequest("rdcevent");
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //匹配查询，走分词
        MatchQueryBuilder queryBuilder2 = QueryBuilders.matchQuery("content", name);
        builder.from(0);
        builder.size(100);
        builder.query(queryBuilder2);
        searchRequest.source(builder);
        ArrayList<Map<String, Object>> maps = new ArrayList<>();
        try {
            SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            for (SearchHit hit : search.getHits()) {
                maps.add(hit.getSourceAsMap());
            }
            return maps;
        } catch (Exception e) {
            if (maps.size() == 0) {
//                ParesHtml(key);
                return maps;
            } else {
                return maps;
            }
        } finally {
            return maps;
        }
    }

    /**
     * 支持高亮查询事件
     * @param name
     * @return
     */
    public List<Map<String, Object>> findEventWithHighLight(String name) {
        SearchRequest searchRequest = new SearchRequest("rdcevent");
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //匹配查询，走分词
        MatchQueryBuilder queryBuilder2 = QueryBuilders.matchQuery("content", name);
        builder.from(0);
        builder.size(100);
        builder.query(queryBuilder2);
        searchRequest.source(builder);
        ArrayList<Map<String, Object>> maps = new ArrayList<>();
        try {
            SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            SearchHits hits = search.getHits();
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                Map<String, Object> highlights = new HashMap<String, Object>();
                Map<String, Object> map = hit.getSourceAsMap();
                // 获取高亮结果
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                for (Map.Entry<String, HighlightField> entry : highlightFields.entrySet()) {
                    String mapKey = entry.getKey();
                    HighlightField mapValue = entry.getValue();
                    Text[] fragments = mapValue.fragments();
                    String fragmentString = fragments[0].string();
                    highlights.put(mapKey, fragmentString);
                }
                map.put("highlight", highlights);
                maps.add(map);
            }

            return maps;
        } catch (Exception e) {
            if (maps.size() == 0) {
//                ParesHtml(key);
                return maps;
            } else {
                return maps;
            }
        } finally {
            return maps;
        }
    }

    /**
     * 查询组件示例
     * @param name
     * @param comptype
     * @return
     */
    public List<Map<String, Object>> findComps(String name, String comptype) {
        SearchRequest searchRequest = new SearchRequest("rdccomponents");
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //匹配查询，走分词
        BoolQueryBuilder queryBuilder2 = QueryBuilders.boolQuery();
        queryBuilder2.must(QueryBuilders.matchQuery("enname",comptype));
        queryBuilder2.should(QueryBuilders.matchQuery("content",name));
        builder.from(0);
        builder.size(100);
        builder.query(queryBuilder2);
        searchRequest.source(builder);
        ArrayList<Map<String, Object>> maps = new ArrayList<>();
        try {
            SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            for (SearchHit hit : search.getHits()) {
                maps.add(hit.getSourceAsMap());
            }
            return maps;
        } catch (Exception e) {
            if (maps.size() == 0) {
//                ParesHtml(key);
                return maps;
            } else {
                return maps;
            }
        } finally {
            return maps;
        }
    }

    /**
     * es中使用 id作为主键，这里试用与首次全量初始化（会覆盖相同ID内容）
     * 如需追加，需要另外写方法先获取 count后，再绑定 i（id主键）   get /rdccomps/_count
     */
    static int i = 1;
    public void addComponents(String fileName) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(ResourceUtils.getFile("classpath:rdccomps/"+fileName)));
        String s;
        List<Map<String, Object>> docs = new ArrayList<>();
        StringBuffer content = new StringBuffer();
        String eventName = "";
        HashMap<String, Object> doc = new HashMap<String, Object>();

        while ((s = br.readLine()) != null) {
            if(s.contains("\"<s1>Human:")){
                eventName = s.substring(s.lastIndexOf(":")+1,s.length());
                System.out.println(eventName+"s");

                if(!(content.toString().equals(""))){
                    doc.put("content", content.toString());
                    docs.add(doc);
                }
                RdcComponent rdccomp = fileParse.getName(s);
                content = new StringBuffer();
                doc = new HashMap<String, Object>();
                doc.put("id", i);
                doc.put("key", i+"");
                doc.put("name", rdccomp.getName());
                doc.put("enname", rdccomp.getEname());
                i++;

            }
//            if(!(content.toString().equals(""))) {
            content.append(s + "\n");
//            }

        }
        int start = 0;
        while (start < docs.size()) {
            int end = 0;
            if (start + 1000 <= docs.size()) {
                end = start + 1000;
            } else {
                end = docs.size();
            }
            List<Map<String, Object>> sublist = docs.subList(start, end);
            this.indexDocs("rdccomponents", "_doc", sublist);
            start += 1000;
        }
        br.close();
    }
}
