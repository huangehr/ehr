package com.yihu.ehr.ha.std.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.standard.dict.DictEntryModel;
import com.yihu.ehr.agModel.standard.dict.DictModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.ha.std.service.DictClient;
import com.yihu.ehr.model.standard.MStdDict;
import com.yihu.ehr.model.standard.MStdDictEntry;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by AndyCai on 2016/1/25.
 */
@RequestMapping(ApiVersion.Version1_0 + "/dict")
@RestController
public class DictController extends BaseController{

    @Autowired
    private DictClient dictClient;


    @RequestMapping(value = "/dicts",method = RequestMethod.GET)
    @ApiOperation(value = "查询字典")
    public Envelop searchDataSets(
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
            @RequestParam(value = "version") String version){

        Collection<MStdDict> mStdDictCollection = dictClient.searchDict(fields, filters, sorts, size, page, version);
        List<DictModel> dictModelList = (List<DictModel>)convertToModels(mStdDictCollection,new ArrayList<DictModel>(mStdDictCollection.size()),DictModel.class,null);

        //TODO:获取总条数
//        String count = response.getHeader(AgAdminConstants.ResourceCount);
//        int totalCount = StringUtils.isNotEmpty(count) ? Integer.parseInt(count) : 0;

        Envelop envelop = getResult(dictModelList,0,page,size);

        return envelop;
    }

    @RequestMapping(value = "/dict",method = RequestMethod.GET)
    @ApiOperation(value = "根据dictid，version获取字典信息")
    public Envelop getDictById(
                              @ApiParam(name = "versionCode", value = "标准版本代码")
                              @RequestParam(value = "versionCode") String versionCode,
                              @ApiParam(name = "dictId",value = "字典ID")
                              @RequestParam(value = "dictId")long dictId){

        Envelop envelop = new Envelop();

        MStdDict mStdDict = dictClient.getCdaDictInfo(dictId, versionCode);
        DictModel dictModel = convertToModel(mStdDict,DictModel.class);

        if(dictModel != null){
            envelop.setObj(dictModel);
            envelop.setSuccessFlg(true);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("获取字典信息失败");
        }

        return envelop;
    }



    @RequestMapping(value = "/createDict",method = RequestMethod.POST)
    public Envelop createDict(
                           @ApiParam(name = "dictId", value = "字典ID")
                           @RequestParam(value = "dictId") String dictId,
                           @ApiParam(name = "code", value = "字典代码")
                           @RequestParam(value = "code") String code,
                           @ApiParam(name = "name", value = "字典名称")
                           @RequestParam(value = "name") String name,
                           @ApiParam(name = "baseDict", value = "父级字典ID")
                           @RequestParam(value = "baseDict") String baseDict,
                           @ApiParam(name = "stdSource", value = "标准来源ID")
                           @RequestParam(value = "stdSource") String stdSource,
                           @ApiParam(name = "stdVersion", value = "标准版本号")
                           @RequestParam(value = "stdVersion") String stdVersion,
                           @ApiParam(name = "description", value = "说明")
                           @RequestParam(value = "description") String description,
                           @ApiParam(name = "userId", value = "用户ID")
                           @RequestParam(value = "userId") String userId) throws JsonProcessingException {

        Envelop envelop = new Envelop();
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> dictMap = new HashMap<>();

        dictMap.put("id",dictId);
        dictMap.put("code",code);
        dictMap.put("name",name);
        dictMap.put("baseDict",baseDict);
        dictMap.put("sourceId",stdSource);
        dictMap.put("description",description);
        dictMap.put("author",userId);
        dictMap.put("stdVersion",stdVersion);

        String dictJsonModel = mapper.writeValueAsString(dictMap);

        MStdDict mStdDict = dictClient.addDict(stdVersion, dictJsonModel);

        DictModel dictModel = convertToModel(mStdDict,DictModel.class);

        if (dictModel != null) {
            envelop.setSuccessFlg(true);
            envelop.setObj(dictModel);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("标准字典新增失败");
        }

        return envelop;
    }

