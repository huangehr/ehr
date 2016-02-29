package com.yihu.ehr.ha.std.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.standard.standardsource.StdSourceDetailModel;
import com.yihu.ehr.agModel.standard.standardsource.StdSourceModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.ha.SystemDict.service.ConventionalDictEntryClient;
import com.yihu.ehr.ha.std.service.StandardSourceClient;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.standard.MStdSource;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by AndyCai on 2016/1/25.
 */
@RequestMapping(ApiVersion.Version1_0 + "/stdSource")
@RestController
public class StandardSourceController extends BaseController {

    @Autowired
    private StandardSourceClient stdSourcrClient;

    @Autowired
    private ConventionalDictEntryClient conDictEntryClient;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/sources", method = RequestMethod.GET)
    @ApiOperation(value = "标准来源分页搜索")
    public Envelop searchAdapterOrg(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception {
        List<MStdSource> stdSources = stdSourcrClient.searchAdapterOrg(fields, filters, sorts, size, page);
        List<StdSourceModel> sourcrModelList = new ArrayList<>();
        for (MStdSource stdSource : stdSources) {
            StdSourceModel sourceModel = convertToModel(stdSource, StdSourceModel.class);
            //标准来源类型(
            MConventionalDict sourcerTypeDict = conDictEntryClient.getStdSourceType(stdSource.getSourceType());
            sourceModel.setSourceName(sourcerTypeDict == null ? "" : sourcerTypeDict.getValue());
            //微服务返回的是String类型的日期
            //sourceModel.setCreate_date(DateUtil.formatDate(stdSource.getCreate_date(),DateUtil.DEFAULT_YMDHMSDATE_FORMAT));
        }
        //TODO 取得符合条件总记录数的方法
        int totalCount = 10;
        return getResult(sourcrModelList, totalCount, page, size);
    }


    @RequestMapping(value = "/source/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取标准来源信息")
    public Envelop getStdSource(
            @ApiParam(name = "id", value = "标准来源编号", defaultValue = "")
            @PathVariable(value = "id") String id) {
        Envelop envelop = new Envelop();
        MStdSource mStdSource = stdSourcrClient.getStdSource(id);
        if (mStdSource == null) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("标准来源不存在！");
            return envelop;
        }
        StdSourceDetailModel sourceDetailModel = getStdSourceDetailModel(mStdSource);
        envelop.setSuccessFlg(true);
        envelop.setObj(sourceDetailModel);
        return envelop;
    }

    /**
     * 微服务返回数据转换为前端DetailModel
     *
     * @param mStdSource
     * @return
     */
    private StdSourceDetailModel getStdSourceDetailModel(MStdSource mStdSource) {
        StdSourceDetailModel sourceDetailModel = convertToModel(mStdSource, StdSourceDetailModel.class);
        //标准来源类型字典
        MConventionalDict sourcerTypeDict = conDictEntryClient.getStdSourceType(mStdSource.getSourceType());
        sourceDetailModel.setSourceName(sourcerTypeDict == null ? "" : sourcerTypeDict.getValue());
        return sourceDetailModel;
    }


    @RequestMapping(value = "/source/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "修改标准来源，通过id取数据，取不到数据时新增，否则修改")
    public Envelop updateStdSource(
            @ApiParam(name = "id", value = "标准来源编号", defaultValue = "")
            @PathVariable(value = "id") String id,
            @ApiParam(name = "code", value = "编码", defaultValue = "")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "name", value = "名称", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "type", value = "类型", defaultValue = "")
            @RequestParam(value = "type") String type,
            @ApiParam(name = "description", value = "描述", defaultValue = "")
            @RequestParam(value = "description") String description) throws Exception {
        Envelop envelop = new Envelop();
        boolean flag = stdSourcrClient.updateStdSource(id, code, name, type, description);
        if (!flag) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("标准来源更新失败！");
            return envelop;
        }
        envelop.setSuccessFlg(true);
        return envelop;
    }

    @RequestMapping(value = "/source", method = RequestMethod.POST)
    @ApiOperation(value = "新增标准来源")
    public boolean addStdSource(
            @ApiParam(name = "code", value = "编码", defaultValue = "")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "name", value = "名称", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "type", value = "类型", defaultValue = "")
            @RequestParam(value = "type") String type,
            @ApiParam(name = "description", value = "描述", defaultValue = "")
            @RequestParam(value = "description") String description) throws Exception {


        return true;
    }


    @RequestMapping(value = "/sources", method = RequestMethod.DELETE)
    @ApiOperation(value = "通过id组删除标准来源，多个id以,分隔")
    public boolean delStdSources(
            @ApiParam(name = "ids", value = "标准来源编号", defaultValue = "")
            @RequestParam(value = "ids") String ids) throws Exception {

        return true;

    }

    @RequestMapping(value = "/source/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "通过id删除标准来源")
    public boolean delStdSource(
            @ApiParam(name = "id", value = "标准来源编号", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {

        return true;
    }


//    @RequestMapping(value = "/standardSources",method = RequestMethod.GET)
//    public Object getStdSourceList(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
//                                   @PathVariable(value = "apiVersion") String apiVersion) {
//        return null;
//    }
//
//    @RequestMapping(value = "/standardSource",method = RequestMethod.GET)
//    public Object getStdSourceById(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
//                                   @PathVariable(value = "apiVersion") String apiVersion,
//                                   @ApiParam(name = "id",value = "标准来源ID")
//                                   @RequestParam(value = "id")String id){
//        return null;
//    }
//    @RequestMapping(value = "/getStdSource",method = RequestMethod.GET)
//    public String getStandardSourceByCodeOrName(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
//                                      @PathVariable(value = "apiVersion") String apiVersion,
//                                  @ApiParam(name = "code",value = "来源代码")
//                                  @RequestParam(value = "code")String code,
//                                  @ApiParam(name = "name",value = "来源名称")
//                                      @RequestParam(value = "name")String name,
//                                  @ApiParam(name = "type",value = "来源类型")
//                                      @RequestParam(value = "type")String type,
//                                  @ApiParam(name = "page", value = "当前页", defaultValue = "1")
//                                      @RequestParam(value = "page") int page,
//                                  @ApiParam(name = "rows", value = "每页行数", defaultValue = "20")
//                                      @RequestParam(value = "rows") int rows) {
//        return null;
//    }
//
//    @RequestMapping(value = "/standardSource",method = RequestMethod.POST)
//    public Object saveStandardSource(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
//                                     @PathVariable(value = "apiVersion") String apiVersion,
//                                     @ApiParam(name = "id",value = "id")
//                                     @RequestParam(value = "id")String id,
//                                     @ApiParam(name = "code",value = "代码")
//                                     @RequestParam(value = "code")String code,
//                                     @ApiParam(name = "name",value = "名称")
//                                     @RequestParam(value = "name")String name,
//                                     @ApiParam(name = "type",value = "类别")
//                                     @RequestParam(value = "type")String type,
//                                     @ApiParam(name = "description",value = "说明")
//                                     @RequestParam(value = "description")String description){
//
//        return null;
//    }
//
//
//    @RequestMapping(value = "/standardSource",method = RequestMethod.DELETE)
//    public String delStdSource(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
//                               @PathVariable(value = "apiVersion") String apiVersion,
//                               @ApiParam(name = "id", value = "id")
//                               @RequestParam(value = "id") String id) {
//        return null;
//    }
}
