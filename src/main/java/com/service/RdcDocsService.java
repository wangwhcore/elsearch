package com.service;

import com.Util.FileParse;
import com.pojo.RdcComponent;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RdcDocsService {
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private FileParse fileParse;
    static int i = 1;
    public void addDocs(String fileName) throws Exception {
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

}
