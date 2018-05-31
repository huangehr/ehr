package com.yihu.ehr.resource.client;

import com.yihu.ehr.agModel.resource.RsReportModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.resource.MRsReport;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 资源报表 client
 *
 * @author 张进军
 * @created 2017.8.15 19:18
 */
@FeignClient(name = MicroServices.Resource)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface RsReportClient {

    @ApiOperation("根据ID获取资源报表")
    @RequestMapping(value = ServiceApi.Resources.RsReport, method = RequestMethod.GET)
    MRsReport getById(
            @ApiParam(name = "id", value = "主键", required = true)
            @PathVariable(value = "id") Integer id);

    @ApiOperation("根据编码获取资源报表")
    @RequestMapping(value = ServiceApi.Resources.RsReportFindByCode, method = RequestMethod.GET)
    MRsReport findByCode(
            @ApiParam(name = "code", value = "编码", required = true)
            @RequestParam(value = "code") String code);

    @ApiOperation(value = "根据条件获取资源报表")
    @RequestMapping(value = ServiceApi.Resources.RsReports, method = RequestMethod.GET)
    ResponseEntity<List<MRsReport>> search(
            @ApiParam(name = "fields", value = "返回字段，为空返回全部字段")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "筛选条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小")
            @RequestParam(value = "size", required = false) int size);

    @ApiOperation("新增资源报表")
    @RequestMapping(value = ServiceApi.Resources.RsReportSave, method = RequestMethod.POST)
    MRsReport add(
            @ApiParam(name = "rsReport", value = "资源报表JSON字符串", required = true)
            @RequestBody String rsReport);

    @ApiOperation("更新资源报表")
    @RequestMapping(value = ServiceApi.Resources.RsReportSave, method = RequestMethod.PUT)
    MRsReport update(
            @ApiParam(name = "rsReport", value = "资源报表JSON字符串", required = true)
            @RequestBody String rsReport);

    @ApiOperation("删除资源报表")
    @RequestMapping(value = ServiceApi.Resources.RsReportDelete, method = RequestMethod.DELETE)
    void delete(
            @ApiParam(name = "id", value = "资源报表ID", required = true)
            @RequestParam(value = "id") Integer id);

    @ApiOperation("验证资源报表编码是否唯一")
    @RequestMapping(value = ServiceApi.Resources.RsReportIsUniqueCode, method = RequestMethod.GET)
    Boolean isUniqueCode(
            @ApiParam(name = "id", value = "资源报表ID", required = true)
            @RequestParam("id") Integer id,
            @ApiParam(name = "code", value = "资源报表编码", required = true)
            @RequestParam("code") String code);

    @ApiOperation("验证资源报表名称是否唯一")
    @RequestMapping(value = ServiceApi.Resources.RsReportIsUniqueName, method = RequestMethod.GET)
    Boolean isUniqueName(
            @ApiParam(name = "id", value = "资源报表ID", required = true)
            @RequestParam("id") Integer id,
            @ApiParam(name = "name", value = "资源报表名称", required = true)
            @RequestParam("name") String name);

    @ApiOperation("查询报表信息（不分页）")
    @RequestMapping(value = ServiceApi.Resources.RsReportNoPage, method = RequestMethod.GET)
    List<MRsReport> queryNoPageResources(
            @RequestParam(value = "filters", required = false) String filters);

    @ApiOperation("获取报表模版内容")
    @RequestMapping(value = ServiceApi.Resources.RsReportTemplateContent, method = RequestMethod.GET)
    String getTemplateContent(
            @ApiParam(name = "reportCode", value = "资源报表Code", required = true)
            @RequestParam(value = "reportCode") String reportCode);

    @ApiOperation("判断资源报表分类是否被使用")
    @RequestMapping(value = ServiceApi.Resources.RsReportIsCategoryApplied, method = RequestMethod.GET)
    boolean isCategoryApplied(
            @ApiParam(name = "reportCategoryId", value = "资源报表分类ID", required = true)
            @RequestParam(value = "reportCategoryId") Integer reportCategoryId);

    @ApiOperation("根据资源分类编码获取资源报表分")
    @RequestMapping(value = ServiceApi.Resources.RsReportByCategoryId, method = RequestMethod.GET)
    List<RsReportModel> getByCategoryId(
            @ApiParam(name = "reportCategoryId", value = "资源报表分类ID", required = true)
            @RequestParam(value = "reportCategoryId") Integer reportCategoryId);

    @ApiOperation("根据报表编码获取视图位置")
    @RequestMapping(value = ServiceApi.Resources.GetPositionMapByCode, method = RequestMethod.GET)
    Envelop getPositionByCode(
            @RequestParam(value = "code") String code);

    @ApiOperation(value = "根据报表名称模糊查询")
    @RequestMapping(value = ServiceApi.Resources.GetRsReportByParam, method = RequestMethod.GET)
    Envelop searchReportByName(
            @RequestParam(value = "filters", required = false) String filters);
}
