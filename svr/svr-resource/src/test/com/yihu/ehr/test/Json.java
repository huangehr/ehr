package com.yihu.ehr.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * Created by progr1mmer on 2018/7/10.
 */
public class Json {

    @Test
    public void test() throws Exception {
        String data = "[{\"mime\":\"text/html\",\"urls\":\"group1/M00/26/B4/rBFuWFs5etSADACsAAAieob_q0Q73.html\",\"url_score\":\"\",\"emr_id\":\"73326\",\"emr_name\":\"健康宣教--上饶中医院 2017-03-05 10:36:36 李琦芯\",\"expire_date\":\"\",\"note\":null,\"files\":\"73326:group1/M00/26/B4/rBFuWFs5etSADACsAAAieob_q0Q73.html;\",\"cda_document_id\":\"HSDC02.39\",\"cda_document_name\":null},{\"mime\":\"text/html\",\"urls\":\"group1/M00/26/B4/rBFuWFs5etSAGLX8AAAibP2Xg1g89.html\",\"url_score\":\"\",\"emr_id\":\"73331\",\"emr_name\":\"入院须知--上饶中医院 2017-03-05 10:37:37 李琦芯\",\"expire_date\":\"\",\"note\":null,\"files\":\"73331:group1/M00/26/B4/rBFuWFs5etSAGLX8AAAibP2Xg1g89.html;\",\"cda_document_id\":\"HSDC02.39\",\"cda_document_name\":null}]";
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, String>> list = objectMapper.readValue(data, List.class);
        System.out.println(list.size());
    }

}
