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
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Service
public class ContentService {
    @Autowired
    private Jsousn jsousn;
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private FileParse fileParse;
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

//    public boolean addContent(String key){
//        List<Content> contents = null;
//        System.out.println(contents.toString());
//        BulkRequest bulkRequest = new BulkRequest();
//        for (Content content : contents) {
//            bulkRequest.add(
//                    new IndexRequest("goods").source(JSON.toJSONString(content), XContentType.JSON)
//            );
//        }
//        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
//        return !bulk.hasFailures();
//    }
    public void indexDocs(String indexName, String indexType, List<Map<String, Object>> docs) {
        try {
            if (null == docs || docs.size() <= 0) {
                return;
            }
            BulkRequest request = new BulkRequest();
            for (Map<String, Object> doc : docs) {
                request.add(new IndexRequest(indexName, indexType, (String)doc.get("key"))
                        .source(doc));
            }
            BulkResponse bulkResponse = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
            if (bulkResponse != null) {
                for (BulkItemResponse bulkItemResponse : bulkResponse) {
                    DocWriteResponse itemResponse = bulkItemResponse.getResponse();

                    if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.INDEX
                            || bulkItemResponse.getOpType() == DocWriteRequest.OpType.CREATE) {
                        IndexResponse indexResponse = (IndexResponse) itemResponse;
                        System.out.println("新增成功" + indexResponse.toString());
                    } else if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.UPDATE) {
                        UpdateResponse updateResponse = (UpdateResponse) itemResponse;
                        System.out.println("修改成功" + updateResponse.toString());
                    } else if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.DELETE) {
                        DeleteResponse deleteResponse = (DeleteResponse) itemResponse;
                        System.out.println("删除成功" + deleteResponse.toString());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


        public List<Map<String, Object>> findAll(String key) throws IOException {
        SearchRequest searchRequest = new SearchRequest("goods");
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //精确查询，且不走分词
//        TermQueryBuilder queryBuilder = QueryBuilders.termQuery("title", key);
//        builder.from(0);
//        builder.size(100);
//        builder.query(queryBuilder);
//        searchRequest.source(builder);
        //匹配查询，走分词
        MatchQueryBuilder queryBuilder2 = QueryBuilders.matchQuery("title", key);
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