    @RequestMapping(value = "/updateDict",method = RequestMethod.POST)
    public Envelop updateDict(
                             @ApiParam(name = "dictId", value = "字典ID")
                             @RequestParam(value = "dictId") String dictId,
                             @ApiParam(name = "code", value = "字典代码")
                             @RequestParam(value = "code") String code,
                             @ApiParam(name = "name", value = "字典名称")
                             @RequestParam(value = "name") String name,
                             @ApiParam(name = "baseDict", value = "父级字典ID")
                             @RequestParam(value = "baseDict") String baseDict,
                             @ApiParam(name = "stdSource", value = "标准来源ID")
                             @RequestParam(value = "stdSource") String stdSource,
                             @ApiParam(name = "stdVersion", value = "标准版本号")
                             @RequestParam(value = "stdVersion") String stdVersion,
                             @ApiParam(name = "description", value = "说明")
                             @RequestParam(value = "description") String description,
                             @ApiParam(name = "userId", value = "用户ID")
                             @RequestParam(value = "userId") String userId) throws Exception{

        Envelop envelop = new Envelop();
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> dictMap = new HashMap<>();

        dictMap.put("id",dictId);
        dictMap.put("code",code);
        dictMap.put("name",name);
        dictMap.put("baseDict",baseDict);
        dictMap.put("sourceId",stdSource);
        dictMap.put("description",description);
        dictMap.put("author",userId);
        dictMap.put("stdVersion",stdVersion);

        String dictJsonModel = mapper.writeValueAsString(dictMap);

        MStdDict mStdDict = dictClient.updateDict(stdVersion, dictJsonModel);

        DictModel dictModel = convertToModel(mStdDict,DictModel.class);

        if (dictModel != null) {
            envelop.setSuccessFlg(true);
            envelop.setObj(dictModel);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("标准字典修改失败");
        }

        return envelop;
    }

    @RequestMapping(value = "deleteDict",method = RequestMethod.DELETE)
    @ApiOperation(value = "删除字典")
    public Envelop deleteDict(
                             @ApiParam(name = "versionCode", value = "标准版本代码")
                             @RequestParam(value = "versionCode") String versionCode,
                             @ApiParam(name = "dictId", value = "字典ID")
                             @RequestParam(value = "dictId") String ids) {

        Envelop envelop = new Envelop();

        String[] dictId = ids.split(",");
        boolean bo;

        if(dictId.length>1){
            //批量删除
            bo = dictClient.deleteDicts(versionCode,ids);
        }else {
            //单个删除
            long id = Long.valueOf(ids);
            bo = dictClient.deleteDict(versionCode,id);
        }

        envelop.setSuccessFlg(bo);

        return envelop;
    }




//    @RequestMapping(value = "/getCdaDictList",method = RequestMethod.GET)
//    public Object getCdaDictList(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
//                                 @PathVariable(value = "apiVersion") String apiVersion,
//                                 @ApiParam(name = "versionCode", value = "标准版本代码")
//                                 @RequestParam(value = "versionCode") String versionCode,
//                                 @ApiParam(name = "code", value = "字典代码")
//                                 @RequestParam(value = "code") String code,
//                                 @ApiParam(name = "name", value = "字典名称")
//                                 @RequestParam(value = "name") String name,
//                                 @ApiParam(name = "page", value = "当前页", defaultValue = "1")
//                                 @RequestParam(value = "page") int page,
//                                 @ApiParam(name = "rows", value = "每页行数", defaultValue = "20")
//                                 @RequestParam(value = "rows") int rows) {
//        return null;
//    }


//    @RequestMapping(value = "/getCdaBaseDictList",method = RequestMethod.GET)
//    public Object getCdaBaseDictList(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
//                                     @PathVariable(value = "apiVersion") String apiVersion,
//                                     @ApiParam(name = "versionCode", value = "标准版本代码")
//                                     @RequestParam(value = "versionCode") String versionCode,
//                                     @ApiParam(name = "dictId", value = "字典ID")
//                                     @RequestParam(value = "dictId") String dictId,
//                                     @ApiParam(name = "page", value = "当前页", defaultValue = "1")
//                                     @RequestParam(value = "page") int page,
//                                     @ApiParam(name = "rows", value = "每页行数", defaultValue = "20")
//                                     @RequestParam(value = "rows") int rows) {
//        return null;
//    }











    @RequestMapping(value = "/createDictEntry",method = RequestMethod.POST)
    @ApiOperation(value = "新增字典项")
      public Envelop createDictEntry(
                                    @ApiParam(name = "versionCode", value = "标准版本代码")
                                    @RequestParam(value = "versionCode") String versionCode,
                                    @ApiParam(name = "dictId", value = "字典ID")
                                    @RequestParam(value = "dictId") String dictId,
                                    @ApiParam(name = "entryId",value = "字典项ID")
                                    @RequestParam(value = "entryId")String entryId,
                                    @ApiParam(name = "entryCode",value = "字典项代码")
                                    @RequestParam(value = "entryCode")String entryCode,
                                    @ApiParam(name = "entryValue",value = "字典项值")
                                    @RequestParam(value = "entryValue")String entryValue,
                                    @ApiParam(name = "description",value = "说明")
                                    @RequestParam(value = "description")String description) throws JsonProcessingException {

        Envelop envelop = new Envelop();
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> dictEntryMap = new HashMap<>();

        dictEntryMap.put("id",entryId);
        dictEntryMap.put("dictId",dictId);
        dictEntryMap.put("code",entryCode);
        dictEntryMap.put("value",entryValue);
        dictEntryMap.put("desc",description);

        String  dictEntryJsonModel = mapper.writeValueAsString(dictEntryMap);

        MStdDictEntry mStdDictEntry = dictClient.addDictEntry(versionCode, dictEntryJsonModel);
        DictEntryModel dictEntryModel = convertToModel(mStdDictEntry,DictEntryModel.class);

        if (dictEntryModel != null){
            envelop.setSuccessFlg(true);
            envelop.setObj(dictEntryModel);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("字典项新增失败");
        }

        return envelop;
    }

