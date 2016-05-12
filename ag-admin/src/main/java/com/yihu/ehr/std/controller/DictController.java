package com.yihu.ehr.std.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.standard.datasset.DataSetModel;
import com.yihu.ehr.agModel.standard.dict.DictEntryModel;
import com.yihu.ehr.agModel.standard.dict.DictModel;
import com.yihu.ehr.constants.AgAdminConstants;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.standard.*;
import com.yihu.ehr.std.service.CDAVersionClient;
import com.yihu.ehr.std.service.DataSetClient;
import com.yihu.ehr.std.service.DictClient;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by AndyCai on 2016/1/25.
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin/std")
@RestController
public class DictController extends BaseController {

    @Autowired
    private DictClient dictClient;

    @Autowired
    private CDAVersionClient versionClient;

    @Autowired
    private DataSetClient dataSetClient;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/dicts", method = RequestMethod.GET)
    @ApiOperation(value = "查询字典")
    public Envelop searchDicts(
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
            @RequestParam(value = "version") String version) throws Exception {

        ResponseEntity<Collection<MStdDict>> responseEntity = dictClient.searchDict(fields, filters, sorts, size, page, version);

        List<DictModel> dictModelList = (List<DictModel>) convertToModels(responseEntity.getBody(), new ArrayList<DictModel>(responseEntity.getBody().size()), DictModel.class, null);
        MCDAVersion mcdaVersion = versionClient.getVersion(version);
        if(dictModelList!=null)
        {
            for(int i=0;i<dictModelList.size();i++)
            {
                dictModelList.get(i).setInStage(mcdaVersion.isInStage()?0:1);
            }
        }
        Envelop envelop = getResult(dictModelList, getTotalCount(responseEntity), page, size);

        return envelop;
    }



    @RequestMapping(value = "/dicts/no_paging", method = RequestMethod.GET)
    @ApiOperation(value = "标准字典条件搜索(不分页)")
    public Envelop searchDictsWithoutPaging(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version) throws Exception {
        Envelop envelop = new Envelop();
        ResponseEntity<Collection<DictModel>> responseEntity = dictClient.search(filters,version);
        List<DictModel> stdSources = (List<DictModel>) responseEntity.getBody();
        envelop.setDetailModelList(stdSources);
        envelop.setSuccessFlg(true);
        return envelop;
    }



    @RequestMapping(value = "/dict", method = RequestMethod.GET)
    @ApiOperation(value = "根据dictid，version获取字典信息")
    public Envelop getDictById(
            @ApiParam(name = "version_code", value = "标准版本代码")
            @RequestParam(value = "version_code") String versionCode,
            @ApiParam(name = "dictId", value = "字典ID")
            @RequestParam(value = "dictId") long dictId) {

        MStdDict mStdDict = dictClient.getCdaDictInfo(dictId, versionCode);
        DictModel dictModel = convertToModel(mStdDict, DictModel.class);

        if (dictModel == null) {
            return failed("数据获取失败!");
        }
        MCDAVersion mcdaVersion = versionClient.getVersion(versionCode);
        dictModel.setInStage(mcdaVersion.isInStage()?0:1);

        return success(dictModel);
    }


