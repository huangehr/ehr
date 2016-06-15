package com.yihu.ehr.std.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.standard.datasset.DataSetModel;
import com.yihu.ehr.agModel.standard.datasset.MetaDataModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.standard.*;
import com.yihu.ehr.std.service.*;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.ehr.controller.BaseController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by AndyCai on 2016/1/25.
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin/std")
@RestController
public class DataSetController extends BaseController {

    @Autowired
    private DataSetClient dataSetClient;

    @Autowired
    private DictClient dictClient;

    @Autowired
    private StandardSourceClient stdSourcrClient;

    @Autowired
    private CDAVersionClient versionClient;

    @Autowired
    private CDAClient cdaClient;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/data_sets", method = RequestMethod.GET)
    @ApiOperation(value = "查询数据集的方法")
    public Envelop getDataSets(
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

        ResponseEntity<Collection<MStdDataSet>> responseEntity = dataSetClient.searchDataSets(fields, filters, sorts, size, page, version);
        List<DataSetModel> dataSetModelList = (List<DataSetModel>) convertToModels(responseEntity.getBody(), new ArrayList<DataSetModel>(responseEntity.getBody().size()), DataSetModel.class, null);
        List<DataSetModel> dataSetModels = new ArrayList<>();
        for(DataSetModel dataSetModel:dataSetModelList){
            String reference = dataSetModel.getReference();
            if (!StringUtils.isEmpty(reference)){
                MStdSource mStdSource = stdSourcrClient.getStdSource(reference);
                dataSetModel.setReferenceCode(mStdSource == null ? "" : mStdSource.getCode());
            }
            dataSetModels.add(dataSetModel);
        }
        Envelop envelop = getResult(dataSetModels, getTotalCount(responseEntity), page, size);

        return envelop;
    }


    @RequestMapping(value = "/data_sets/no_paging", method = RequestMethod.GET)
    @ApiOperation(value = "标准数据集搜索(不分页)")
    public Envelop searchDataSetsWithoutPaging(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version) throws Exception {
        Envelop envelop = new Envelop();
        List<MStdDataSet> mStdDataSetList = dataSetClient.search(filters,version);
        List<DataSetModel> dataSetModelList = (List<DataSetModel>)convertToModels(mStdDataSetList,new ArrayList<DataSetModel>(mStdDataSetList.size()),DataSetModel.class,null);
        envelop.setDetailModelList(dataSetModelList);
        return envelop;
    }



    @RequestMapping(value = "/data_set/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据集信息")
    public Envelop deleteDataSet(
            @ApiParam(name = "id", value = "数据集编号", defaultValue = "")
            @PathVariable(value = "id") String ids,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version) {

        Envelop envelop = new Envelop();

        ids = trimEnd(ids, ",");
        if (StringUtils.isEmpty(ids)) {
            return failed("请选择需要删除的数据!");
        }

        envelop = isDeleteDataSet(ids,version,"该数据集正被当前版本的CDA文档使用，不可删除");
        if (!envelop.isSuccessFlg()){
            return envelop;
        }

        boolean bo = dataSetClient.deleteDataSet(ids, version);
        if (!bo) {
            return failed("删除失败!");
        }
        return success(null);
    }

    public Envelop isDeleteDataSet(String id,String version,String msg){

        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);

//        ResponseEntity<Collection<MCDAVersion>> responseEntity = versionClient.searchCDAVersions("", "", "", 1000, 1);
//        Collection<MCDAVersion> mCdaVersions = responseEntity.getBody();

//        for (MCDAVersion mcdaVersion : mCdaVersions) {
            ResponseEntity<Collection<MCdaDataSetRelationship>> mCdaDataSetRelationships = cdaClient.getCDADataSetRelationships("", "dataSetId=" + id, "", id.length(), 1, version);
            if (mCdaDataSetRelationships.getBody().size()>0){
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg(msg);
                return envelop;
            }
//        }

