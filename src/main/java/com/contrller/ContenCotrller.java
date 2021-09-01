package com.contrller;

import com.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
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
}