    @RequestMapping(value = "/save_dict", method = RequestMethod.POST)
    public Envelop saveDict(
            @ApiParam(name = "version_code", value = "版本号")
            @RequestParam(value = "version_code") String versionCode,
            @ApiParam(name = "json_data", value = "字典信息")
            @RequestParam(value = "json_data") String jsonData) {

        try {
            DictModel dictModel = objectMapper.readValue(jsonData, DictModel.class);
            String errorMsg = "";
            if (StringUtils.isEmpty(versionCode)) {
                errorMsg += "版本号不能为空!";
            }
            if (StringUtils.isEmpty(dictModel.getCode())) {
                errorMsg += "代码不能为空!";
            }
            if (StringUtils.isEmpty(dictModel.getName())) {
                errorMsg += "名称不能为空!";
            }
            if (StringUtils.isNotEmpty(errorMsg)) {
                return failed(errorMsg);
            }

            //代码唯一性校验
            boolean isExist = dictClient.isExistDictCode(dictModel.getCode(),versionCode);

            MStdDict mStdDict = convertToModel(dictModel, MStdDict.class);
            if (dictModel.getId() == 0) {
                if(isExist)
                {
                    return failed("代码已存在!");
                }
                mStdDict = dictClient.addDict(versionCode, objectMapper.writeValueAsString(mStdDict));
            } else {
                MStdDict stdDict = dictClient.getCdaDictInfo(mStdDict.getId(),versionCode);
                if(stdDict==null)
                {
                    return failed("字典不存在!");
                }
                if(!stdDict.getCode().equals(mStdDict.getCode())
                        && isExist)
                {
                    return failed("代码已存在!");
                }

                mStdDict.setCreateDate(StringToDate(dictModel.getCreateDate(), AgAdminConstants.DateTimeFormat));
                mStdDict = dictClient.updateDict(versionCode, mStdDict.getId(), objectMapper.writeValueAsString(mStdDict));
            }
            dictModel = convertToModel(mStdDict, DictModel.class);
            if (dictModel == null) {
                return failed("保存失败!");
            }
            dictModel.setCreateDate(DateToString(mStdDict.getCreateDate(),AgAdminConstants.DateTimeFormat));
            return success(dictModel);
        } catch (Exception ex) {
            return failedSystem();
        }
    }

//    @RequestMapping(value = "/updateDict",method = RequestMethod.POST)
//    public Envelop updateDict(
//                             @ApiParam(name = "dictId", value = "字典ID")
//                             @RequestParam(value = "dictId") String dictId,
//                             @ApiParam(name = "code", value = "字典代码")
//                             @RequestParam(value = "code") String code,
//                             @ApiParam(name = "name", value = "字典名称")
//                             @RequestParam(value = "name") String name,
//                             @ApiParam(name = "baseDict", value = "父级字典ID")
//                             @RequestParam(value = "baseDict") String baseDict,
//                             @ApiParam(name = "stdSource", value = "标准来源ID")
//                             @RequestParam(value = "stdSource") String stdSource,
//                             @ApiParam(name = "stdVersion", value = "标准版本号")
//                             @RequestParam(value = "stdVersion") String stdVersion,
//                             @ApiParam(name = "description", value = "说明")
//                             @RequestParam(value = "description") String description,
//                             @ApiParam(name = "userId", value = "用户ID")
//                             @RequestParam(value = "userId") String userId) throws Exception{
//
//        Envelop envelop = new Envelop();
//        ObjectMapper mapper = new ObjectMapper();
//        Map<String,Object> dictMap = new HashMap<>();
//
//        dictMap.put("id",dictId);
//        dictMap.put("code",code);
//        dictMap.put("name",name);
//        dictMap.put("baseDict",baseDict);
//        dictMap.put("sourceId",stdSource);
//        dictMap.put("description",description);
//        dictMap.put("author",userId);
//        dictMap.put("stdVersion",stdVersion);
//
//        String dictJsonModel = mapper.writeValueAsString(dictMap);
//
//        MStdDict mStdDict = dictClient.updateDict(stdVersion, dictJsonModel);
//
//        DictModel dictModel = convertToModel(mStdDict,DictModel.class);
//
//        if (dictModel != null) {
//            envelop.setSuccessFlg(true);
//            envelop.setObj(dictModel);
//        }else {
//            envelop.setSuccessFlg(false);
//            envelop.setErrorMsg("标准字典修改失败");
//        }
//
//        return envelop;
//    }

