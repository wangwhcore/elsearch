package com;

import com.Util.Jsousn;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ElsearchApplicationTests2 {
//    @Test
//    void contextLoads() throws IOException {
//        CreateIndexRequest user03 = new CreateIndexRequest("user04");
//        CreateIndexResponse response = restHighLevelClient.indices().create(user03, RequestOptions.DEFAULT);
//        System.out.println(response);
//
//    }
//    @Test
//    void test01() throws IOException {
//        GetRequest user02 = new GetRequest("user02", "3");
//        GetResponse response = restHighLevelClient.get(user02, RequestOptions.DEFAULT);
//        System.out.println(response.toString());
//    }
//    @Test
//    void test02() throws IOException {
//        UpdateRequest user02 = new UpdateRequest("user02", "3");
//        User01 user01 = new User01();
//        user01.setName("小谭来了");
//        user01.setAge("121312");
//        user01.setText("的快感卡拉富贵花开讲话稿");
//        user02.doc(JSON.toJSON(user01),XContentType.JSON);
//        UpdateResponse update = restHighLevelClient.update(user02, RequestOptions.DEFAULT);
//        System.out.println(update.status());
//    }
//    @Test
//    void test03() throws IOException {
//        DeleteRequest user02 = new DeleteRequest("user02", "4");
//        DeleteResponse delete = restHighLevelClient.delete(user02, RequestOptions.DEFAULT);
//        System.out.println(delete.status());
//    }
//    @Test
//    void test04() throws IOException {
//        BulkRequest bulkRequest = new BulkRequest();
//        bulkRequest.timeout("10s");
//        ArrayList<User01> list = new ArrayList<>();
//        User01 user01 = new User01();
//        user01.setName("等级考试感觉");
//        user01.setAge("12");
//        user01.setText("领导看见饭都快死了个IG");
//        list.add(user01);
//        for (int i = 0; i < list.size(); i++) {
//            bulkRequest.add(new IndexRequest("user02").id(""+(i+1)).source(JSON.toJSON(list.get(i)),XContentType.JSON));
//        }
//        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
//        System.out.println(bulk.hasFailures());
//    }
//    @Test
//    void test05() throws IOException {
//        SearchRequest user02 = new SearchRequest("user02");
//        SearchSourceBuilder builder = new SearchSourceBuilder();
//        TermQueryBuilder query = QueryBuilders.termQuery("name", "小谭");
//        builder.query(query);
//        user02.source(builder);
//    }
    @Test
    void test06() throws IOException{
//      String input = "<s1>Human:RDC中页面:定价单-查看-单物料 组件名称:SelectHook 业务名称：\"采购组织\",F下拉框 组件,元属性/元数据怎么编写？</s1>";
//        String input = "<s1>Human:RDC中页面:定价单-查看-单物料 组件名称:SelectHook 业务名称:\"采购组织\",F下拉框 组件,元属性/元数据怎么编写?</s1>";
        String input =
        "<s1>Human:RDC中页面:查看物料组分类列表 组件名称:QzingCategoryList 业务名称: \"别名\",D分类列表 组件,元属性/元数据怎么编写？</s1>";
        String pattern = "组件名称:(\\w+) 业务名称:.*?(\\w+\\d?[^\\s]+)";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(input);

        if (m.find()) {
            System.out.println(m.group(1));
            System.out.println(m.group(2));
        }

    }
}
