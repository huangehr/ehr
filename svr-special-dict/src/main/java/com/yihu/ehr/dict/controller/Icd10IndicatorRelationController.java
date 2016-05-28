package com.yihu.ehr.dict.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.dict.service.Icd10IndicatorRelation;
import com.yihu.ehr.dict.service.Icd10IndicatorRelationService;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.specialdict.MIcd10IndicatorRelation;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author linaz
 * @created 2016.05.28 9:52
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "Icd10Dict", description = "ICD10与指标之间关联关系管理接口")
public class Icd10IndicatorRelationController  extends BaseRestController {

    @Autowired
    private Icd10IndicatorRelationService icd10IndicatorRelationService;

    @RequestMapping(value = "/dict/icd10/indicator", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "为ICD10增加指标关联。" )
    public MIcd10IndicatorRelation createIcd10IndicatorRelation(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestBody String dictJson) throws Exception{

        Icd10IndicatorRelation relation = toEntity(dictJson, Icd10IndicatorRelation.class);
        String id = getObjectId(BizObject.Dict);
        relation.setId(id);
        relation.setCreateDate(new Date());
        icd10IndicatorRelationService.save(relation);
        return convertToModel(relation, MIcd10IndicatorRelation.class, null);
    }

    @RequestMapping(value = "/dict/icd10/indicators", method = RequestMethod.POST)
    @ApiOperation(value = "为ICD10增加指标关联。---批量关联，" )
    public Collection<MIcd10IndicatorRelation> createIcd10IndicatorRelations(
            @ApiParam(name = "icd10_id", value = "健康问题Id")
            @RequestParam(value = "icd10_id") String icd10Id,
            @ApiParam(name = "indicator_ids", value = "关联的指标字典ids,多个以逗号连接")
            @RequestParam(value = "indicator_ids") String indicatorIds,
            @ApiParam(name = "create_user",value = "创建者")
            @RequestParam(value = "create_user") String createUser) throws Exception{

        Collection<Icd10IndicatorRelation> relations = new ArrayList<>();
        for(String indicatorId : indicatorIds.split(",")){
            Icd10IndicatorRelation relation = new Icd10IndicatorRelation();
            relation.setCreateUser(createUser);
            relation.setCreateDate(new Date());
            relation.setIcd10Id(icd10Id);
            relation.setIndicatorId(indicatorId);
            String id = getObjectId(BizObject.Dict);
            relation.setId(id);
            icd10IndicatorRelationService.save(relation);
            relations.add(relation);
        }
        return convertToModels(relations, new ArrayList<MIcd10IndicatorRelation>(relations.size()), MIcd10IndicatorRelation.class, null);
    }

    @RequestMapping(value = "/dict/icd10/indicator", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "为ICD10修改指标关联。" )
    public MIcd10IndicatorRelation updateIcd10IndicatorRelation(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestBody String dictJson) throws Exception {

        Icd10IndicatorRelation relation = toEntity(dictJson, Icd10IndicatorRelation.class);
        if (null == icd10IndicatorRelationService.retrieve(relation.getId())) throw new ApiException(ErrorCode.GetDictFaild, "该关联不存在");
        icd10IndicatorRelationService.save(relation);
        return convertToModel(relation, MIcd10IndicatorRelation.class);
    }

    @RequestMapping(value = "/dict/icd10/indicator", method = RequestMethod.DELETE)
    @ApiOperation(value = "为ICD10删除指标关联。" )
    public boolean deleteIcd10IndicatorRelation(
            @ApiParam(name = "id", value = "关联ID", defaultValue = "")
            @RequestParam(value = "id", required = true) String id) throws Exception{

        icd10IndicatorRelationService.delete(id);

        return true;
    }

    @RequestMapping(value = "/dict/icd10/indicators", method = RequestMethod.DELETE)
    @ApiOperation(value = "为ICD10删除指标关联。--批量删除，多个以逗号隔开" )
    public boolean deleteIcd10IndicatorRelations(
            @ApiParam(name = "ids", value = "关联IDs", defaultValue = "")
            @RequestParam(value = "ids", required = true) String ids) throws Exception{

        icd10IndicatorRelationService.delete(ids.split(","));

        return true;
    }

    @RequestMapping(value = "/dict/icd10/indicators", method = RequestMethod.GET)
    @ApiOperation(value = "根据ICD10查询相应的指标关联列表信息。" )
    public Collection<MIcd10IndicatorRelation> getIcd10IndicatorRelationList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,icd10Id,indicatorId")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+icd10Id")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception{

        page = reducePage(page);

        if (StringUtils.isEmpty(filters)) {
            Page<Icd10IndicatorRelation> icd10IndicatorRelationPage = icd10IndicatorRelationService.getRelationList(sorts, page, size);
            pagedResponse(request, response, icd10IndicatorRelationPage.getTotalElements(), page, size);
            return convertToModels(icd10IndicatorRelationPage.getContent(), new ArrayList<>(icd10IndicatorRelationPage.getNumber()), MIcd10IndicatorRelation.class, fields);
        } else {
            List<Icd10IndicatorRelation> icd10IndicatorRelationList = icd10IndicatorRelationService.search(fields, filters, sorts, page, size);
            pagedResponse(request, response, icd10IndicatorRelationService.getCount(filters), page, size);
            return convertToModels(icd10IndicatorRelationList, new ArrayList<>(icd10IndicatorRelationList.size()), MIcd10IndicatorRelation.class, fields);
        }
    }

    @RequestMapping(value = "/dict/icd10/indicators/no_paging", method = RequestMethod.GET)
    @ApiOperation(value = "根据ICD10查询相应的指标关联列表信息,---不分页。")
    public Collection<MIcd10IndicatorRelation> getIcd10IndicatorRelationListWithoutPaging(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters) throws Exception {
        List<Icd10IndicatorRelation> icd10IndicatorRelations = icd10IndicatorRelationService.search(filters);
        return convertToModels(icd10IndicatorRelations, new ArrayList<MIcd10IndicatorRelation>(icd10IndicatorRelations.size()), MIcd10IndicatorRelation.class, "");
    }

    @RequestMapping(value = "/dict/icd10/indicator/existence" , method = RequestMethod.GET)
    @ApiOperation(value = "判断ICD10与指标字典的关联关系在系统中是否已存在")
    public boolean isIcd10IndicatorsRelationExist(
            @ApiParam(name = "indicatorsId", value = "药品字典内码")
            @RequestParam(value = "indicatorsId", required = false) String indicatorsId,
            @ApiParam(name = "icd10Id", value = "Icd10内码", defaultValue = "")
            @RequestParam(value = "icd10Id", required = false) String icd10Id) throws Exception {

        return icd10IndicatorRelationService.isExist(icd10Id,indicatorsId);
    }

    @RequestMapping(value = "/icd10_indicator_relations/icd10_ids" , method = RequestMethod.GET)
    @ApiOperation(value = "根据ICD10id列表获取IDC10和指标关系")
    public List<MIcd10IndicatorRelation> getIcd10DrugRelationsByIcd10Ids(
            @ApiParam(name = "icd10_ids", value = "icd10_ids", defaultValue = "")
            @RequestParam(value = "icd10_ids", required = false) String[] icd10Ids) throws Exception {
        List<Icd10IndicatorRelation> icd10IndicatorRelations = icd10IndicatorRelationService.getIcd10DrugRelationsByIcd10Ids(icd10Ids);
        return ( List<MIcd10IndicatorRelation>)convertToModels(icd10IndicatorRelations,new ArrayList<MIcd10IndicatorRelation>(icd10IndicatorRelations.size()),MIcd10IndicatorRelation.class,"");
    }
}