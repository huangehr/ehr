package com.yihu.ehr.api.dict;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.feign.StdDictEntryClient;
import com.yihu.ehr.model.standard.MStdDictEntry;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.1
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "Entry", description = "字典项服务")
public class DictEntryEndPoint {

    @Autowired
    private StdDictEntryClient stdDictEntryClient;

    @RequestMapping(value = "/std/dictionaries/entries", method = RequestMethod.GET)
    @ApiOperation(value = "查询字典项")
    public Collection<MStdDictEntry> searchDictEntry(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version) throws Exception{
       return stdDictEntryClient.searchDictEntry(fields,filters,sorts,size,page,version);
    }

}
