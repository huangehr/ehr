package com.yihu.ehr.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Multimap;
import com.yihu.ehr.ui.SwaggerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.service.*;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.json.Json;
import springfox.documentation.swagger2.web.Swagger2Controller;

import java.net.URI;
import java.util.*;

/**
 * 应用API入口。入口处显示平台的开放API列表，并可以点击进去。列表只显示各API的第一个GET方法链接。若含有多个GET方法会被忽略。
 * GET方法点击进去之后，根据方法的实际情况
 * - 不需要认证，可以修改下参数就可以显示出结果
 * - 需要认证，此时返回API的错误信息。结构如下：
 *
 * {
 *     message: "需要认证",
 *     documentation_url: http://ehr.yihu.com/docs/help.html
 * }
 *
 * @author Sand
 * @version 1.0
 * @created 2016.02.23 11:45
 */
@RestController
@RequestMapping("/api")
@ApiIgnore
public class ApiEndPoint {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DocumentationCache documentationCache;

    @ApiIgnore
    @RequestMapping(value = "", produces = {"application/json;charset=UTF-8"}, method = RequestMethod.GET)
    public ResponseEntity<Json> getApiIndex() throws JsonProcessingException {
        Documentation documentation = documentationCache.documentationByGroup(SwaggerConfig.PUBLIC_API);
        if (documentation == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Multimap<String, ApiListing> multimap = documentation.getApiListings();
        Map<String, String> urls = new TreeMap<>();

        for (String key : multimap.keys()){
            Collection<ApiListing> apiListings = multimap.get(key);
            Iterator<ApiListing> iterator = apiListings.iterator();

            if(!key.contains("restart") && iterator.hasNext()){
                ApiListing apiListing = iterator.next();
                List<ApiDescription> descriptionList = apiListing.getApis();

                for (ApiDescription apiDescription : descriptionList) {
                    List<Operation> operationList = apiDescription.getOperations();

                    for (Operation operation : operationList) {
                        if (operation.getMethod() == HttpMethod.GET) {
                            List<Parameter> parameters = operation.getParameters();

                            String queryParam = "";
                            for (Parameter parameter : parameters){
                                String type = parameter.getParamType();
                                if (type.equals("query")){
                                    queryParam += "{" + parameter.getName() + "}";
                                }
                            }

                            String url = "https://" + apiListing.getResourcePath() + (queryParam.length() > 0 ? "/" + queryParam : queryParam);
                            urls.put(key + "_url", url);

                            break;
                        }
                    }
                }
            }
        }

        return new ResponseEntity<>(new Json(objectMapper.writeValueAsString(urls)), HttpStatus.OK);
    }
}
