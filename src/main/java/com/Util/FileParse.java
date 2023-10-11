package com.Util;

import com.pojo.Content;
import com.pojo.RdcComponent;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class FileParse {
    public RdcComponent getName(String str){
        RdcComponent rdcComponent = new RdcComponent() ;
//        String input = "<s1>Human:RDC中页面:定价单-查看-单物料 组件名称:SelectHook 业务名称:\"采购组织\",F下拉框 组件,元属性/元数据怎么编写?</s1>";
        String pattern = "组件名称:(\\w+) 业务名称:.*?(\\w+\\d?[^\\s]+)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(str);

        if (m.find()) {
            rdcComponent.setEname(m.group(1));
            rdcComponent.setName(m.group(2));
        }
        return rdcComponent;
    }
    public String  getRelatePath(String rootPath,File f){
        return f.getPath().replace(rootPath,"");
    }
}
