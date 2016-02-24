package com.yihu.ehr.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Multimap;
import com.yihu.ehr.ui.SwaggerConfig;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.service.ApiListing;
import springfox.documentation.service.Documentation;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.json.Json;
import springfox.documentation.swagger2.web.Swagger2Controller;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.02.23 11:45
 */
@RestController
@RequestMapping("/api")
@ApiIgnore
public class ApiEntryController {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DocumentationCache documentationCache;

    @ApiIgnore
    @RequestMapping(value = "", produces = {"application/json;charset=UTF-8"}, method = RequestMethod.GET)
    public ResponseEntity<String> getApiIndex() throws JsonProcessingException {
        Documentation documentation = documentationCache.documentationByGroup(SwaggerConfig.PUBLIC_API);
        if (documentation == null) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }

        Multimap<String, ApiListing> multimap = documentation.getApiListings();
        Map<String, String> urls = new HashMap<>(multimap.size());

        for (String key : multimap.keys()){
            Collection<ApiListing> apiListings = multimap.get(key);
            Iterator<ApiListing> iterator = apiListings.iterator();

            if(!key.contains("restart") && iterator.hasNext()){
                ApiListing apiListing = iterator.next();

                urls.put(key + "_url", "https://" + hostName() + apiListing.getResourcePath());
            }
        }

        return new ResponseEntity<String>(objectMapper.writeValueAsString(urls), HttpStatus.OK);
    }

    private String hostName() {
        URI uri = linkTo(Swagger2Controller.class).toUri();
        String host = uri.getHost();
        int port = uri.getPort();
        if (port > -1) {
            return String.format("%s:%d", host, port);
        }

        return host;
    }
}
