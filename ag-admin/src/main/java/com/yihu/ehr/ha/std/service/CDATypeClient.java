package com.yihu.ehr.ha.std.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.model.standard.MCDAType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/2/29.
 */
@FeignClient("svr-standard")
@RequestMapping(ApiVersion.Version1_0 + "/std")
@ApiIgnore
public interface CDATypeClient {

    @RequestMapping(value = "/children_cda_types/{parent_id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据父级ID获取下级")
    List<MCDAType> getChildrenByPatientId(
            @ApiParam(name = "parent_id", value = "父级id")
            @PathVariable(value = "parent_id") String parentId);


    @RequestMapping(value = "/cda_types/patient_ids/key", method = RequestMethod.GET)
    @ApiOperation(value = "根据父级类别获取父级类别所在以下所有子集类别（包括当前父级列表）")
    List<MCDAType> getChildIncludeSelfByParentTypesAndKey(
            @ApiParam(name = "patient_ids", value = "父级id")
            @RequestParam(value = "patient_ids") String[] patientIds,
            @ApiParam(name = "key", value = "查询条件")
            @RequestParam(value = "key") String key);


    @RequestMapping(value = "/cda_types/code_name", method = RequestMethod.GET)
    @ApiOperation(value = "根据code或者name获取CDAType列表")
    List<MCDAType> getCdaTypeByCodeOrName(
            @ApiParam(name = "code", value = "代码")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "name", value = "名称")
            @RequestParam(value = "name") String name);


    @RequestMapping(value = "/cda_types/id/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取CDAType")
    MCDAType getCdaTypeById(
            @ApiParam(name = "id", value = "id")
            @PathVariable(value = "id") String id);


    @RequestMapping(value = "/cda_types/ids/{ids}", method = RequestMethod.GET)
    @ApiOperation(value = "根据ids获取CDAType列表")
    List<MCDAType> getCdaTypeByIds(
            @ApiParam(name = "ids", value = "ids")
            @PathVariable(value = "ids") String[] ids);


    @RequestMapping(value = "/cda_types", method = RequestMethod.POST)
    @ApiOperation(value = "新增CDAType")
    MCDAType saveCDAType(
            @ApiParam(name = "jsonData", value = "json")
            @RequestParam(value = "jsonData") String jsonData);


    @RequestMapping(value = "/cda_types", method = RequestMethod.PUT)
    @ApiOperation(value = "修改CDAType")
    MCDAType updateCDAType(
            @ApiParam(name = "jsonData", value = "json")
            @RequestParam(value = "jsonData") String jsonData);


    @RequestMapping(value = "/cda_types/existence/{code}", method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的机构代码是否已经存在")
    boolean isCDATypeExists(
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @PathVariable(value = "code") String code);


    @RequestMapping(value = "/cda_types/{ids}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除CDA类别，若该类别存在子类别，将一并删除子类别")
    boolean deleteCDATypeByPatientIds(
            @ApiParam(name = "ids", value = "ids")
            @PathVariable(value = "ids") String[] ids);

}
