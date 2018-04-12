package com.yihu.ehr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.model.packs.MPackage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by progr1mmer on 2018/4/10.
 */
public class Test {

    @org.junit.Test
    public void test() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(objectMapper.writeValueAsString(null));

        String queue = "{\"id\":\"%s\",\"remote_path\":\"%s\"}";
        System.out.println(String.format(queue, 1, 2));

        String sorts = "sn desc;name asc";
        String [] sortArr = sorts.split(";");
        System.out.println(sortArr[0].split(" ")[1].trim());
    }


}
