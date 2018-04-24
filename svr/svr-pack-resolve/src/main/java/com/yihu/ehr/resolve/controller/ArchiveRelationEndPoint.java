package com.yihu.ehr.resolve.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * EndPoint - 档案关联
 * Created by progr1mmer on 2018/4/4.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "ArchiveRelationEndPoint", description = "档案关联", tags = {"档案解析服务-档案识别关联信息"})
public class ArchiveRelationEndPoint extends EnvelopRestEndPoint {

    private static final String INDEX = "archive_relation";
    private static final String TYPE = "info";

    @Autowired
    private ElasticSearchUtil elasticSearchUtil;

    @RequestMapping(value = ServiceApi.ArchiveRelation.Crud, method = RequestMethod.GET)
    @ApiOperation(value = "获取档案关联列表")
    public Envelop list(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "分页大小", required = true, defaultValue = "1")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "size", value = "页码", required = true, defaultValue = "15")
            @RequestParam(value = "size") int size) throws Exception {
        List<Map<String, Object>> archiveRelationList  = elasticSearchUtil.page(INDEX, TYPE, filters, sorts, page, size);
        int count = (int)elasticSearchUtil.count(INDEX, TYPE, filters);
        Envelop envelop = getPageResult(archiveRelationList, count, page, size);
        return envelop;
    }
}
