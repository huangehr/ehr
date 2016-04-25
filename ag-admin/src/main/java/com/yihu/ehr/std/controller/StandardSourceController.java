package com.yihu.ehr.std.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.SystemDict.service.ConventionalDictEntryClient;
import com.yihu.ehr.agModel.standard.standardsource.StdSourceDetailModel;
import com.yihu.ehr.agModel.standard.standardsource.StdSourceModel;
import com.yihu.ehr.agModel.standard.standardversion.StdVersionModel;
import com.yihu.ehr.constants.AgAdminConstants;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.standard.*;
import com.yihu.ehr.std.service.*;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.HttpClientUtil;
import com.yihu.ehr.util.controller.BaseController;
import com.yihu.ehr.util.operator.DateUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by yww on 2016/3/1.
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin/standard_source")
@RestController
public class StandardSourceController extends BaseController {

    @Autowired
    private StandardSourceClient stdSourcrClient;

    @Autowired
    private CDAVersionClient cdaVersionClient;

    @Autowired
    private DictClient dictClient;

    @Autowired
    private DataSetClient dataSetClient;

    @Autowired
    private CDAClient cdaClient;

    @Autowired
    private ConventionalDictEntryClient conDictEntryClient;

    @Autowired
    private ObjectMapper objectMapper;


    List<StdSourceModel> getStdSourceModel(Collection<MStdSource> stdSources) {
        List<StdSourceModel> sourcrModelList = new ArrayList<>();
        for (MStdSource stdSource : stdSources) {
            StdSourceModel sourceModel = convertToModel(stdSource, StdSourceModel.class);
            //标准来源类型字典
            MConventionalDict sourcerTypeDict = conDictEntryClient.getStdSourceType(stdSource.getSourceType());
            sourceModel.setSourceValue(sourcerTypeDict == null ? "" : sourcerTypeDict.getValue());
            sourceModel.setCreateDate(DateUtil.formatDate(stdSource.getCreateDate(), DateUtil.DEFAULT_YMDHMSDATE_FORMAT));
            sourcrModelList.add(sourceModel);
        }
        return sourcrModelList;
    }

    @RequestMapping(value = "/sources", method = RequestMethod.GET)
    @ApiOperation(value = "标准来源分页搜索")
    public Envelop searchSources(
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
        ResponseEntity<Collection<MStdSource>> responseEntity = stdSourcrClient.searchSources(fields, filters, sorts, size, page);
        Collection<MStdSource> stdSources = responseEntity.getBody();
        List<StdSourceModel> sourcrModelList = getStdSourceModel(stdSources);
        int totalCount = getTotalCount(responseEntity);
        return getResult(sourcrModelList, totalCount, page, size);
    }


