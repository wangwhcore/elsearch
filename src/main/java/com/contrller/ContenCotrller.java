package com.contrller;

import com.pojo.Content;
import com.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@CrossOrigin
public class ContenCotrller {
    @Autowired
    private ContentService contentService;
    @RequestMapping("/goods")
    @ResponseBody
    public Boolean goods(String key) throws IOException {
        return contentService.ParesHtml(key);
    }
    @RequestMapping("/goods/i")
    @ResponseBody
    public List<Map<String, Object>> searchPage(String key,int start,int size) throws IOException {
        return contentService.searchPage(key,start,size);
    }
    @RequestMapping("/findall")
    @ResponseBody
    public List<Map<String, Object>> searchPage(String name) throws IOException {
        return contentService.findAll(name);
    }
    @RequestMapping("/findevent")
    @ResponseBody
    public List<Map<String, Object>> searchEvent(String name) throws IOException {
        return contentService.findEvent(name);
    }
    @RequestMapping("/findeventmap")
    @ResponseBody
    public Map searchEventMap(String name) throws IOException {
        List<Map<String, Object>> result = contentService.findEvent(name);
        Map resultmap = new HashMap<>();
        Map page = new HashMap<>();
        resultmap.put("code","0000");
        resultmap.put("success",true);
        resultmap.put("data",result.subList(0,100));
        page.put("totalRecords",resultmap.size());
        resultmap.put("pager", page);

        return resultmap;
    }
    @RequestMapping("/findeventmaphighlight")
    @ResponseBody
    public Map searchEventMapWithColor(String name) throws IOException {
        List<Map<String, Object>> result = contentService.findEventWithHighLight(name);
        Map resultmap = new HashMap<>();
        Map page = new HashMap<>();
        resultmap.put("code","0000");
        resultmap.put("success",true);
        resultmap.put("data",result.subList(0,100));
        page.put("totalRecords",resultmap.size());
        resultmap.put("pager", page);

        return resultmap;
    }
    @RequestMapping("/addevent")
    @ResponseBody
    public void addEvents() throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(ResourceUtils.getFile("classpath:allEvent-srm8.2.txt")));
        String s;
        int i = 1;
        List<Map<String, Object>> docs = new ArrayList<>();
        StringBuffer content = new StringBuffer();
        String eventName = "";
        HashMap<String, Object> doc = new HashMap<String, Object>();

        while ((s = br.readLine()) != null) {
            if(s.contains("页面名称:123 事件名称:")){
                eventName = s.substring(s.lastIndexOf(":")+1,s.length());
                System.out.println(eventName+"s");

                if(!(content.toString().equals(""))){
                    doc.put("content", content.toString());
                    docs.add(doc);
                }
                content = new StringBuffer();
                doc = new HashMap<String, Object>();
                doc.put("id", i);
                doc.put("key", i+"");
                doc.put("eventname", s);
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
            contentService.indexDocs("rdcevent", "_doc", sublist);
            start += 1000;
        }
        br.close();
    }
    @RequestMapping("/findcomps")
    @ResponseBody
    public List<Map<String, Object>> findComps(String name,String comptype ) throws IOException {
        return contentService.findComps(name,comptype);
    }
    @RequestMapping("/addcomponents")
    @ResponseBody
    public void addComponents() throws Exception {
        File file = ResourceUtils.getFile("classpath:rdccomps/");
        if(file.isDirectory()){
            for(File f: file.listFiles()){
                if(f.getName().endsWith(".txt"))
                    contentService.addComponents(f.getName());
            }
        }
    }

    @RequestMapping("/finddocs")
    @ResponseBody
    public List<Map<String, Object>> findDocs(String name,String comptype) throws Exception {
        return contentService.findDocs(name,comptype);
    }
    @RequestMapping("/addrdcdocs")
    @ResponseBody
    public void addDocs() throws Exception {
        String fileRoot = "D:\\neusoft\\docs\\markdown\\ieep-tech-doc-front\\docs\\2.x\\2.2\\lowcode\\web";
        File file = new File(fileRoot);
        contentService.findLocalDocs(fileRoot,file);
    }
}
