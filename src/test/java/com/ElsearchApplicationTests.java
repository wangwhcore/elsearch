package com;

import com.Util.Jsousn;
import com.alibaba.fastjson.JSON;
import com.pojo.Content;
import com.pojo.User01;
import org.apache.catalina.User;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@SpringBootTest
class ElsearchApplicationTests {
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private Jsousn jsousn;
    @Test
    void contextLoads() throws IOException {
        CreateIndexRequest user03 = new CreateIndexRequest("user04");
        CreateIndexResponse response = restHighLevelClient.indices().create(user03, RequestOptions.DEFAULT);
        System.out.println(response);

    }
    @Test
    void test01() throws IOException {
        GetRequest user02 = new GetRequest("user02", "3");
        GetResponse response = restHighLevelClient.get(user02, RequestOptions.DEFAULT);
        System.out.println(response.toString());
    }
    @Test
    void test02() throws IOException {
        UpdateRequest user02 = new UpdateRequest("user02", "3");
        User01 user01 = new User01();
        user01.setName("小谭来了");
        user01.setAge("121312");
        user01.setText("的快感卡拉富贵花开讲话稿");
        user02.doc(JSON.toJSON(user01),XContentType.JSON);
        UpdateResponse update = restHighLevelClient.update(user02, RequestOptions.DEFAULT);
        System.out.println(update.status());
    }
    @Test
    void test03() throws IOException {
        DeleteRequest user02 = new DeleteRequest("user02", "4");
        DeleteResponse delete = restHighLevelClient.delete(user02, RequestOptions.DEFAULT);
        System.out.println(delete.status());
    }
    @Test
    void test04() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");
        ArrayList<User01> list = new ArrayList<>();
        User01 user01 = new User01();
        user01.setName("等级考试感觉");
        user01.setAge("12");
        user01.setText("领导看见饭都快死了个IG");
        list.add(user01);
        for (int i = 0; i < list.size(); i++) {
            bulkRequest.add(new IndexRequest("user02").id(""+(i+1)).source(JSON.toJSON(list.get(i)),XContentType.JSON));
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulk.hasFailures());
    }
    @Test
    void test05() throws IOException {
        SearchRequest user02 = new SearchRequest("user02");
        SearchSourceBuilder builder = new SearchSourceBuilder();
        TermQueryBuilder query = QueryBuilders.termQuery("name", "小谭");
        builder.query(query);
        user02.source(builder);
    }
    void test06() throws IOException{
        Scanner scanner = new Scanner(System.in);
        System.out.println("请搜索:");
        String next = scanner.next();
        List<Content> java = jsousn.contentList(next);
        System.out.println(java.toString());
    }
}
