package com.yihu.ehr.dict.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.dict.model.Icd10HpRelation;
import com.yihu.ehr.dict.service.Icd10HpRelationService;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.specialdict.MIcd10HpRelation;
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
 * @created 2016.05.28 9:32
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "hpIcd10relation", description = "健康问题和ICD10关管理接口")
public class HpIcd10RelationController extends BaseRestController {

    @Autowired
    private Icd10HpRelationService icd10HpRelationService;

    @RequestMapping(value = "/dict/hp/icd10", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "为健康问题增加ICD10疾病关联。" )
    public MIcd10HpRelation createHpIcd10Relation(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestBody String dictJson) throws Exception {

        Icd10HpRelation relation = toEntity(dictJson, Icd10HpRelation.class);
        String id = getObjectId(BizObject.Dict);
        relation.setId(id);
        relation.setCreateDate(new Date());
        icd10HpRelationService.save(relation);
        return convertToModel(relation, MIcd10HpRelation.class, null);
    }

    @RequestMapping(value = "/dict/hp/icd10s", method = RequestMethod.POST)
    @ApiOperation(value = "为健康问题增加ICD10疾病关联,--批量增加关联。" )
    public Collection<MIcd10HpRelation> createHpIcd10Relations(
            @ApiParam(name = "hp_id", value = "健康问题Id")
            @RequestParam(value = "hp_id") String hpId,
            @ApiParam(name = "icd10_ids", value = "关联的icd10字典ids,多个以逗号连接")
            @RequestParam(value = "icd10_ids") String icd10Ids,
            @ApiParam(name = "create_user",value = "创建者")
            @RequestParam(value = "create_user") String createUser) throws Exception {
        Collection<MIcd10HpRelation> mIcd10HpRelations = new ArrayList<>();
        for(String icd10Id : icd10Ids.split(",")){
            Icd10HpRelation relation = new Icd10HpRelation();
            relation.setCreateUser(createUser);
            relation.setCreateDate(new Date());
            relation.setHpId(hpId);
            relation.setIcd10Id(icd10Id);
            String id = getObjectId(BizObject.Dict);
            relation.setId(id);
            icd10HpRelationService.save(relation);
            mIcd10HpRelations.add(convertToModel(relation, MIcd10HpRelation.class, null));
        }
        return mIcd10HpRelations;
    }

    @RequestMapping(value = "/dict/hp/icd10", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "为健康问题修改ICD10疾病关联。" )
    public MIcd10HpRelation updateHpIcd10Relation(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestBody String dictJson) throws Exception {

        Icd10HpRelation relation = toEntity(dictJson, Icd10HpRelation.class);
        if (null == icd10HpRelationService.retrieve(relation.getId())) throw new ApiException(ErrorCode.GetDictFaild, "该关联不存在");
        icd10HpRelationService.save(relation);
        return convertToModel(relation, MIcd10HpRelation.class);
    }

    @RequestMapping(value = "/dict/hp/icd10", method = RequestMethod.DELETE)
    @ApiOperation(value = "为健康问题删除ICD10疾病关联。" )
    public boolean deleteHpIcd10Relation(
            @ApiParam(name = "id", value = "关联ID", defaultValue = "")
            @RequestParam(value = "id", required = true) String id) throws Exception{

        icd10HpRelationService.delete(id);

        return true;
    }

    @RequestMapping(value = "/dict/hp/icd10s", method = RequestMethod.DELETE)
    @ApiOperation(value = "通过ids批量删除健康问题与ICD10关联，多个id以,分隔")
    public boolean deleteHpIcd10Relations(
            @ApiParam(name = "ids", value = "关联关系ids", defaultValue = "")
            @RequestParam(value = "ids") String ids) throws Exception {
        icd10HpRelationService.delete(ids.split(","));
        return true;
    }

    @RequestMapping(value = "/dict/hp/icd10s", method = RequestMethod.GET)
    @ApiOperation(value = "根据健康问题查询相应的ICD10关联列表信息。" )
    public Collection<MIcd10HpRelation> getHpIcd10RelationList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,hpId,icd10Id")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+hpId")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception{

        page = reducePage(page);

        if (StringUtils.isEmpty(filters)) {
            Page<Icd10HpRelation> hpIcd10RelationPage = icd10HpRelationService.getRelationList(sorts, page, size);
            pagedResponse(request, response, hpIcd10RelationPage.getTotalElements(), page, size);
            return convertToModels(hpIcd10RelationPage.getContent(), new ArrayList<>(hpIcd10RelationPage.getNumber()), MIcd10HpRelation.class, fields);
        } else {
            List<Icd10HpRelation> icd10HpRelationList = icd10HpRelationService.search(fields, filters, sorts, page, size);
            pagedResponse(request, response, icd10HpRelationService.getCount(filters), page, size);
            return convertToModels(icd10HpRelationList, new ArrayList<>(icd10HpRelationList.size()), MIcd10HpRelation.class, fields);
        }
    }

    @RequestMapping(value = "/dict/hp/icd10s/no_paging", method = RequestMethod.GET)
    @ApiOperation(value = "根据健康问题查询相应的ICD10关联列表信息,不分页。")
    public Collection<MIcd10HpRelation> getHpIcd10RelationListWithoutPaging(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters) throws Exception {
        List<Icd10HpRelation> icd10HpRelations = icd10HpRelationService.search(filters);
        return convertToModels(icd10HpRelations, new ArrayList<MIcd10HpRelation>(icd10HpRelations.size()), MIcd10HpRelation.class, "");
    }

    @RequestMapping(value = "/dict/hp/icd10/existence" , method = RequestMethod.GET)
    @ApiOperation(value = "判断健康问题与ICD10的关联关系在系统中是否已存在")
    public boolean isHpIcd10RelaExist(

            @ApiParam(name = "icd10Id", value = "Icd10内码", defaultValue = "")
            @RequestParam(value = "icd10Id", required = false) String icd10Id,
            @ApiParam(name = "hpId", value = "健康问题内码")
            @RequestParam(value = "hpId", required = false) String hpId) throws Exception {

        return icd10HpRelationService.isExist(icd10Id,hpId);
    }

    @RequestMapping(value = "/hp_icd10_relation/hp_id" , method = RequestMethod.GET)
    @ApiOperation(value = "根基健康问题id获取健康问题与ICD10的关联")
    public List<MIcd10HpRelation> getHpIcd10RelationByHpId(
            @ApiParam(name = "hp_id", value = "健康问题内码")
            @RequestParam(value = "hp_id", required = false) String hpId) throws Exception {
        List<Icd10HpRelation> icd10HpRelations = icd10HpRelationService.getHpIcd10RelationByHpId(hpId);
        return (List<MIcd10HpRelation>)convertToModels(icd10HpRelations, new ArrayList<>(icd10HpRelations.size()), MIcd10HpRelation.class, "");
    }

}