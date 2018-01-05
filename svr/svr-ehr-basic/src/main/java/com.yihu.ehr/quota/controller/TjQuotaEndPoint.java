package com.yihu.ehr.quota.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.quota.*;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.model.tj.MQuotaConfigModel;
import com.yihu.ehr.model.tj.MTjQuotaDataSaveModel;
import com.yihu.ehr.model.tj.MTjQuotaDataSourceModel;
import com.yihu.ehr.model.tj.MTjQuotaModel;
import com.yihu.ehr.quota.service.*;
import com.yihu.ehr.util.datetime.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/6/9.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "TjQuota", description = "统计表", tags = {"统计指标管理-统计表"})
public class TjQuotaEndPoint extends EnvelopRestEndPoint {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    TjQuotaService tjQuotaService;
    @Autowired
    TjQuotaDataSaveService tjQuotaDataSaveService;
    @Autowired
    TjQuotaDataSourceService tjQuotaDataSourceService;
    @Autowired
    TjDataSaveService tjDataSaveService;
    @Autowired
    TjDataSourceService tjDataSourceService;
    @Autowired
    TjQuotaDimensionMainService tjQuotaDimensionMainService;

    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaList, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询统计指标表")
    public ListResult getTjDataSaveList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ListResult listResult = new ListResult();
        List<TjQuota> tjQuota = tjQuotaService.search(fields, filters, sorts, page, size);
        if(tjQuota != null){
            listResult.setDetailModelList(tjQuota);
            listResult.setTotalCount((int)tjQuotaService.getCount(filters));
            listResult.setCode(200);
            listResult.setCurrPage(page);
            listResult.setPageSize(size);
        }else{
            listResult.setCode(200);
            listResult.setMessage("查询无数据");
            listResult.setTotalCount(0);
        }
        return listResult;
    }

    @RequestMapping(value = ServiceApi.TJ.AddTjQuota, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增&修改统计指标表")
    public ObjectResult add(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody String model) throws Exception{
        MTjQuotaModel tjQuotaModel = objectMapper.readValue(model, MTjQuotaModel.class);
        TjQuotaDataSource tjquotaDataSource = null;
        TjQuotaDataSave tjQuotaDataSave = null;
        if(tjQuotaModel.getTjQuotaDataSourceModel() != null){
            tjquotaDataSource = convertToModel(tjQuotaModel.getTjQuotaDataSourceModel(), TjQuotaDataSource.class);
            tjquotaDataSource.setQuotaCode(tjQuotaModel.getCode());
        }
        if(tjQuotaModel.getTjQuotaDataSourceModel() != null){
            tjQuotaDataSave = convertToModel(tjQuotaModel.getTjQuotaDataSaveModel(), TjQuotaDataSave.class);
            tjQuotaDataSave.setQuotaCode(tjQuotaModel.getCode());
        }
        TjQuota tjQuota = convertToModel(tjQuotaModel, TjQuota.class);
        String execTime = tjQuotaModel.getExecTime();
        tjQuota.setExecTime(DateUtil.strToDate(execTime));
        if (tjQuotaModel.getId() != null) {
            tjQuota.setUpdateTime(new Date());
            tjQuota.setCreateTime(DateUtil.strToDate(tjQuotaModel.getCreateTime()));
        } else{
            tjQuota.setStatus(1);
            tjQuota.setCreateTime(new Date());
        }
        tjQuota = tjQuotaService.saves(tjQuota, tjquotaDataSource, tjQuotaDataSave);
        return Result.success("统计表更新成功！", tjQuota);
    }

    @RequestMapping(value = ServiceApi.TJ.UpdateTjQuota, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改统计指标表")
    public Result update(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody String model) throws Exception{
        TjQuota tjQuota = objectMapper.readValue(model, TjQuota.class);
        tjQuotaService.save(tjQuota);
        return Result.success("统计指标表成功！");
    }

    @RequestMapping(value = ServiceApi.TJ.DeleteTjQuota, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除统计指标表")
    public Result delete(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") Long id) throws Exception{
        TjQuota tjQuota = tjQuotaService.getById(id);
        tjQuota.setStatus(-1);
        tjQuotaService.save(tjQuota);
        return Result.success("统计指标表删除成功！");
    }

    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaById, method = RequestMethod.GET)
    @ApiOperation(value = "根据ID获取指标")
    public MTjQuotaModel getById(
            @ApiParam(name = "id")
            @PathVariable(value = "id") Long id) {
        TjQuota tjQuota = tjQuotaService.getById(id);
        MTjQuotaModel mTjQuotaModel = null;
        if (null != tjQuota) {
            mTjQuotaModel = convertToModel(tjQuota, MTjQuotaModel.class);
            if(tjQuota.getExecTime() != null){
                mTjQuotaModel.setExecTime(DateUtil.toStringLong(tjQuota.getExecTime()));
            }
            if(tjQuota.getCreateTime() != null){
                mTjQuotaModel.setCreateTime(DateUtil.toStringLong(tjQuota.getCreateTime()));
            }
            if(tjQuota.getUpdateTime() != null){
                mTjQuotaModel.setUpdateTime(DateUtil.toStringLong(tjQuota.getUpdateTime()));
            }
            TjQuotaDataSave tjQuotaDataSave = tjQuotaDataSaveService.getByQuotaCode(tjQuota.getCode());
            TjQuotaDataSource tjQuotaDataSource = tjQuotaDataSourceService.getByQuotaCode(tjQuota.getCode());
            MTjQuotaDataSaveModel mTjQuotaDataSaveModel = new MTjQuotaDataSaveModel();
            MTjQuotaDataSourceModel mTjQuotaDataSourceModel = new MTjQuotaDataSourceModel();
            if (tjQuotaDataSave != null) {
                TjDataSave tjDataSave = tjDataSaveService.getByCode(tjQuotaDataSave.getSaveCode());
                mTjQuotaDataSaveModel.setId(tjQuotaDataSave.getId());
                mTjQuotaDataSaveModel.setSaveCode(tjQuotaDataSave.getSaveCode());
                mTjQuotaDataSaveModel.setQuotaCode(tjQuotaDataSave.getQuotaCode());
                mTjQuotaDataSaveModel.setConfigJson(tjQuotaDataSave.getConfigJson());
                mTjQuotaDataSaveModel.setName(tjDataSave.getName());
            }
            if (tjQuotaDataSource != null) {
                TjDataSource tjDataSource = tjDataSourceService.getByCode(tjQuotaDataSource.getSourceCode());
                mTjQuotaDataSourceModel.setId(tjQuotaDataSource.getId());
                mTjQuotaDataSourceModel.setQuotaCode(tjQuotaDataSource.getQuotaCode());
                mTjQuotaDataSourceModel.setSourceCode(tjQuotaDataSource.getSourceCode());
                mTjQuotaDataSourceModel.setConfigJson(tjQuotaDataSource.getConfigJson());
                mTjQuotaDataSourceModel.setName(tjDataSource.getName());
            }
            mTjQuotaModel.setTjQuotaDataSaveModel(mTjQuotaDataSaveModel);
            mTjQuotaModel.setTjQuotaDataSourceModel(mTjQuotaDataSourceModel);
        }
        return mTjQuotaModel;
    }

    @RequestMapping(value = ServiceApi.TJ.TjQuotaExistsName, method = RequestMethod.GET)
    @ApiOperation(value = "校验name是否存在")
    public boolean hasExistsName(
            @ApiParam(name = "name")
            @PathVariable("name") String name) throws Exception {
        String filter = "name=" + name;
        List<TjQuota> list = tjQuotaService.search(filter);
        if (list != null && list.size() > 0) {
            return true;
        }
        return false;
    }

    @RequestMapping(value = ServiceApi.TJ.TjQuotaExistsCode, method = RequestMethod.GET)
    @ApiOperation(value = "校验code是否存在")
    public boolean hasExistsCode(
            @ApiParam(name = "code")
            @PathVariable("code") String code) throws Exception {
        String filter = "code=" + code;
        List<TjQuota> list = tjQuotaService.search(filter);
        if (list != null && list.size() > 0) {
            return true;
        }
        return false;
    }

    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaByCode, method = RequestMethod.GET)
    @ApiOperation(value = "根据Code获取指标")
    public MTjQuotaModel getByCode(
            @ApiParam(name = "code")
            @RequestParam(value = "code") String code) {
        TjQuota tjQuota = tjQuotaService.findByCode(code);
        MTjQuotaModel mTjQuotaModel = convertToModel(tjQuota, MTjQuotaModel.class);
        return mTjQuotaModel;
    }

    @RequestMapping(value = ServiceApi.TJ.TjHasConfigDimension, method = RequestMethod.GET)
    @ApiOperation(value = "校验code是否存在")
    public boolean hasConfigDimension(
            @ApiParam(name = "quotaCode", value = "指标编码")
            @RequestParam(value = "quotaCode") String quotaCode) throws Exception {
        String filters = "quotaCode=" + quotaCode;
        List<TjQuotaDimensionMain> dimensionMainList = tjQuotaDimensionMainService.search(filters);
        if (null != dimensionMainList && dimensionMainList.size() > 0) {
            return true;
        }
        return false;
    }

    @RequestMapping(value = ServiceApi.TJ.TjQuotaConfigInfo, method = RequestMethod.GET)
    @ApiOperation(value = "分页获取指标配置")
    public ListResult quotaConfigInfo(
            @ApiParam(name = "quotaName", value = "指标名称", defaultValue = "")
            @RequestParam(value = "quotaName", required = false) String quotaName,
            @ApiParam(name = "page", value = "页码",defaultValue = "1")
            @RequestParam(value = "page") Integer page,
            @ApiParam(name = "pageSize", value = "每页大小",defaultValue = "15")
            @RequestParam(value = "pageSize") Integer pageSize) {
        ListResult listResult = new ListResult();
        List<MQuotaConfigModel> quotaConfigList = tjQuotaService.getQuotaConfig(quotaName, page, pageSize);
        if(quotaConfigList != null){
            listResult.setDetailModelList(quotaConfigList);
            listResult.setTotalCount((int)tjQuotaService.getCountInfo(quotaName));
            listResult.setCode(200);
            listResult.setCurrPage(page);
            listResult.setPageSize(pageSize);
        }else{
            listResult.setCode(200);
            listResult.setMessage("查询无数据");
            listResult.setTotalCount(0);
        }
        return listResult;
    }

    @RequestMapping(value = ServiceApi.TJ.TjQuotaTypeIsExist,method = RequestMethod.GET)
    @ApiOperation("获取已存在指标编码/指标名称")
    public List emailsExistence(
            @ApiParam(name = "type", value = "类型")
            @RequestParam(value = "type") String type,
            @ApiParam(name="json",value="json")
            @RequestBody String json) throws Exception {
        List values = tjQuotaService.isExist(type,toEntity(json, String[].class));
        return values;
    }
}
