package com.yihu.ehr.standard.dict.controller;

import com.yihu.ehr.api.RestApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.standard.MCDAType;
import com.yihu.ehr.model.standard.MStdDict;
import com.yihu.ehr.model.standard.MStdSource;
import com.yihu.ehr.standard.cdatype.service.CDAType;
import com.yihu.ehr.standard.commons.ExtendController;
import com.yihu.ehr.standard.dict.service.Dict;
import com.yihu.ehr.standard.dict.service.DictService;
import com.yihu.ehr.standard.dict.service.IDict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.1
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "dictionary", description = "标准字典", tags = {"标准字典"})
public class DictController extends ExtendController<MStdDict> {

    @Autowired
    private DictService dictService;

    private Class getServiceEntity(String version){
        return dictService.getServiceEntity(version);
    }


    @RequestMapping(value = RestApi.Standards.Dictionaries, method = RequestMethod.GET)
    @ApiOperation(value = "查询字典")
    public Collection<MStdDict> searchDataSets(
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
            @RequestParam(value = "version") String version,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception{

        Class entityClass = getServiceEntity(version);
        List ls = dictService.search(entityClass, fields, filters, sorts, page, size);
        pagedResponse(request, response, dictService.getCount(entityClass, filters), page, size);
        return convertToModels(ls, new ArrayList<>(ls.size()), MStdDict.class, fields);
    }


    @RequestMapping(value = RestApi.Standards.NoPageDictionaries, method = RequestMethod.GET)
    @ApiOperation(value = "标准字典不分页搜索")
    public Collection<MStdDict> searchSourcesWithoutPaging(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version) throws Exception {
        Class entityClass = getServiceEntity(version);
        List dictList = dictService.search(entityClass,filters);
        return convertToModels(dictList, new ArrayList<>(dictList.size()), MStdDict.class, "");
    }


    @RequestMapping(value = RestApi.Standards.Dictionaries, method = RequestMethod.POST)
    @ApiOperation(value = "新增字典")
    public MStdDict addDict(
            @ApiParam(name = "version", value = "标准版本", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam(value = "model") String model) throws Exception{

        Class entityClass = getServiceEntity(version);
        IDict dict = (IDict) jsonToObj(model, entityClass);
//        if(dictService.isExistByField("code", dict.getCode(), entityClass))
//            throw errRepeatCode();

        dict.setCreateDate(new Date());
        if(dictService.add(dict, version))
            return getModel(dict);
         return null;
    }


    @RequestMapping(value = RestApi.Standards.Dictionary, method = RequestMethod.PUT)
    @ApiOperation(value = "修改字典")
    public MStdDict updateDict(
            @ApiParam(name = "version", value = "标准版本", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam(value = "model") String model) throws Exception{

        Class entityClass = getServiceEntity(version);
        IDict dictModel = (IDict) jsonToObj(model, entityClass);
//        IDict dict = dictService.retrieve(id, entityClass);
//        if(!dict.getCode().equals(dictModel.getCode()) &&
//                dictService.isExistByField("code", dictModel.getCode(), entityClass))
//            throw errRepeatCode();
        dictModel.setId(id);
        dictService.save(dictModel);
        return getModel(dictModel);
    }


    @RequestMapping(value = RestApi.Standards.Dictionary, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除字典")
    public boolean deleteDict(
            @ApiParam(name = "version", value = "cda版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") long id) throws Exception{

        return dictService.removeDicts(new Object[]{id}, version) > 0;
    }


    @RequestMapping(value = RestApi.Standards.Dictionaries, method = RequestMethod.DELETE)
    @ApiOperation(value = "批量删除字典")
    public boolean deleteDicts(
            @ApiParam(name = "version", value = "cda版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "ids", value = "编号", defaultValue = "")
            @RequestParam(value = "ids") String ids) throws Exception{

        return dictService.removeDicts(strToLongArr(ids), version) > 0;
    }


    @RequestMapping(value = RestApi.Standards.Dictionary, method = RequestMethod.GET)
    @ApiOperation(value = "获取字典详细信息")
    public MStdDict getCdaDictInfo(
            @ApiParam(name = "id", value = "字典编号", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "version", value = "版本编号", defaultValue = "")
            @RequestParam(value = "version") String version) throws Exception{

        return getModel(dictService.retrieve(id, getServiceEntity(version)));
    }


    @RequestMapping(value = RestApi.Standards.MetaDataWithDict, method = RequestMethod.GET)
    @ApiOperation(value = "获取字典 map集")
    public Map getDictMapByIds(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "data_set_id", value = "数据集编号", defaultValue = "")
            @PathVariable(value = "data_set_id") Long dataSetId,
            @ApiParam(name = "meta_data_id", value = "数据元编号", defaultValue = "")
            @PathVariable(value = "meta_data_id") Long metaDataId) {

        return dictService.getDictMapByIds(version, dataSetId, metaDataId);
    }

    @RequestMapping(value = RestApi.Standards.DictCodeIsExist,method = RequestMethod.GET)
    public boolean isExistDictCode(
            @RequestParam(value = "dict_code")String dictCode,
            @RequestParam("version_code")String versionCode)
    {
        Class entityClass = getServiceEntity(versionCode);
        return dictService.isExistByField("code",dictCode , entityClass);
    }


    @RequestMapping(value = RestApi.Standards.DictParent, method = RequestMethod.GET)
    @ApiOperation(value = "根据当前类别获取自己的父级以及同级以及同级所在父级类别列表")
    public List<MStdDict> getCdaTypeExcludeSelfAndChildren(
            @ApiParam(name = "id", value = "id")
            @RequestParam(value = "id") long id,
            @ApiParam(name = "version", value = "版本编号", defaultValue = "")
            @RequestParam(value = "version") String version) throws Exception {
        List<IDict> dicts = new ArrayList<>();

        IDict dict = dictService.retrieve(id, getServiceEntity(version));
        dicts.add(dict);
        String childrenIds = getChildIncludeSelfByParentsAndChildrenIds(dicts,"",version);   //递归获取
        List<IDict> returnDicts = dictService.getCdaTypeExcludeSelfAndChildren(childrenIds,version);

        return  (List<MStdDict>)convertToModels(returnDicts,new ArrayList<MStdDict>(returnDicts.size()),MStdDict.class,"");
    }


    /**
     * 根据父级类别获取父级类别所在以下所有子集类别（包括当前父级列表）
     * @param parents 父级信息
     * @param childrenIds   子级ID
     */
    public String getChildIncludeSelfByParentsAndChildrenIds(List<IDict> parents,String childrenIds,String version) {
        for (int i = 0; i < parents.size(); i++) {
            IDict dict = parents.get(i);
            childrenIds+=dict.getId()+",";
            List<IDict> listChild = dictService.getChildrensByParentId(dict.getId(),version);
            if(listChild.size()>0){
                childrenIds = getChildIncludeSelfByParentsAndChildrenIds(listChild,childrenIds,version);
            }
        }
        return childrenIds;
    }

}
