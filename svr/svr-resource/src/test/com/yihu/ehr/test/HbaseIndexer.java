package com.yihu.ehr.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;

/**
 * Created by progr1mmer on 2018/6/15.
 */
public class HbaseIndexer {

    @Test
    public void test() throws Exception {
        List<Map<String, String>> dataList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File("D:\\schemaSUB.xml");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(file);
        NodeList nodeList = doc.getElementsByTagName("fields");
        for (int i = 0; i < nodeList.getLength(); i ++) {
            Node node = nodeList.item(i);
            NodeList childNodes = node.getChildNodes();
            for (int j = 0 ; j < childNodes.getLength(); j ++) {
                Node node1 = childNodes.item(j);
                if (node1.getAttributes() != null) {
                    if (node1.getAttributes().getNamedItem("name") != null) {
                        String name = node1.getAttributes().getNamedItem("name").getNodeValue();
//                        System.out.println("1:" + name);
                        if (!name.endsWith("_ANSJ")) {
                            Map<String, String> dataMap = new LinkedHashMap<>();
                            dataMap.put("inputColumn", "d:" + name);
                            dataMap.put("outputField", name);
                            dataMap.put("type", "string");
                            dataMap.put("isAllowEmpty", "true");
                            dataMap.put("source", "value");
                            dataList.add(dataMap);
                        }
                    }
                }
            }
        }
        System.out.println(objectMapper.writeValueAsString(dataList));

    }
}