    @RequestMapping(value = "/sources/no_paging", method = RequestMethod.GET)
    @ApiOperation(value = "标准来源分页搜索(不分页)")
    public Envelop searchSourcesWithoutPaging(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters) throws Exception {
        Envelop envelop = new Envelop();
        ResponseEntity<Collection<MStdSource>> responseEntity = stdSourcrClient.search(filters);
        Collection<MStdSource> stdSources = responseEntity.getBody();
        List<StdSourceModel> sourcrModelList = getStdSourceModel(stdSources);
        envelop.setDetailModelList(sourcrModelList);
        return envelop;
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
        StdSourceDetailModel sourceDetailModel = convertToStdSourceDetailModel(mStdSource);
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
    private StdSourceDetailModel convertToStdSourceDetailModel(MStdSource mStdSource) {

        if (mStdSource == null) {
            return null;
        }

        StdSourceDetailModel sourceDetailModel = convertToModel(mStdSource, StdSourceDetailModel.class);
        sourceDetailModel.setCreateDate(DateToString(mStdSource.getCreateDate(), AgAdminConstants.DateTimeFormat));
        sourceDetailModel.setUpdateDate(DateToString(mStdSource.getUpdateDate(), AgAdminConstants.DateTimeFormat));
        //标准来源类型字典
        MConventionalDict sourcerTypeDict = conDictEntryClient.getStdSourceType(mStdSource.getSourceType());
        sourceDetailModel.setSourceValue(sourcerTypeDict == null ? "" : sourcerTypeDict.getValue());
        return sourceDetailModel;
    }


    @RequestMapping(value = "/source", method = RequestMethod.PUT)
    @ApiOperation(value = "修改标准来源，通过id取数据，取不到数据时新增，否则修改")
    public Envelop updateStdSource(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam(value = "model") String model) throws Exception {

        StdSourceDetailModel sourceDetailModel = objectMapper.readValue(model, StdSourceDetailModel.class);

        String errorMsg = "";
        if (StringUtils.isEmpty(sourceDetailModel.getSourceType())) {
            errorMsg += "类别不能为空!";
        }
        if (StringUtils.isEmpty(sourceDetailModel.getCode())) {
            errorMsg += "代码不能为空！";
        }
        if (StringUtils.isEmpty(sourceDetailModel.getName())) {
            errorMsg += "名称不能为空！";
        }
        if (StringUtils.isNotEmpty(errorMsg)) {
            return failed(errorMsg);
        }
        MStdSource mStdSourceOld = stdSourcrClient.getStdSource(sourceDetailModel.getId());
        if (mStdSourceOld == null) {
            return failed("标准来源不存在,请确认!");
        }
        if (!mStdSourceOld.getCode().equals(sourceDetailModel.getCode())
                && stdSourcrClient.isCodeExist(sourceDetailModel.getCode())) {
            return failed("代码已存在!");
        }

        mStdSourceOld = convertToMStdSource(sourceDetailModel);
        mStdSourceOld.setUpdateDate(new Date());
        String jsonData = objectMapper.writeValueAsString(mStdSourceOld);
        MStdSource mStdSource = stdSourcrClient.updateStdSource(sourceDetailModel.getId(), jsonData);
        if (mStdSource == null) {
            return failed("保存失败!");
        }
        return success(convertToStdSourceDetailModel(mStdSource));
    }

    @RequestMapping(value = "/source", method = RequestMethod.POST)
    @ApiOperation(value = "新增标准来源")
    public Envelop addStdSource(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam(value = "model") String model) throws Exception {

        StdSourceDetailModel sourceDetailModel = objectMapper.readValue(model, StdSourceDetailModel.class);

        String errorMsg = "";
        if (StringUtils.isEmpty(sourceDetailModel.getSourceType())) {
            errorMsg += "类别不能为空!";
        }
        if (StringUtils.isEmpty(sourceDetailModel.getCode())) {
            errorMsg += "代码不能为空！";
        }
        if (StringUtils.isEmpty(sourceDetailModel.getName())) {
            errorMsg += "名称不能为空！";
        }
        if (StringUtils.isNotEmpty(errorMsg)) {
            return failed(errorMsg);
        }
        if (stdSourcrClient.isCodeExist(sourceDetailModel.getCode())) {
            return failed("代码已存在!");
        }

        MStdSource mStdSource = convertToMStdSource(sourceDetailModel);
        mStdSource.setCreateDate(new Date());
        String jsonData = objectMapper.writeValueAsString(mStdSource);
        mStdSource = stdSourcrClient.addStdSource(jsonData);
        if (mStdSource == null) {
            return failed("保存失败!");
        }
        return success(convertToStdSourceDetailModel(mStdSource));
    }


    @RequestMapping(value = "/sources", method = RequestMethod.DELETE)
    @ApiOperation(value = "通过id组删除标准来源，多个id以,分隔")
    public Envelop delStdSources(
            @ApiParam(name = "ids", value = "标准来源编号", defaultValue = "")
            @RequestParam(value = "ids") String ids) throws Exception {

        Envelop envelop = new Envelop();
        envelop = isDeleteStdSource(ids);
        if (!envelop.isSuccessFlg())
            return envelop;
        boolean bo = stdSourcrClient.delStdSources(ids);
        if (!bo) {
            envelop.setErrorMsg("标准来源删除失败");
        }
        envelop.setSuccessFlg(bo);
        return envelop;
    }

    public Envelop isDeleteStdSource(String id) {

        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);

        try {
            //查询所有版本
            ResponseEntity<Collection<MCDAVersion>> responseEntity = cdaVersionClient.searchCDAVersions("", "", "", 1000, 1);
            Collection<MCDAVersion> mCdaVersions = responseEntity.getBody();

            for (MCDAVersion mcdaVersion : mCdaVersions) {
                ResponseEntity<Collection<MStdDict>> dict = dictClient.searchDict("", "sourceId=" + id, "", id.split(",").length, 1, mcdaVersion.getVersion());
                if (dict.getBody().size() > 0) {
                    envelop.setErrorMsg("该标准来源正在被标准字典使用，不可删除");
                    envelop.setSuccessFlg(false);
                    return envelop;
                }

                ResponseEntity<Collection<MStdDataSet>> dataSets = dataSetClient.searchDataSets("", "reference=" + id, "", id.split(",").length, 1, mcdaVersion.getVersion());
                if (dataSets.getBody().size() > 0) {
                    envelop.setErrorMsg("该标准来源正在被标准数据集使用，不可删除");
                    envelop.setSuccessFlg(false);
                    return envelop;
                }

                ResponseEntity<List<MCDADocument>> cdaDocument = cdaClient.GetCDADocuments("", "sourceId=" + id, "", id.split(",").length, 1, mcdaVersion.getVersion());
                if (cdaDocument.getBody().size() > 0) {
                    envelop.setErrorMsg("该标准来源正在被CDA文档使用，不可删除");
                    envelop.setSuccessFlg(false);
                    return envelop;
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return envelop;
    }

    @RequestMapping(value = "/source/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "通过id删除标准来源")
    public boolean delStdSource(
            @ApiParam(name = "id", value = "标准来源编号", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {

        return stdSourcrClient.delStdSource(id);
    }

    @RequestMapping(value = "/source/is_exist", method = RequestMethod.GET)
    public boolean isCodeExist(@RequestParam(value = "code") String code) {
        return stdSourcrClient.isCodeExist(code);
    }

    public MStdSource convertToMStdSource(StdSourceDetailModel detailModel) {
        if (detailModel == null) {
            return null;
        }
        MStdSource mStdSource = convertToModel(detailModel, MStdSource.class);
        mStdSource.setCreateDate(StringToDate(detailModel.getCreateDate(), AgAdminConstants.DateTimeFormat));
        mStdSource.setUpdateDate(StringToDate(detailModel.getUpdateDate(), AgAdminConstants.DateTimeFormat));

        return mStdSource;
    }
}
