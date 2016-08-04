package com.yihu.ehr.medicalRecords.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.specialdict.MIcd10Dict;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;

/**
 * Created by hzp on 2016/8/1.
 */
@FeignClient(MicroServices.SpecialDict)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface XDictClient {
    @RequestMapping(value = "/dict/icd10", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建新的ICD10字典" )
    MIcd10Dict createIcd10Dict(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestBody String dictJson);

    /**
     * like：使用"?"来表示，如：name?%医
     * in：使用"="来表示并用","逗号对值进行分隔，如：status=2,3,4,5
     * not in：使用"<>"来表示并用","逗号对值进行分隔，如：status=2,3,4,5
     * =：使用"="来表示，如：status=2
     * >=：使用大于号和大于等于语法，如：createDate>2012
     * <=：使用小于号和小于等于语法，如：createDate<=2015
     * 分组：在条件后面加上空格，并设置分组号，如：createDate>2012 g1，具有相同组名的条件将使用or连接 GB/T 2261.2-2003
     * 多条件组合：使用";"来分隔
     * */
    @RequestMapping(value = "/dict/icd10s", method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询相应的ICD10字典信息。" )
    ResponseEntity<Collection<MIcd10Dict>> getIcd10DictList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器(例如 phoneticCode?%T ?=like)", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page);

}
