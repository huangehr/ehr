package com.yihu.ehr.ha.std.controller;

import com.yihu.ehr.agModel.standard.datasset.DataSetModel;
import com.yihu.ehr.agModel.standard.datasset.MetaDataModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.ha.std.service.DataSetClient;
import com.yihu.ehr.model.adaption.MDataSet;
import com.yihu.ehr.model.standard.MStdDataSet;
import com.yihu.ehr.model.standard.MStdMetaData;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        List<DataSetModel> dataSetModelList = new ArrayList<>();

        for (MStdDataSet mStdDataSet:mStdDataSets){
            DataSetModel dataSetModel = convertToModel(mStdDataSet,DataSetModel.class);
            dataSetModel.setId(mStdDataSet.getId());
            dataSetModel.setDatasetCode(mStdDataSet.getCode());
            dataSetModel.setDatasetName(mStdDataSet.getName());
            dataSetModel.setStdVersion(mStdDataSet.getStdVersion());
            dataSetModelList.add(dataSetModel);
        }

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
            @PathVariable(value = "id") long id,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version) {

        Envelop envelop = new Envelop();

        boolean bo = dataSetClient.deleteDataSet(id,version);

        envelop.setSuccessFlg(bo);

        return envelop;
    }

    @RequestMapping(value = "/datasets", method = RequestMethod.DELETE)
    @ApiOperation(value = "批量删除数据集信息")
    public Envelop deleteDataSet(
            @ApiParam(name = "ids", value = "数据集编号", defaultValue = "")
            @RequestParam(value = "ids") String ids,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version) {

        Envelop envelop = new Envelop();

        boolean bo = dataSetClient.deleteDataSet(ids,version);

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

        MStdDataSet mStdDataSet = dataSetClient.getDataSet(id,version);

        DataSetModel dataSetModel = new DataSetModel();
        Envelop envelop = new Envelop();

        if(mStdDataSet != null){
            dataSetModel.setId(mStdDataSet.getId());
            dataSetModel.setDatasetCode(mStdDataSet.getCode());
            dataSetModel.setDatasetName(mStdDataSet.getName());
            dataSetModel.setStdVersion(mStdDataSet.getStdVersion());
            dataSetModel.setReference(mStdDataSet.getReference());
            dataSetModel.setSummary(mStdDataSet.getSummary());
            envelop.setSuccessFlg(true);
            envelop.setObj(dataSetModel);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("获取数据集信息失败");
        }

        return envelop;
    }



    //todo:type属性是啥？？微服务没有type属性
    @RequestMapping(value = "/dataSet", method = RequestMethod.POST)
    @ApiOperation(value = "新增,修改数据集信息")
    public Envelop saveDataSet(
            @ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "apiVersion") String apiVersion,
            @ApiParam(name = "id", value = "数据集ID")
            @RequestParam(value = "id") long id,
            @ApiParam(name = "code", value = "数据集代码")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "name", value = "数据集名称")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "type", value = "数据集类型")
            @RequestParam(value = "type") String type,
            @ApiParam(name = "refStandard", value = "标准来源ID")
            @RequestParam(value = "refStandard") String refStandard,
            @ApiParam(name = "summary", value = "描述")
            @RequestParam(value = "summary") String summary,
            @ApiParam(name = "version", value = "标准版本号")
            @RequestParam(value = "version") String version) {

        //todo:根据id是否为空来判断具体调用微服务的新增还是修改
        Envelop envelop = new Envelop();

        boolean bo;
        envelop.setSuccessFlg(false);

        if(id == 0){
            bo = dataSetClient.saveDataSet(code,name,refStandard,summary,version);
            if(bo){
                envelop.setSuccessFlg(true);
            }else {
                envelop.setErrorMsg("新增数据集信息失败");
            }
        }else {
            bo = dataSetClient.updateDataSet(id,code,name,refStandard,summary,version);
            if(bo){
                envelop.setSuccessFlg(true);
            }else {
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

        Envelop envelop = new Envelop();
        List<MetaDataModel> metaDataModels = new ArrayList<>();

        Collection<MStdMetaData> mStdMetaDatas = dataSetClient.searchMetaDatas(fields,filters,sorts,size,page,version);

        for (MStdMetaData mStdMetaData:mStdMetaDatas){
            MetaDataModel metaDataModel = convertToModel(mStdMetaDatas,MetaDataModel.class);
            metaDataModels.add(metaDataModel);
        }
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(metaDataModels);

        return envelop;
    }

//todo:2.29
    @RequestMapping(value = "/metaData", method = RequestMethod.DELETE)
    public String deleteMetaData(
            @ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")

            @PathVariable(value = "apiVersion") String apiVersion,
            @ApiParam(name = "ids", value = "数据元ID")
            @RequestParam(value = "ids") long ids,
            @ApiParam(name = "versionCode", value = "标准版本号")
            @RequestParam(value = "versionCode") String versionCode) {

        return null;
    }

    @RequestMapping(value = "/getMetaData", method = RequestMethod.GET)
    public String getMetaData(
            @ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "apiVersion") String apiVersion,
            @ApiParam(name = "dataSetId", value = "数据集ID")
            @RequestParam(value = "dataSetId") long dataSetId,
            @ApiParam(name = "metaDataId", value = "数据元ID")
            @RequestParam(value = "metaDataId") long metaDataId,
            @ApiParam(name = "versionCode", value = "标准版本号")
            @RequestParam(value = "versionCode") String versionCode) {
        return null;
    }

    @RequestMapping(value = "/updataMetaSet", method = RequestMethod.POST)
    public String updataMetaData(
            @ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "apiVersion") String apiVersion,
            @ApiParam(name = "metaDataJson", value = "数据元Json")
            @RequestParam(value = "metaDataJson") String metaDataJson) {
        return null;
    }

    @RequestMapping(value = "/validatorMetadata", method = RequestMethod.GET)
    public String validatorMetadata(
            @ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "apiVersion") String apiVersion,
            @ApiParam(name = "versionCode", value = "标准版本代码")
            @RequestParam(value = "versionCode") String versionCode,
            @ApiParam(name = "datasetId", value = "数据集ID")
            @RequestParam(value = "datasetId") String datasetId,
            @ApiParam(name = "searchNm", value = "查询条件")
            @RequestParam(value = "searchNm") String searchNm,
            @ApiParam(name = "metaDataCodeMsg", value = "查询类别")
            @RequestParam(value = "metaDataCodeMsg") String metaDataCodeMsg) {
        return null;
    }
}
