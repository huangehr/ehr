package com.yihu.ehr.ha.std.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.standard.datasset.DataSetModel;
import com.yihu.ehr.agModel.standard.datasset.MetaDataModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.ha.std.service.DataSetClient;
import com.yihu.ehr.model.standard.MStdDataSet;
import com.yihu.ehr.model.standard.MStdMetaData;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

/**
 * Created by AndyCai on 2016/1/25.
 */
@RequestMapping(ApiVersion.Version1_0 + "/dataSet")
@RestController
public class DataSetController extends BaseController{

    @Autowired
    private DataSetClient dataSetClient;

    @RequestMapping(value = "/dataSets", method = RequestMethod.GET)
    @ApiOperation(value = "查询数据集的方法")
    public Envelop getDataSetByCodeName(
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

        Collection<MStdDataSet> mStdDataSets = dataSetClient.searchDataSets(fields, filters, sorts, size, page, version);
        List<DataSetModel> dataSetModelList = (List<DataSetModel>)convertToModels(mStdDataSets,new ArrayList<DataSetModel>(mStdDataSets.size()),DataSetModel.class,null);

//TODO:获取总条数
//        String count = response.getHeader(AgAdminConstants.ResourceCount);
//        int totalCount = StringUtils.isNotEmpty(count) ? Integer.parseInt(count) : 0;

        Envelop envelop = getResult(dataSetModelList,0,page,size);

        return envelop;
    }

    @RequestMapping(value = "/dataSet", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据集信息")
    public Envelop deleteDataSet(
            @ApiParam(name = "id", value = "数据集编号", defaultValue = "")
            @PathVariable(value = "id") String ids,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version) {

        Envelop envelop = new Envelop();
        boolean bo;

        if(ids.split(",").length>1){
            //批量删除
            bo = dataSetClient.deleteDataSet(ids,version);

        }else {
            //单个删除
            long id = Long.valueOf(ids);
            bo = dataSetClient.deleteDataSet(id,version);

        }
        envelop.setSuccessFlg(bo);
        return envelop;
    }

    @RequestMapping(value = "/dataSet", method = RequestMethod.GET)
    @ApiOperation(value = "获取数据集信息")
    public Envelop getDataSet(
            @ApiParam(name = "id", value = "数据集编号", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version) {

        Envelop envelop = new Envelop();

        MStdDataSet mStdDataSet = dataSetClient.getDataSet(id,version);

        if(mStdDataSet != null){
            DataSetModel dataSetModel = convertToModel(mStdDataSet,DataSetModel.class);
            envelop.setSuccessFlg(true);
            envelop.setObj(dataSetModel);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("获取数据集信息失败");
        }

        return envelop;
    }

    @RequestMapping(value = "/dataSet", method = RequestMethod.POST)
    @ApiOperation(value = "新增,修改数据集信息")
    public Envelop saveDataSet(
            @ApiParam(name = "id", value = "数据集ID")
            @RequestParam(value = "id") String id,
            @ApiParam(name = "code", value = "数据集代码")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "name", value = "数据集名称")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "refStandard", value = "标准来源ID")
            @RequestParam(value = "refStandard") String refStandard,
            @ApiParam(name = "summary", value = "描述")
            @RequestParam(value = "summary") String summary,
            @ApiParam(name = "version", value = "标准版本号")
            @RequestParam(value = "version") String version) throws JsonProcessingException {

        //根据id是否为空来判断具体调用微服务的新增还是修改
        Envelop envelop = new Envelop();

        boolean bo;
        envelop.setSuccessFlg(false);

        DataSetModel dataSetModel = new DataSetModel();
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> dictMap = new HashMap<>();

        dictMap.put("id",id);
        dictMap.put("code",code);
        dictMap.put("name",name);
        dictMap.put("reference",refStandard);
        dictMap.put("summary",summary);
        dictMap.put("stdVersion",version);

        String datasetJsonModel = mapper.writeValueAsString(dictMap);

        if(id == "0"||id == null||id == ""){

            MStdDataSet mStdDataSet = dataSetClient.saveDataSet(version,datasetJsonModel);
            dataSetModel = convertToModel(mStdDataSet,DataSetModel.class);
            if(dataSetModel != null){
                envelop.setSuccessFlg(true);
                envelop.setObj(dataSetModel);
            }else {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("新增数据集信息失败");
            }

        }else {

            MStdDataSet mStdDataSet = dataSetClient.updateDataSet(version, Long.parseLong(id), datasetJsonModel);
            dataSetModel = convertToModel(mStdDataSet,DataSetModel.class);
            if(dataSetModel != null){
                envelop.setSuccessFlg(true);
                envelop.setObj(dataSetModel);
            }else {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("修改数据集信息失败");
            }

        }

        return envelop;
    }











    @RequestMapping(value = "/metaDatas", method = RequestMethod.GET)
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

