package com.yihu.ehr.ha.std.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

/**
 * Created by AndyCai on 2016/1/25.
 */
@RequestMapping(ApiVersionPrefix.CommonVersion + "/dataSet")
@RestController
public class DataSetController extends BaseRestController {

    @RequestMapping(value = "/dataSets",method = RequestMethod.GET)
    public String getDataSetByCodeName(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                       @PathVariable(value = "apiVersion") String apiVersion,
                                       @ApiParam(name = "code", value = "数据集代码")
                                       @RequestParam(value = "code") String code,
                                       @ApiParam(name = "name", value = "数据集名称")
                                       @RequestParam(value = "name") String name,
                                       @ApiParam(name = "versionCode", value = "标准版本代码")
                                       @RequestParam(value = "versionCode") String versionCode,
                                       @ApiParam(name = "page", value = "当前页", defaultValue = "1")
                                       @RequestParam(value = "page") int page,
                                       @ApiParam(name = "rows", value = "每页行数", defaultValue = "20")
                                       @RequestParam(value = "rows") int rows ) {
        return null;
    }

    @RequestMapping(value = "/dataSet",method = RequestMethod.DELETE)
    public String deleteDataSet(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                @PathVariable(value = "apiVersion") String apiVersion,
                                @ApiParam(name = "dataSetId", value = "数据集ID")
                                @RequestParam(value = "dataSetId") long dataSetId,
                                @ApiParam(name = "versionCode", value = "标准版本代码")
                                @RequestParam(value = "versionCode") String versionCode) {
        return null;
    }

    @RequestMapping(value = "/dataSet",method = RequestMethod.GET)
    public String getDataSet(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                             @PathVariable(value = "apiVersion") String apiVersion,
                             @ApiParam(name = "dataSetId", value = "数据集ID")
                             @RequestParam(value = "dataSetId") long dataSetId,
                             @ApiParam(name = "versionCode", value = "标准版本代码")
                             @RequestParam(value = "versionCode") String versionCode) {
        return null;
    }

    @RequestMapping(value = "/dataSet",method = RequestMethod.POST)
    public String saveDataSet(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
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
                              @ApiParam(name = "versionCode", value = "标准版本号")
                              @RequestParam(value = "versionCode") String versionCode) {
        return null;
    }

    @RequestMapping(value = "/metaDatas",method = RequestMethod.GET)
    public String getMetaDataByCodeOrName(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                          @PathVariable(value = "apiVersion") String apiVersion,
                                          @ApiParam(name = "id", value = "数据元ID")
                                          @RequestParam(value = "id") long id,
                                          @ApiParam(name = "versionCode", value = "标准版本号")
                                          @RequestParam(value = "versionCode") String versionCode,
                                          @ApiParam(name = "metaDataCode", value = "数据元代码")
                                          @RequestParam(value = "metaDataCode") String metaDataCode,
                                          @ApiParam(name = "metaDataName", value = "数据元名称")
                                          @RequestParam(value = "metaDataName") String metaDataName,
                                          @ApiParam(name = "page", value = "当前页", defaultValue = "1")
                                          @RequestParam(value = "page") int page,
                                          @ApiParam(name = "rows", value = "每页行数", defaultValue = "20")
                                          @RequestParam(value = "rows") int rows) {
        return null;
    }
    @RequestMapping(value = "/metaData",method = RequestMethod.DELETE)
    public String deleteMetaData(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                 @PathVariable(value = "apiVersion") String apiVersion,
                                 @ApiParam(name = "ids", value = "数据元ID")
                                 @RequestParam(value = "ids") long ids,
                                 @ApiParam(name = "versionCode", value = "标准版本号")
                                 @RequestParam(value = "versionCode") String versionCode) {
        return null;
    }

    @RequestMapping(value = "/getMetaData", method = RequestMethod.GET)
    public String getMetaData(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                              @PathVariable(value = "apiVersion") String apiVersion,
                              @ApiParam(name = "dataSetId", value = "数据集ID")
                              @RequestParam(value = "dataSetId") long dataSetId,
                              @ApiParam(name = "metaDataId", value = "数据元ID")
                              @RequestParam(value = "metaDataId") long metaDataId,
                              @ApiParam(name = "versionCode", value = "标准版本号")
                              @RequestParam(value = "versionCode") String versionCode) {
        return null;
    }

    @RequestMapping(value = "/updataMetaSet",method = RequestMethod.POST)
    public String updataMetaData(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                 @PathVariable(value = "apiVersion") String apiVersion,
                                 @ApiParam(name = "metaDataJson", value = "数据元Json")
                                 @RequestParam(value = "metaDataJson") String metaDataJson) {
        return null;
    }

    @RequestMapping(value = "/validatorMetadata",method = RequestMethod.GET)
    public String validatorMetadata(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                    @PathVariable(value = "apiVersion") String apiVersion,
                                    @ApiParam(name = "versionCode", value = "标准版本代码")
                                    @RequestParam(value = "versionCode") String versionCode,
                                    @ApiParam(name = "datasetId",value = "数据集ID")
                                    @RequestParam(value = "datasetId")String datasetId,
                                    @ApiParam(name = "searchNm",value = "查询条件")
                                    @RequestParam(value = "searchNm")String searchNm,
                                    @ApiParam(name = "metaDataCodeMsg",value = "查询类别")
                                    @RequestParam(value = "metaDataCodeMsg") String metaDataCodeMsg) {
        return null;
    }
}
