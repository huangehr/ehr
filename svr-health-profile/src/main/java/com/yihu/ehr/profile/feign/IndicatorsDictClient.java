package com.yihu.ehr.profile.feign;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.specialdict.MIndicatorsDict;
import com.yihu.ehr.model.standard.MCDADocument;
import com.yihu.ehr.model.standard.MCdaDataSetRelationship;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author linaz
 * @created 2016.05.27 9:10
 */
@ApiIgnore
@FeignClient(MicroServices.SpecialDict)
@RequestMapping(ApiVersion.Version1_0)
public interface IndicatorsDictClient {

    @RequestMapping(value = "/dict/indicator/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据ID获取相应的指标字典信息。" )
    MIndicatorsDict getIndicatorsDict(
            @PathVariable(value = "id") String id);
    
    @RequestMapping(value = "/dict/indicators", method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询相应的指标字典信息。" )
    Collection<MIndicatorsDict> getIndicatorsDictList(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = "dict/indicator/icd10/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据指标的ID判断是否与ICD10字典存在关联。")
    boolean indicatorIsUsage(
            @PathVariable( value = "id") String id);
    
    @RequestMapping(value = "/dict/indicator/existence/name" , method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的字典名称是否已经存在")
    boolean isNameExists(
            @RequestParam(value = "name") String name);

    @RequestMapping(value = "/dict/indicator/existence/code/{code}" , method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的字典代码是否已经存在")
    boolean isCodeExists(
            @PathVariable(value = "code") String code);
}