        Collection<MStdMetaData> mStdMetaDatas = dataSetClient.searchMetaDatas(fields,filters,sorts,size,page,version);
        List<MetaDataModel> metaDataModels = (List<MetaDataModel>)convertToModels(mStdMetaDatas,new ArrayList<MetaDataModel>(mStdMetaDatas.size()),MetaDataModel.class,null);

        //TODO:获取总条数
//        String count = response.getHeader(AgAdminConstants.ResourceCount);
//        int totalCount = StringUtils.isNotEmpty(count) ? Integer.parseInt(count) : 0;

        Envelop envelop = getResult(metaDataModels,0,page,size);
        return envelop;
    }

    @RequestMapping(value = "/metaData", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据元")
    public Envelop deleteMetaData(
            @ApiParam(name = "ids", value = "数据元ID")
            @RequestParam(value = "ids") String ids,
            @ApiParam(name = "versionCode", value = "标准版本号")
            @RequestParam(value = "versionCode") String versionCode) {

        Envelop envelop = new Envelop();

        String[] strings = ids.split(",");
        boolean bo;
        if (strings.length>1){
            //批量删除数据元
            bo = dataSetClient.deleteMetaDatas(ids,versionCode);
            envelop.setSuccessFlg(bo);
        }else {
            //单个删除数据元
            long id = Long.valueOf(ids);
            bo = dataSetClient.deleteMetaData(id,versionCode);
            envelop.setSuccessFlg(bo);
        }

        return envelop;
    }


    @RequestMapping(value = "/getMetaData", method = RequestMethod.GET)
    @ApiOperation(value = "获取数据元")
    public Envelop getMetaData(
            @ApiParam(name = "metaDataId", value = "数据元ID")
            @RequestParam(value = "metaDataId") long metaDataId,
            @ApiParam(name = "versionCode", value = "标准版本号")
            @RequestParam(value = "versionCode") String versionCode) {

        Envelop envelop = new Envelop();

        MStdMetaData mStdMetaData = dataSetClient.getMetaData(metaDataId, versionCode);
        MetaDataModel metaDataModel = convertToModel(mStdMetaData,MetaDataModel.class);

        if (metaDataModel != null){
            envelop.setObj(metaDataModel);
            envelop.setSuccessFlg(true);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("获取数据元失败");
        }
        return envelop;
    }

    @RequestMapping(value = "/MetaData", method = RequestMethod.POST)
    @ApiOperation(value = "更新数据元")
    public Envelop saveMetaData(
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "metaDataJson", value = "数据元Json")
            @RequestParam(value = "metaDataJson") String metaDataJson) throws IOException {

        Envelop envelop = new Envelop();
        ObjectMapper mapper = new ObjectMapper();
        MStdMetaData mStdMetaData;

        MetaDataModel metaDataModel = mapper.readValue(metaDataJson, MetaDataModel.class);

        if(metaDataModel.getId()>0){

            mStdMetaData = dataSetClient.updataMetaSet(version, metaDataModel.getId(), metaDataJson);
            metaDataModel = convertToModel(mStdMetaData,MetaDataModel.class);
            if(metaDataModel != null){
                envelop.setSuccessFlg(true);
                envelop.setObj(metaDataModel);
            }else {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("修改数据元失败");
            }

        }else {

            mStdMetaData = dataSetClient.saveMetaSet(version,metaDataJson);
            metaDataModel = convertToModel(mStdMetaData,MetaDataModel.class);
            if(metaDataModel != null){
                envelop.setSuccessFlg(true);
                envelop.setObj(metaDataModel);
            }else {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("新增数据元失败");
            }
        }

        return envelop;
    }

    @RequestMapping(value = "/validatorMetadata/code", method = RequestMethod.GET)
    @ApiOperation(value = "验证数据元代码是否重复")
    public Envelop validatorMetadataCode(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "dataSetId", value = "数据集编号", defaultValue = "")
            @RequestParam(value = "dataSetId") long dataSetId,
            @ApiParam(name = "code", value = "查询代码", defaultValue = "")
            @RequestParam(value = "code") String code) {

        Envelop envelop = new Envelop();

        boolean bo = dataSetClient.validateCode(version,dataSetId,code);
        envelop.setSuccessFlg(bo);

        return envelop;
    }

    @RequestMapping(value = "/validatorMetadata/name", method = RequestMethod.GET)
    @ApiOperation(value = "验证数据元名称是否重复")
    public Envelop validatorMetadataName(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "dataSetId", value = "数据集编号", defaultValue = "")
            @RequestParam(value = "dataSetId") long dataSetId,
            @ApiParam(name = "code", value = "查询代码", defaultValue = "")
            @RequestParam(value = "code") String name) {

        Envelop envelop = new Envelop();

        boolean bo = dataSetClient.validatorName(version, dataSetId, name);
        envelop.setSuccessFlg(bo);

        return envelop;
    }
}
