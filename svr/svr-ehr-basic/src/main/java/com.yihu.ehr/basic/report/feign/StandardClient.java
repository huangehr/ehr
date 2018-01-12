package com.yihu.ehr.basic.report.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.model.standard.MStdDataSet;
import com.yihu.ehr.model.standard.MStdMetaData;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Map;

@FeignClient(value = MicroServices.Standard)
@RequestMapping(ApiVersion.Version1_0)
public interface StandardClient {

    @RequestMapping(value = ServiceApi.Standards.NoPageDataSets, method = RequestMethod.GET)
    @ApiOperation(value = "标准数据集不分页搜索")
    Collection<MStdDataSet> searchSourcesWithoutPaging(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version);

    @RequestMapping(value = ServiceApi.Standards.MetaDatas, method = RequestMethod.GET)
    @ApiOperation(value = "查询数据元")
    Collection<MStdMetaData> searchDataSets(
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
            @RequestParam(value = "version") String version);

    @RequestMapping(value = ServiceApi.Standards.GetDataSetByCode, method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "根据Code获取数据集", notes = "根据Code获取数据集")
    Map<String,Object> getDataSetByCode(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "code", value = "数据集code", defaultValue = "")
            @RequestParam(value = "code") String code);
}
