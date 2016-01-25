package com.yihu.ehr.ha.std.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

/**
 * Created by AndyCai on 2016/1/21.
 */
@RequestMapping(ApiVersionPrefix.CommonVersion + "/cda")
@RestController
public class CDAController extends BaseRestController {

    @RequestMapping(value = "/cdas", method = RequestMethod.GET)
    public Object GetCdaListByKey(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                  @PathVariable(value = "apiVersion") String apiVersion,
                                  @ApiParam(name = "code", value = "CDA代码")
                                  @RequestParam(value = "code") String code,
                                  @ApiParam(name = "name", value = "CDA名称")
                                  @RequestParam(value = "name") String name,
                                  @ApiParam(name = "versionCode", value = "标准版本代码")
                                  @RequestParam(value = "versionCode") String versionCode,
                                  @ApiParam(name = "cdaType", value = "cda类别")
                                  @RequestParam(value = "cdaType") String cdaType,
                                  @ApiParam(name = "page", value = "当前页", defaultValue = "1")
                                  @RequestParam(value = "page") int page,
                                  @ApiParam(name = "rows", value = "每页行数", defaultValue = "20")
                                  @RequestParam(value = "rows") int rows) {
        return null;
    }

    @RequestMapping(value = "/cda", method = RequestMethod.GET)
    public Object getCDAInfoById(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                 @PathVariable(value = "apiVersion") String apiVersion,
                                 @ApiParam(name = "cdaId", value = "cdaID")
                                 @PathVariable(value = "cdaId") String cdaId,
                                 @ApiParam(name = "versionCode", value = "标准版本代码")
                                 @PathVariable(value = "versionCode") String versionCode) {
        return null;
    }

    @RequestMapping(value = "/relationships",method = RequestMethod.GET)
    public Object getRelationByCdaId(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                     @PathVariable(value = "apiVersion") String apiVersion,
                                     @ApiParam(name = "cdaId", value = "cdaID")
                                     @PathVariable(value = "cdaId") String cdaId,
                                     @ApiParam(name = "versionCode", value = "标准版本代码")
                                     @PathVariable(value = "versionCode") String versionCode) {
        return null;
    }

    @RequestMapping(value = "cda",method = RequestMethod.POST)
    public Object SaveCdaInfo(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                              @PathVariable(value = "apiVersion") String apiVersion,
                              @ApiParam(name = "cdaInfoJson", value = "CDAJson")
                              @RequestParam(value = "cdaInfoJson") String cdaInfoJson) {
        return null;
    }

    @RequestMapping(value = "/cda",method = RequestMethod.DELETE)
    public Object deleteCdaInfo(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                @PathVariable(value = "apiVersion") String apiVersion,
                                @ApiParam(name = "cdaId", value = "cdaID")
                                @PathVariable(value = "cdaId") String cdaId,
                                @ApiParam(name = "versionCode", value = "标准版本代码")
                                @PathVariable(value = "versionCode") String versionCode) {
        return null;
    }
    @RequestMapping("SaveRelationship")
    public Object SaveRelationship(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                   @PathVariable(value = "apiVersion") String apiVersion,
                                   @ApiParam(name = "dataSetIds", value = "数据集ID(多ID以逗号隔开)")
                                   @RequestParam(value = "dataSetIds") String dataSetIds,
                                   @ApiParam(name = "cdaId", value = "cdaID")
                                   @PathVariable(value = "cdaId") String cdaId,
                                   @ApiParam(name = "versionCode", value = "标准版本代码")
                                   @PathVariable(value = "versionCode") String versionCode,
                                   @ApiParam(name = "xmlInfo", value = "XML文件信息")
                                   @RequestParam(value = "xmlInfo") String xmlInfo) {
        return null;
    }

    @RequestMapping(value = "/getDatasetByCdaId",method = RequestMethod.GET)
    public Object getDatasetByCdaId(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                    @PathVariable(value = "apiVersion") String apiVersion,
                                    @ApiParam(name = "cdaId", value = "cdaID")
                                    @PathVariable(value = "cdaId") String cdaId,
                                    @ApiParam(name = "versionCode", value = "标准版本代码")
                                    @PathVariable(value = "versionCode") String versionCode) {
        return null;
    }

    @RequestMapping(value = "documentExist",method = RequestMethod.GET)
    public Object documentExist(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                @PathVariable(value = "apiVersion") String apiVersion,
                                @ApiParam(name = "code", value = "CDA代码")
                                @RequestParam(value = "code") String code,
                                @ApiParam(name = "versionCode", value = "标准版本代码")
                                @PathVariable(value = "versionCode") String versionCode){

        return null;
    }

    @RequestMapping(value = "/getCdaXmlFileInfo",method = RequestMethod.GET)
    public Object getCdaXmlFileInfo(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                    @PathVariable(value = "apiVersion") String apiVersion,
                                    @ApiParam(name = "cdaId", value = "cdaID")
                                    @PathVariable(value = "cdaId") String cdaId,
                                    @ApiParam(name = "versionCode", value = "标准版本代码")
                                    @PathVariable(value = "versionCode") String versionCode) {
        return null;
    }
}
