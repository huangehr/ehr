package com.yihu.ehr.specialdict.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.specialdict.MIcd10DrugRelation;
import com.yihu.ehr.specialdict.model.Icd10DrugRelation;
import com.yihu.ehr.specialdict.service.Icd10DrugRelationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
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
 * @created 2016.05.28 9:46
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "Icd10DrugRelationEndPoint", description = "ICD10和疾病关系", tags = {"特殊字典-ICD10和疾病关系"})
public class Icd10DrugRelationEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private Icd10DrugRelationService icd10DrugRelationService;

    @RequestMapping(value = "/dict/icd10/drug", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "为ICD10增加药品关联。" )
    public MIcd10DrugRelation createIcd10DrugRelation(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestBody String dictJson) throws Exception {

        Icd10DrugRelation relation = toEntity(dictJson, Icd10DrugRelation.class);
        relation.setCreateDate(new Date());
        icd10DrugRelationService.save(relation);
        return convertToModel(relation, MIcd10DrugRelation.class, null);
    }

    @RequestMapping(value = "/dict/icd10/drugs", method = RequestMethod.POST)
    @ApiOperation(value = "为ICD10增加药品关联。--批量关联" )
    public Collection<MIcd10DrugRelation> createIcd10DrugRelations(
            @ApiParam(name = "icd10_id", value = "健康问题Id")
            @RequestParam(value = "icd10_id") long icd10Id,
            @ApiParam(name = "drug_ids", value = "关联的药品字典ids,多个以逗号连接")
            @RequestParam(value = "drug_ids") String drugIds,
            @ApiParam(name = "create_user",value = "创建者")
            @RequestParam(value = "create_user") String createUser) throws Exception {
        Collection<Icd10DrugRelation> icd10DrugRelations = new ArrayList<>();
        for(String drugId : drugIds.split(",")){
            Icd10DrugRelation icd10DrugRelation = new Icd10DrugRelation();
            icd10DrugRelation.setCreateUser(createUser);
            icd10DrugRelation.setCreateDate(new Date());
            icd10DrugRelation.setIcd10Id(icd10Id);
            icd10DrugRelation.setDrugId(Long.parseLong(drugId));
            icd10DrugRelationService.save(icd10DrugRelation);
            icd10DrugRelations.add(icd10DrugRelation);
        }
        return convertToModels(icd10DrugRelations, new ArrayList<MIcd10DrugRelation>(icd10DrugRelations.size()), MIcd10DrugRelation.class, null);
    }

    @RequestMapping(value = "/dict/icd10/drug", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "为ICD10修改药品关联。" )
    public MIcd10DrugRelation updateIcd10DrugRelation(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestBody String dictJson) throws Exception {

        Icd10DrugRelation relation = toEntity(dictJson, Icd10DrugRelation.class);
        if (null == icd10DrugRelationService.retrieve(relation.getId())) throw new ApiException(ErrorCode.GetDictFaild, "该关联不存在");
        icd10DrugRelationService.save(relation);
        return convertToModel(relation, MIcd10DrugRelation.class);
    }

    @RequestMapping(value = "/dict/icd10/drug", method = RequestMethod.DELETE)
    @ApiOperation(value = "为ICD10删除药品关联。" )
    public boolean deleteIcd10DrugRelation(
            @ApiParam(name = "id", value = "关联ID", defaultValue = "")
            @RequestParam(value = "id", required = true) long id) throws Exception{

        icd10DrugRelationService.delete(id);

        return true;
    }

    @RequestMapping(value = "/dict/icd10/drugs", method = RequestMethod.DELETE)
    @ApiOperation(value = "为ICD10删除药品关联。--批量删除，多个以逗号隔开" )
    public boolean deleteIcd10DrugRelations(
            @ApiParam(name = "ids", value = "关联IDs", defaultValue = "")
            @RequestParam(value = "ids", required = true) String ids) throws Exception{

        String[] strIds = ids.split(",");
        Long[] longIds = new Long[strIds.length];
        for(int i=0; i<strIds.length;i++){
            longIds[i] = Long.parseLong(strIds[i]);
        }
        icd10DrugRelationService.delete(longIds);
        return true;
    }

    @RequestMapping(value = "/dict/icd10/drugs", method = RequestMethod.GET)
    @ApiOperation(value = "根据ICD10查询相应的药品关联列表信息。" )
    public  Collection<MIcd10DrugRelation> getIcd10DrugRelationList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,icd10Id,drugId")
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

        List<Icd10DrugRelation> icd10DrugRelationList = icd10DrugRelationService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, icd10DrugRelationService.getCount(filters), page, size);
        return convertToModels(icd10DrugRelationList, new ArrayList<>(icd10DrugRelationList.size()), MIcd10DrugRelation.class, fields);
    }

    @RequestMapping(value = "/dict/icd10/drugs/no_paging", method = RequestMethod.GET)
    @ApiOperation(value = "根据ICD10查询相应的药品关联列表信息,------不分页。")
    public Collection<MIcd10DrugRelation> getIcd10DrugRelationListWithoutPaging(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters) throws Exception {
        List<Icd10DrugRelation> icd10DrugRelations = icd10DrugRelationService.search(filters);
        return convertToModels(icd10DrugRelations, new ArrayList<MIcd10DrugRelation>(icd10DrugRelations.size()), MIcd10DrugRelation.class, "");
    }

    @RequestMapping(value = "/dict/icd10/drug/existence" , method = RequestMethod.GET)
    @ApiOperation(value = "判断ICD10与药品字典的关联关系在系统中是否已存在")
    public boolean isIcd10DrugRelationExist(
            @ApiParam(name = "drugId", value = "药品字典内码")
            @RequestParam(value = "drugId", required = false) long drugId,
            @ApiParam(name = "icd10Id", value = "Icd10内码", defaultValue = "")
            @RequestParam(value = "icd10Id", required = false) long icd10Id) throws Exception {
        return icd10DrugRelationService.isExist(icd10Id,drugId);
    }

    @RequestMapping(value = "/icd10_drug_relations/icd10_ids" , method = RequestMethod.GET)
    @ApiOperation(value = "根据ICD10Id列表获取IDC10和药品关系")
    public List<MIcd10DrugRelation> getIcd10DrugRelationsByIcd10Ids(
            @ApiParam(name = "icd10_ids", value = "icd10_ids", defaultValue = "")
            @RequestParam(value = "icd10_ids", required = false) String[] icd10Ids) throws Exception {
        long[] longIds = new long[icd10Ids.length];
        for(int i=0; i<icd10Ids.length;i++){
            longIds[i] = Long.parseLong(icd10Ids[i]);
        }
        List<Icd10DrugRelation> icd10DrugRelations = icd10DrugRelationService.getIcd10DrugRelationsByIcd10Ids(longIds);
        return ( List<MIcd10DrugRelation>)convertToModels(icd10DrugRelations,new ArrayList<MIcd10DrugRelation>(icd10DrugRelations.size()),MIcd10DrugRelation.class,"");
    }
}