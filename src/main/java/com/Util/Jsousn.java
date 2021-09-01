package com.Util;

import com.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
@Component
public class Jsousn {
    public  List<Content> contentList(String key) throws IOException {
        String url="https://search.suning.com/emall/bookSearch.do?keyword="+key;
        Document parse = Jsoup.parse(new URL(url), 3000);
        Elements elementsimg = parse.getElementsByClass("res-img");
        ArrayList<Content> contents = new ArrayList<>();
        for (Element element : elementsimg) {
            String title = element.getElementsByTag("img").attr("alt");
            String img ="https:"+ element.getElementsByTag("img").attr("src2");
            Content content = new Content();
            content.setTitle(title);
            content.setImg(img);
            contents.add(content);
        }
        return contents;
    }
}
