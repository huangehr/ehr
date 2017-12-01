package com.yihu.ehr.dfs.es.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.dfs.es.entity.FastDFSIndex;
import com.yihu.ehr.dfs.es.service.FastDFSIndexService;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

/**
 * IndexController - FastDFS索引
 * Created by progr1mmer on 2017/11/25.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "FastDFSIndexEndPoint", description = "FastDFS索引接口", tags = {"ES-FastDFS"})
public class FastDFSIndexEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private FastDFSIndexService fastDFSIndexService;

    @RequestMapping(value = "/es/index", method = RequestMethod.POST)
    @ApiOperation(value = "建立索引")
    public Envelop index(
            @ApiParam(name = "name", value = "名字")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "path", value = "路径")
            @RequestParam(value = "path") String path) {
        Envelop envelop = new Envelop();
        FastDFSIndex fastDFSIndex = new FastDFSIndex();
        //fastDFSIndex.setId(new Date().getTime());
        fastDFSIndex.setSn("sn" + new Date().getTime());
        fastDFSIndex.setName(name);
        fastDFSIndex.setType("default");
        fastDFSIndex.setPath(path);
        fastDFSIndex.setCreateDate(new Date());
        fastDFSIndex.setCreator("sxy");
        fastDFSIndex.setModifyDate(new Date());
        fastDFSIndex.setModifier("sxy");
        FastDFSIndex fastDFSIndex1;
        try {
            fastDFSIndex1 = fastDFSIndexService.index(fastDFSIndex);
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorCode(500);
            envelop.setErrorMsg(e.getMessage());
            return envelop;
        }
        envelop.setSuccessFlg(true);
        envelop.setObj(fastDFSIndex1);
        return envelop;
    }

    @RequestMapping(value = "/es/delete", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除索引")
    public Envelop delete(
            @ApiParam(name = "sn", value = "sn")
            @RequestParam(value = "sn") String sn) {
        Envelop envelop = new Envelop();
        fastDFSIndexService.delete(sn);
        envelop.setSuccessFlg(true);
        return envelop;
    }

    @RequestMapping(value = "/es/update", method = RequestMethod.PUT)
    @ApiOperation(value = "更新索引")
    public Envelop update(
            @ApiParam(name = "sn", value = "sn")
            @RequestParam(value = "sn") String sn,
            @ApiParam(name = "name", value = "名字")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "path", value = "路径")
            @RequestParam(value = "path") String path) {
        Envelop envelop = new Envelop();
        FastDFSIndex fastDFSIndex = fastDFSIndexService.findBySn(sn);
        fastDFSIndex.setName(name);
        fastDFSIndex.setPath(path);
        fastDFSIndexService.index(fastDFSIndex);
        envelop.setSuccessFlg(true);
        return envelop;
    }

    @RequestMapping(value = "/es/search", method = RequestMethod.GET)
    @ApiOperation(value = "搜索")
    public Envelop search(
            @ApiParam(name = "filter", value = "查询条件")
            @RequestParam(value = "filter") String filter,
            @ApiParam(name = "page", value = "页数", defaultValue = "1")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "size", value = "大小", defaultValue = "15")
            @RequestParam(value = "size") int size) {
        Envelop envelop = new Envelop();
        Map<String, String> queryMap = null;
        try {
            queryMap = objectMapper.readValue(filter, Map.class);
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorCode(500);
            envelop.setErrorMsg(e.getMessage());
        }
        if(queryMap != null) {

        }else {

        }
        fastDFSIndexService.index(null);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //SearchQuery query = new NativeSearchQueryBuilder().withQuery()
        //BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //fastDFSIndexService.search(query);
        envelop.setSuccessFlg(true);
        return envelop;
    }
}
