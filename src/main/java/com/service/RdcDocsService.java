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
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RdcDocsService extends BaseSeachService{
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private FileParse fileParse;
    /**
     * 同上，作为添加文档时的主键，追加文档时，需要先获取 count数
     */
    static int docs_index = 1;
    public void addDocs(String rootPath, File file) throws Exception {
//        BufferedReader br = new BufferedReader(new FileReader(ResourceUtils.getFile("classpath:rdcdocs/"+fileName)));
        BufferedReader br = new BufferedReader(new FileReader(file));
        String s;
        List<Map<String, Object>> docs = new ArrayList<>();
        StringBuffer content = new StringBuffer();
        HashMap<String, Object> doc = new HashMap<String, Object>();
        doc.put("id", docs_index);
        doc.put("key", docs_index+"");
        doc.put("path", fileParse.getRelatePath(rootPath,file));//用于截取本文件的相对路径，便于后续拼接链接
        doc.put("name", file.getPath());
        System.out.println(file.getName()+"s");
        while ((s = br.readLine()) != null) {
            content.append(s + "\n"); //@TODO: 浪费了，可以优化
        }
        docs_index++;
        doc.put("content", content.toString());
        docs.add(doc);
        int start = 0;
        while (start < docs.size()) {
            int end = 0;
            if (start + 1000 <= docs.size()) {
                end = start + 1000;
            } else {
                end = docs.size();
            }
            List<Map<String, Object>> sublist = docs.subList(start, end);
            this.indexDocs("rdcdocs", "_doc", sublist);
            start += 1000;
        }
        br.close();
    }

    /**
     * 查找文档
     * @param name
     * @param comptype
     * @return
     */

    public List<Map<String, Object>> findDocs(String name, String comptype) {
        SearchRequest searchRequest = new SearchRequest("rdcdocs");
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //匹配查询，走分词
        BoolQueryBuilder queryBuilder2 = QueryBuilders.boolQuery();
        queryBuilder2.should(QueryBuilders.matchQuery("content", name));
        builder.from(0);
        builder.size(100);
        builder.query(queryBuilder2);

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.fragmentSize(15);
        highlightBuilder.preTags("<span style='color:red;'>");
        highlightBuilder.postTags("</span>");
        highlightBuilder.field("content");
        builder.highlighter(highlightBuilder);
        searchRequest.source(builder);
        ArrayList<Map<String, Object>> maps = new ArrayList<>();
        try {
            SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            for (SearchHit hit : search.getHits()) {
                Map<String, Object> sourceMap = hit.getSourceAsMap();
                HighlightField highlightField = hit.getHighlightFields().get("content");
                if (highlightField != null) {
//                    sourceMap.put("content",highlightField.getFragments()[0].toString());
                    sourceMap.put("content2", highlightField.toString());
                    //这里是为了拼接跳转链接，不应该固定在这里
//                    sourceMap.put("path", "http://192.168.138.53:3002/#/2.x/2.1/lowcode/web" + sourceMap.get("path"));
                    sourceMap.put("path", fileParse.getLinkPath(sourceMap.get("path").toString()));
                }
                maps.add(sourceMap);
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
     * 迭代的查找处理文件
     * @param rootPath
     * @param ff
     * @throws Exception
     */
    public void findLocalDocs(String rootPath,File ff) throws Exception {
        if(ff.isDirectory()){
            for(File f: ff.listFiles()){
                this.findLocalDocs(rootPath,f);
            }
        }else if(ff.getName().endsWith(".md")){
            this.addDocs(rootPath,ff);
        }
    }
}
