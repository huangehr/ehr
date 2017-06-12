package com.yihu.ehr.tj.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.tj.TjQuota;
import com.yihu.ehr.entity.tj.TjQuotaDataSave;
import com.yihu.ehr.entity.tj.TjQuotaDataSource;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.model.tj.MTjQuotaDataSaveModel;
import com.yihu.ehr.model.tj.MTjQuotaModel;
import com.yihu.ehr.model.tj.MTjquotaDataSourceModel;
import com.yihu.ehr.tj.service.TjQuotaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaList, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询统计表")
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
    @ApiOperation(value = "新增&修改统计表")
    public ObjectResult add(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody String model) throws Exception{
        /*TjQuota obj = objectMapper.readValue(model, TjQuota.class);
        obj = tjQuotaService.save(obj);
        return Result.success("统计表更新成功！", obj);*/
        MTjQuotaModel tjQuotaModel = objectMapper.readValue(model, MTjQuotaModel.class);
        TjQuotaDataSource tjquotaDataSource = new TjQuotaDataSource();
        BeanUtils.copyProperties(tjQuotaModel.getTjquotaDataSourceModel(), tjquotaDataSource);
        TjQuotaDataSave tjQuotaDataSave = new TjQuotaDataSave();
        BeanUtils.copyProperties(tjQuotaModel.getTjQuotaDataSaveModel(),tjQuotaDataSave);
        tjquotaDataSource.setQuotaCode(tjQuotaModel.getCode());
        tjQuotaDataSave.setQuotaCode(tjQuotaModel.getCode());
        TjQuota tjQuota = new TjQuota();
        BeanUtils.copyProperties(tjQuotaModel, tjQuota);
        tjQuotaService.saves(tjQuota, tjquotaDataSource, tjQuotaDataSave);
        return Result.success("统计表更新成功！", tjQuotaModel);
    }


    @RequestMapping(value = ServiceApi.TJ.DeleteTjQuota, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除统计表")
    public Result delete(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") Long id) throws Exception{
        tjQuotaService.delete(id);
        return Result.success("统计表删除成功！");
    }

    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaById, method = RequestMethod.GET)
    @ApiOperation(value = "根据ID获取指标")
    public TjQuota getById(
            @ApiParam(name = "id")
            @PathVariable(value = "id") Long id) {
        TjQuota tjQuota = tjQuotaService.getById(id);
        return tjQuota;
    }
}