    @RequestMapping(value = "/updateDictEntry",method = RequestMethod.POST)
    @ApiOperation(value = "修改字典项")
    public Envelop updateDictEntry(
                                  @ApiParam(name = "versionCode", value = "标准版本代码")
                                  @RequestParam(value = "versionCode") String versionCode,
                                  @ApiParam(name = "dictId", value = "字典ID")
                                  @RequestParam(value = "dictId") String dictId,
                                  @ApiParam(name = "entryId",value = "字典项ID")
                                  @RequestParam(value = "entryId")String entryId,
                                  @ApiParam(name = "entryCode",value = "字典项代码")
                                  @RequestParam(value = "entryCode")String entryCode,
                                  @ApiParam(name = "entryValue",value = "字典项值")
                                  @RequestParam(value = "entryValue")String entryValue,
                                  @ApiParam(name = "description",value = "说明")
                                  @RequestParam(value = "description")String description) throws JsonProcessingException {

        Envelop envelop = new Envelop();
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> dictEntryMap = new HashMap<>();

        dictEntryMap.put("id",entryId);
        dictEntryMap.put("dictId",dictId);
        dictEntryMap.put("code",entryCode);
        dictEntryMap.put("value",entryValue);
        dictEntryMap.put("desc",description);

        String dictEntryJsonModel = mapper.writeValueAsString(dictEntryMap);

        MStdDictEntry mStdDictEntry = dictClient.updateDictEntry(versionCode,dictEntryJsonModel);
        DictEntryModel dictEntryModel = convertToModel(mStdDictEntry,DictEntryModel.class);

        if (dictEntryModel != null){
            envelop.setSuccessFlg(true);
            envelop.setObj(dictEntryModel);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("修改字典项失败");
        }

        return envelop;
    }

    @RequestMapping(value = "/std/dict/entrys", method = RequestMethod.GET)
    @ApiOperation(value = "查询字典项")
    public Envelop searchDictEntry(
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
            @RequestParam(value = "version") String version){

        Collection<MStdDictEntry> mStdDictEntryCollection = dictClient.searchDictEntry(fields, filters, sorts, size, page, version);
        List<DictEntryModel> dictModelList = (List<DictEntryModel>)convertToModels(mStdDictEntryCollection,new ArrayList<DictEntryModel>(mStdDictEntryCollection.size()),DictEntryModel.class,null);

        //TODO:获取总条数
//        String count = response.getHeader(AgAdminConstants.ResourceCount);
//        int totalCount = StringUtils.isNotEmpty(count) ? Integer.parseInt(count) : 0;

        Envelop envelop = getResult(dictModelList,0,page,size);

        return envelop;


    }

    @RequestMapping(value = "/dictEntry",method = RequestMethod.GET)
    @ApiOperation(value = "获取字典项")
    public Envelop getDictEntryById(
                                   @ApiParam(name = "versionCode", value = "标准版本代码")
                                   @RequestParam(value = "versionCode") String versionCode,
                                   @ApiParam(name="entryId",value = "字典项ID")
                                   @RequestParam(value = "entryId")long entryId){

        Envelop envelop = new Envelop();

        MStdDictEntry mStdDictEntry = dictClient.getDictEntry(entryId,versionCode);
        DictEntryModel dictEntryModel = convertToModel(mStdDictEntry,DictEntryModel.class);

        if (dictEntryModel != null){
            envelop.setSuccessFlg(true);
            envelop.setObj(dictEntryModel);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("获取字典项失败");
        }

        return envelop;
    }

    @RequestMapping(value = "/dictEntry",method = RequestMethod.DELETE)
    @ApiOperation(value = "删除字典项", produces = "application/json", notes = "删除字典项信息，多ID删除时，Id以逗号隔开")
    public Envelop deleteDictEntry(
                                  @ApiParam(name = "versionCode", value = "标准版本代码")
                                  @RequestParam(value = "versionCode") String versionCode,
                                  @ApiParam(name = "dictId", value = "字典ID")
                                  @RequestParam(value = "dictId") long dictId,
                                  @ApiParam(name = "entryIds", value = "字典项ID")
                                  @RequestParam(value = "entryIds") String entryIds) {

        Envelop envelop = new Envelop();
        boolean bo;

        String[] dictEntrys = entryIds.split(",");

        if (dictEntrys.length>1){
            //批量删除
            bo = dictClient.deleteDictEntrys(versionCode,dictId,entryIds);
        }else{
            //单个删除
            long id = Long.valueOf(entryIds);
            bo = dictClient.deleteDictEntry(versionCode,dictId,id);
        }

        envelop.setSuccessFlg(bo);
        return envelop;
    }




}