    @RequestMapping(value = "/dict", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除字典")
    public Envelop deleteDict(
            @ApiParam(name = "versionCode", value = "标准版本代码")
            @RequestParam(value = "versionCode") String versionCode,
            @ApiParam(name = "dictId", value = "字典ID")
            @RequestParam(value = "dictId") String ids) {

        Envelop envelop = new Envelop();

        ids = trimEnd(ids, ",");
        if (StringUtils.isEmpty(ids)) {
            return failed("请选择需要删除的数据!");
        }

        envelop = isDeleteDict(ids,versionCode);
        if (!envelop.isSuccessFlg()){
            return envelop;
        }

        boolean result = dictClient.deleteDicts(versionCode, ids);

        if (!result) {
            return failed("删除失败!");
        }
        return success(null);
    }

    public Envelop isDeleteDict(String id,String versionCode){

        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);

        try {

            //查询所有版本
            ResponseEntity<Collection<MCDAVersion>> responseEntity = versionClient.searchCDAVersions("", "", "", 1000, 1);
            Collection<MCDAVersion> mCdaVersions = responseEntity.getBody();

            ResponseEntity<Collection<MStdDict>> dict = dictClient.searchDict("", "baseDict=" + id, "", id.length(), 1, versionCode);
            List<DictModel> dictModelList = (List<DictModel>) convertToModels(dict.getBody(), new ArrayList<DictModel>(dict.getBody().size()), DictModel.class, null);

            if (dict.getBody().size()>0){
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("该字典正被"+dictModelList.get(0).getName()+"作为基础字典使用，不可删除");
                return envelop;
            }

            for (MCDAVersion mcdaVersion : mCdaVersions){
                List<MStdDataSet> mStdDataSetList = dataSetClient.search("",mcdaVersion.getVersion());

                for (MStdDataSet mStdDataSet:mStdDataSetList){
                    ResponseEntity<Collection<MStdMetaData>> metaDatas = dataSetClient.searchMetaDatas("", "dataSetId=" + mStdDataSet.getId() + " g1;dictId=" + id + " g2", "", id.length(), 1, mcdaVersion.getVersion());
                    List<MStdMetaData> mStdMetaDatas = (List<MStdMetaData>) metaDatas.getBody();
                    if(mStdMetaDatas.size()>0){
                        envelop.setSuccessFlg(false);
                        envelop.setErrorMsg("该字典正被"+mcdaVersion.getVersionName()+"版本，"+mStdDataSet.getName()+"数据集，"+mStdMetaDatas.get(0).getName()+"数据元使用，不可删除");
                        return envelop;
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

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


    @RequestMapping(value = "/dict_entry", method = RequestMethod.POST)
    @ApiOperation(value = "新增字典项")
    public Envelop saveDictEntry(
            @ApiParam(name = "version_code", value = "标准版本代码")
            @RequestParam(value = "version_code") String versionCode,
            @ApiParam(name = "json_data", value = "标准版本代码")
            @RequestParam(value = "json_data") String jsonData) {

        try {
            DictEntryModel dictEntryModel = objectMapper.readValue(jsonData, DictEntryModel.class);

            String errorMsg = "";
            if (StringUtils.isEmpty(versionCode)) {
                errorMsg += "版本号不能为空!";
            }
            if (StringUtils.isEmpty(dictEntryModel.getCode())) {
                errorMsg += "代码不能为空!";
            }
            if (StringUtils.isEmpty(dictEntryModel.getValue())) {
                errorMsg += "值不能为空!";
            }
            if (dictEntryModel.getDictId() == 0) {
                errorMsg += "请选择对应的字典!";
            }
            if (StringUtils.isNotEmpty(errorMsg)) {
                return failed(errorMsg);
            }
            //代码唯一性校验
            boolean isExist = dictClient.isExistEntryCode(dictEntryModel.getDictId(),dictEntryModel.getCode(),versionCode);
            MStdDictEntry mStdDictEntry = convertToModel(dictEntryModel, MStdDictEntry.class);
            if (dictEntryModel.getId() == 0) {
                if(isExist)
                {
                    return failed("代码已存在!");
                }
                mStdDictEntry = dictClient.addDictEntry(versionCode, objectMapper.writeValueAsString(mStdDictEntry));
            } else {
                MStdDictEntry dictEntry = dictClient.getDictEntry(mStdDictEntry.getId(),versionCode);
                if(dictEntry==null)
                {
                    return failed("字典项不存在!");
                }
                if(!dictEntry.getCode().equals(mStdDictEntry.getCode())
                        && isExist)
                {
                    return failed("代码已存在!");
                }

                mStdDictEntry = dictClient.updateDictEntry(versionCode, mStdDictEntry.getId(), objectMapper.writeValueAsString(mStdDictEntry));
            }
            dictEntryModel = convertToModel(mStdDictEntry, DictEntryModel.class);
            if (dictEntryModel == null) {
                return failed("保存失败!");
            }
            return success(dictEntryModel);
        } catch (Exception ex) {
            return failedSystem();
        }
    }

//    @RequestMapping(value = "/updateDictEntry",method = RequestMethod.POST)
//    @ApiOperation(value = "修改字典项")
//    public Envelop updateDictEntry(
//                                  @ApiParam(name = "versionCode", value = "标准版本代码")
//                                  @RequestParam(value = "versionCode") String versionCode,
//                                  @ApiParam(name = "dictId", value = "字典ID")
//                                  @RequestParam(value = "dictId") String dictId,
//                                  @ApiParam(name = "entryId",value = "字典项ID")
//                                  @RequestParam(value = "entryId")String entryId,
//                                  @ApiParam(name = "entryCode",value = "字典项代码")
//                                  @RequestParam(value = "entryCode")String entryCode,
//                                  @ApiParam(name = "entryValue",value = "字典项值")
//                                  @RequestParam(value = "entryValue")String entryValue,
//                                  @ApiParam(name = "description",value = "说明")
//                                  @RequestParam(value = "description")String description) throws JsonProcessingException {
//
//        Envelop envelop = new Envelop();
//        ObjectMapper mapper = new ObjectMapper();
//        Map<String,Object> dictEntryMap = new HashMap<>();
//
//        dictEntryMap.put("id",entryId);
//        dictEntryMap.put("dictId",dictId);
//        dictEntryMap.put("code",entryCode);
//        dictEntryMap.put("value",entryValue);
//        dictEntryMap.put("desc",description);
//
//        String dictEntryJsonModel = mapper.writeValueAsString(dictEntryMap);
//
//        MStdDictEntry mStdDictEntry = dictClient.updateDictEntry(versionCode,dictEntryJsonModel);
//        DictEntryModel dictEntryModel = convertToModel(mStdDictEntry,DictEntryModel.class);
//
//        if (dictEntryModel != null){
//            envelop.setSuccessFlg(true);
//            envelop.setObj(dictEntryModel);
//        }else {
//            envelop.setSuccessFlg(false);
//            envelop.setErrorMsg("修改字典项失败");
//        }
//
//        return envelop;
//    }

    @RequestMapping(value = "/dict_entrys", method = RequestMethod.GET)
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
            @RequestParam(value = "version") String version) {

        ResponseEntity<Collection<MStdDictEntry>> responseEntity = dictClient.searchDictEntry(fields, filters, sorts, size, page, version);
        List<DictEntryModel> dictModelList = (List<DictEntryModel>) convertToModels(responseEntity.getBody(), new ArrayList<DictEntryModel>(responseEntity.getBody().size()), DictEntryModel.class, null);

        Envelop envelop = getResult(dictModelList, getTotalCount(responseEntity), page, size);

        return envelop;
    }

    @RequestMapping(value = "/dict_entry", method = RequestMethod.GET)
    @ApiOperation(value = "获取字典项")
    public Envelop getDictEntryById(
            @ApiParam(name = "versionCode", value = "标准版本代码")
            @RequestParam(value = "versionCode") String versionCode,
            @ApiParam(name = "entryId", value = "字典项ID")
            @RequestParam(value = "entryId") long entryId) {

        MStdDictEntry mStdDictEntry = dictClient.getDictEntry(entryId, versionCode);
        DictEntryModel dictEntryModel = convertToModel(mStdDictEntry, DictEntryModel.class);

        if (dictEntryModel == null) {
            return failed("数据获取失败!");
        }
//        MCDAVersion mcdaVersion = versionClient.getVersion(versionCode);
//        dictEntryModel.setStaged(mcdaVersion.isInStage()?0:1);

        return success(dictEntryModel);
    }

    @RequestMapping(value = "/dict_entry", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除字典项", produces = "application/json", notes = "删除字典项信息，多ID删除时，Id以逗号隔开")
    public Envelop deleteDictEntry(
            @ApiParam(name = "versionCode", value = "标准版本代码")
            @RequestParam(value = "versionCode") String versionCode,
            /*@ApiParam(name = "dictId", value = "字典ID")
            @RequestParam(value = "dictId") long dictId,*/
            @ApiParam(name = "entryIds", value = "字典项ID")
            @RequestParam(value = "entryIds") String entryIds) {

        entryIds = trimEnd(entryIds, ",");
        if (StringUtils.isEmpty(entryIds)) {
            return failed("请选择需要删除的数据!");
        }
        boolean result = dictClient.deleteDictEntrys(versionCode, entryIds);
        if (!result) {
            return failed("删除失败!");
        }
        return success(null);
    }

    @RequestMapping(value = "/dict/is_exist/code",method = RequestMethod.GET)
    public boolean isExistDictCode(
            @RequestParam(value = "dict_code")String dictCode,
            @RequestParam("version_code")String versionCode){
        return dictClient.isExistDictCode(dictCode,versionCode);
    }

    @RequestMapping(value = "/dict_entry/is_exist/code",method = RequestMethod.GET)
    public boolean isExistEntryCode(
            @RequestParam(value = "dict_id")long dictId,
            @RequestParam(value = "code")String code,
            @RequestParam(value = "version_code")String versionCode){
        return dictClient.isExistEntryCode(dictId,code,versionCode);
    }

}
