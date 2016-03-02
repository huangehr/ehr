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
import com.yihu.ehr.util.operator.DateUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by yww on 2016/3/1.
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
        Collection<MStdSource> stdSources = stdSourcrClient.searchSources(fields, filters, sorts, size, page);
        List<StdSourceModel> sourcrModelList = new ArrayList<>();
        for (MStdSource stdSource : stdSources) {
            StdSourceModel sourceModel = convertToModel(stdSource, StdSourceModel.class);
            //标准来源类型字典
            MConventionalDict sourcerTypeDict = conDictEntryClient.getStdSourceType(stdSource.getSourceType());
            sourceModel.setSourceValue(sourcerTypeDict == null ? "" : sourcerTypeDict.getValue());
            sourceModel.setCreate_date(DateUtil.formatDate(stdSource.getCreate_date(), DateUtil.DEFAULT_YMDHMSDATE_FORMAT));
            sourcrModelList.add(sourceModel);
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
        sourceDetailModel.setSourceValue(sourcerTypeDict == null ? "" : sourcerTypeDict.getValue());
        return sourceDetailModel;
    }


    @RequestMapping(value = "/source", method = RequestMethod.PUT)
    @ApiOperation(value = "修改标准来源，通过id取数据，取不到数据时新增，否则修改")
    public Envelop updateStdSource(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam(value = "model") String model) throws Exception {
        Envelop envelop = new Envelop();
        MStdSource mStdSource = stdSourcrClient.updateStdSource(model);
        if (mStdSource == null) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("标准来源更新失败！");
            return envelop;
        }
        envelop.setSuccessFlg(true);
        envelop.setObj(getStdSourceDetailModel(mStdSource));
        return envelop;
    }

    @RequestMapping(value = "/source", method = RequestMethod.POST)
    @ApiOperation(value = "新增标准来源")
    public Envelop addStdSource(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam(value = "model") String model) throws Exception {
        Envelop envelop = new Envelop();
        MStdSource mStdSource = stdSourcrClient.addStdSource(model);
        if (mStdSource == null) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("新增标准来源失败！");
            return envelop;
        }
        envelop.setSuccessFlg(true);
        envelop.setObj(getStdSourceDetailModel(mStdSource));
        return envelop;
    }


    @RequestMapping(value = "/sources", method = RequestMethod.DELETE)
    @ApiOperation(value = "通过id组删除标准来源，多个id以,分隔")
    public boolean delStdSources(
            @ApiParam(name = "ids", value = "标准来源编号", defaultValue = "")
            @RequestParam(value = "ids") String ids) throws Exception {

        return stdSourcrClient.delStdSources(ids);

    }

    @RequestMapping(value = "/source/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "通过id删除标准来源")
    public boolean delStdSource(
            @ApiParam(name = "id", value = "标准来源编号", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {

        return stdSourcrClient.delStdSource(id);
    }
}
