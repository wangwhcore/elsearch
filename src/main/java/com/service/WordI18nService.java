package com.service;

import com.Util.FileParse;
import com.Util.Jsousn;
import com.alibaba.fastjson.JSON;
import com.pojo.I18nText;
import com.pojo.RdcComponent;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WordI18nService extends BaseSeachService{
    @Autowired
    private Jsousn jsousn;
    @Autowired
    private FileParse fileParse;

    static int docs_index = 1;
    /**
     * es中使用 id作为主键，这里试用与首次全量初始化（会覆盖相同ID内容）
     * 如需追加，需要另外写方法先获取 count后，再绑定 i（id主键）   get /rdccomps/_count
     */
    static int i = 1;
    public void addWords(String rootPath, File file) throws Exception {
//        BufferedReader br = new BufferedReader(new FileReader(ResourceUtils.getFile("classpath:rdcdocs/"+fileName)));
        BufferedReader br = new BufferedReader(new FileReader(file));
        String s;
        List<Map<String, Object>> docs = new ArrayList<>();


        System.out.println(file.getName()+"s");
        while ((s = br.readLine()) != null && s.trim() != "") {
            HashMap<String, Object> doc = new HashMap<String, Object>();
            doc.put("id", docs_index);
            doc.put("key", docs_index+"");
            doc.put("path", fileParse.getRelatePath(rootPath,file));//用于截取本文件的相对路径，便于后续拼接链接
            I18nText i18nText = JSON.parseObject(s, I18nText.class);
            doc.put("filed",i18nText.getFiled());
            doc.put("i18nText",i18nText.getI18nText());
            doc.put("chName",i18nText.getChName());
            docs.add(doc);
            docs_index++;
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
            this.indexDocs("rdcwordi18n", "_doc", sublist);
            start += 1000;
        }
        br.close();
    }

    public List<Map<String, Object>> findDocs(String name, String comptype) {
        SearchRequest searchRequest = new SearchRequest("rdcwordi18n");
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //匹配查询，走分词
        BoolQueryBuilder queryBuilder2 = QueryBuilders.boolQuery();
        queryBuilder2.should(QueryBuilders.matchPhraseQuery("i18nText", name));
        queryBuilder2.should(QueryBuilders.matchPhraseQuery("filed", name));
        queryBuilder2.should(QueryBuilders.matchPhraseQuery("chName", name));
        queryBuilder2.minimumShouldMatch();
        builder.from(0);
        builder.size(100);
        builder.query(queryBuilder2);
//        builder.sort(SortBuilders.fieldSort("i18nText").order(SortOrder.DESC));
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
}