        return envelop;
    }

    @RequestMapping(value = "/data_set/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取数据集信息")
    public Envelop getDataSet(
            @ApiParam(name = "id", value = "数据集编号", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version) {

        MStdDataSet mStdDataSet = dataSetClient.getDataSet(id, version);

        if (mStdDataSet == null) {
            return failed("数据获取失败！");
        }
        DataSetModel dataSetModel = convertToModel(mStdDataSet, DataSetModel.class);
        return success(dataSetModel);
    }

    @RequestMapping(value = "/data_set", method = RequestMethod.POST)
    @ApiOperation(value = "新增,修改数据集信息")
    public Envelop saveDataSet(
            @ApiParam(name = "version_code", value = "标准版本")
            @RequestParam(value = "version_code") String versionCode,
            @ApiParam(name = "json_data", value = "数据集信息")
            @RequestParam(value = "json_data") String jsonData) {

        try {

            DataSetModel dataSetModel = objectMapper.readValue(jsonData, DataSetModel.class);

            String errorMsg = "";
            if (StringUtils.isEmpty(versionCode)) {
                errorMsg += "版本号不能为空!";
            }
            if (StringUtils.isEmpty(dataSetModel.getCode())) {
                errorMsg += "代码不能为空!";
            }
            if (StringUtils.isEmpty(dataSetModel.getName())) {
                errorMsg += "名称不能为空!";
            }
            if (StringUtils.isNotEmpty(errorMsg)) {
                return failed(errorMsg);
            }

            //代码唯一性校验
            boolean isExist = dataSetClient.isExistCode(dataSetModel.getCode(),versionCode);

            MStdDataSet mStdDataSet = convertToModel(dataSetModel, MStdDataSet.class);
            if (dataSetModel.getId() == 0) {
                if(isExist)
                {
                    return failed("代码已存在!");
                }
                mStdDataSet = dataSetClient.saveDataSet(versionCode, objectMapper.writeValueAsString(mStdDataSet));

            } else {
                MStdDataSet dataSet = dataSetClient.getDataSet(mStdDataSet.getId(),versionCode);
                if(dataSet==null)
                {
                    return failed("数据集不存在!");
                }
                if(!dataSet.getCode().equals(mStdDataSet.getCode())
                        && isExist)
                {
                    return failed("代码已存在!");
                }
                mStdDataSet = dataSetClient.updateDataSet(versionCode, mStdDataSet.getId(), objectMapper.writeValueAsString(mStdDataSet));
            }
            dataSetModel = convertToModel(mStdDataSet, DataSetModel.class);
            if (dataSetModel == null) {
                return failed("保存失败!");
            }
            return success(dataSetModel);
        } catch (Exception ex) {
            return failedSystem();
        }
    }


    @RequestMapping(value = "/meta_datas", method = RequestMethod.GET)
    @ApiOperation(value = "查询数据元")
    public Envelop getMetaDataByCodeOrName(
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
        try{
            ResponseEntity<Collection<MStdMetaData>> responseEntity = dataSetClient.searchMetaDatas(fields, filters, sorts, size, page, version);
            List<MStdMetaData> mStdMetaDatas = (List<MStdMetaData>) responseEntity.getBody();
            List<MetaDataModel> metaDataModels = new ArrayList<>();
            for (MStdMetaData m : mStdMetaDatas){
                MetaDataModel model = convertToModel(m,MetaDataModel.class);
                long dictId = m.getDictId();
                if(!(dictId == 0)){
                    MStdDict dict = dictClient.getCdaDictInfo(dictId, version);
                    model.setDictName(dict == null?"":dict.getName());
                    model.setDictCode(dict == null?"":dict.getCode());
                }
                metaDataModels.add(model);
            }
            return getResult(metaDataModels, getTotalCount(responseEntity), page, size);
        }catch (Exception ex){
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "/meta_data", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据元")
    public Envelop deleteMetaData(
            @ApiParam(name = "ids", value = "数据元ID")
            @RequestParam(value = "ids") String ids,
            @ApiParam(name = "version_code", value = "标准版本号")
            @RequestParam(value = "version_code") String versionCode) {

        Envelop envelop = new Envelop();

        ids = trimEnd(ids, ",");
        if (StringUtils.isEmpty(ids)) {
            return failed("请选择需要删除的数据!");
        }
        if (StringUtils.isEmpty(versionCode)) {
            return failed("版本号不能为空!");
        }

        for (int i = 0;i<ids.split(",").length;i++){
            MStdMetaData mStdMetaData = dataSetClient.getMetaData(Long.valueOf(ids.split(",")[i]), versionCode);
            envelop = isDeleteDataSet(String.valueOf(mStdMetaData.getDataSetId()),versionCode,"该数据元正被当前版本的CDA文档使用，不可删除");

            if (!envelop.isSuccessFlg()){
                return envelop;
            }
        }

        boolean bo = dataSetClient.deleteMetaDatas(ids, versionCode);
        if (!bo) {
            return failed("删除失败!");
        }

        return success(null);
    }


    @RequestMapping(value = "/meta_data", method = RequestMethod.GET)
    @ApiOperation(value = "获取数据元")
    public Envelop getMetaData(
            @ApiParam(name = "metaDataId", value = "数据元ID")
            @RequestParam(value = "metaDataId") long metaDataId,
            @ApiParam(name = "versionCode", value = "标准版本号")
            @RequestParam(value = "versionCode") String versionCode) {
        try {
            MStdMetaData mStdMetaData = dataSetClient.getMetaData(metaDataId, versionCode);
            MetaDataModel metaDataModel = convertToModel(mStdMetaData, MetaDataModel.class);

            if (metaDataModel == null) {
                return failed("数据获取失败!");
            }
            return success(metaDataModel);
        } catch (Exception ex) {
            return failedSystem();
        }
    }

    @RequestMapping(value = "/meta_data", method = RequestMethod.POST)
    @ApiOperation(value = "更新数据元")
    public Envelop saveMetaData(
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "json_data", value = "数据元Json")
            @RequestParam(value = "json_data") String jsonData) {

        try {
            MetaDataModel metaDataModel = objectMapper.readValue(jsonData, MetaDataModel.class);
            String errorMsg = "";
            if (StringUtils.isEmpty(version)) {
                errorMsg += "版本号不能为空！";
            }
            if (StringUtils.isEmpty(metaDataModel.getCode())) {
                errorMsg += "代码不能为空!";
            }
            if (StringUtils.isEmpty(metaDataModel.getName())) {
                errorMsg += "名称不能为空!";
            }
            if (StringUtils.isNotEmpty(errorMsg)) {
                return failed(errorMsg);
            }
            boolean isExist = dataSetClient.validateInnerCode(version, metaDataModel.getDataSetId(), metaDataModel.getInnerCode());
            MStdMetaData mStdMetaData = convertToModel(metaDataModel, MStdMetaData.class);
            if (metaDataModel.getId() > 0) {
                MStdMetaData mStdMetaDataGet = dataSetClient.getMetaData( metaDataModel.getId(),version);
                if(!mStdMetaDataGet.getInnerCode().equals(metaDataModel.getInnerCode())
                        && isExist)
                {
                    return failed("代码已存在!");
                }
                mStdMetaData = dataSetClient.updataMetaSet(version, metaDataModel.getId(), objectMapper.writeValueAsString(mStdMetaData));
            } else {
                if( isExist)
                {
                    return failed("代码已存在!");
                }
                mStdMetaData = dataSetClient.saveMetaSet(version, objectMapper.writeValueAsString(mStdMetaData));
            }
            metaDataModel = convertToModel(mStdMetaData, MetaDataModel.class);
            if (metaDataModel == null) {
                return failed("保存失败!");
            }
            return success(metaDataModel);
        }
        catch (Exception ex)
        {
            return failedSystem();
        }
    }

    @RequestMapping(value = "/meta_data/is_exist/inner_code", method = RequestMethod.GET)
    @ApiOperation(value = "验证数据元代码是否重复")
    public boolean isMetaDataCodeExists(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "dataSetId", value = "数据集编号", defaultValue = "")
            @RequestParam(value = "dataSetId") long dataSetId,
            @ApiParam(name = "inner_code", value = "查询代码", defaultValue = "")
            @RequestParam(value = "inner_code") String innerCode) {

        return dataSetClient.validateInnerCode(version, dataSetId, innerCode);
    }

//    @RequestMapping(value = "/is_exist/name", method = RequestMethod.GET)
//    @ApiOperation(value = "验证数据元名称是否重复")
//    public boolean isMetaDataNameExists(
//            @ApiParam(name = "version", value = "版本号", defaultValue = "")
//            @RequestParam(value = "version") String version,
//            @ApiParam(name = "dataSetId", value = "数据集编号", defaultValue = "")
//            @RequestParam(value = "dataSetId") long dataSetId,
//            @ApiParam(name = "code", value = "查询代码", defaultValue = "")
//            @RequestParam(value = "code") String name) {
//
//        return dataSetClient.validatorName(version, dataSetId, name);
//    }

    @RequestMapping(value = "/data_set/is_exist/code",method = RequestMethod.GET)
    public boolean isExistDataSetCode(
            @RequestParam(value = "code")String code,
            @RequestParam(value = "version_code")String versionCode){
        return dataSetClient.isExistCode(code,versionCode);
    }


    @RequestMapping(value = "/getData_sets", method = RequestMethod.GET)
    @ApiOperation(value = "根据数据集ids(用逗号隔开)获取数据集信息")
    public Envelop getDataSets(
            @ApiParam(name = "ids", value = "数据集编号", defaultValue = "")
            @RequestParam(value = "ids") String ids,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version) {
        Envelop envelop = new Envelop();
        List<MStdDataSet> list = dataSetClient.getDataSets(ids, version);
        Collection<DataSetModel> lists = convertToModels(list, new ArrayList<DataSetModel>(list.size()), DataSetModel.class, null);
        envelop.setDetailModelList((List) lists);
        return envelop;
    }

    @RequestMapping(value = "/getMetaDataByDataSetId", method = RequestMethod.GET)
    @ApiOperation(value = "根据数据集id获取数据元")
    public Envelop getMetaDataByDataSetId(
            @ApiParam(name = "data_set_id", value = "数据元编号", defaultValue = "")
            @RequestParam(value = "data_set_ids") String dataSetIds,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version) throws Exception{

        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList((List) dataSetClient.searchMetaDatas("dataSetId,columnName", "dataSetId=" + dataSetIds, "+dataSetId", -1, 0, version).getBody());
        return envelop;
    }


}
