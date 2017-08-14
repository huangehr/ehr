package com.yihu.ehr.resource.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.resource.MRsReportCategory;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;
import java.util.List;

/**
 * 资源报表分类 client
 *
 * @author 张进军
 * @created 2017.8.8 19:22
 */
@FeignClient(name = MicroServices.Resource)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface RsReportCategoryClient {

    @ApiOperation("根据ID获取资源报表分类")
    @RequestMapping(value = ServiceApi.Resources.RsReportCategory, method = RequestMethod.GET)
    MRsReportCategory getById(
            @ApiParam(name = "id", value = "主键", required = true)
            @PathVariable(value = "id") Integer id);

    @ApiOperation(value = "根据父级ID获取下级")
    @RequestMapping(value = ServiceApi.Resources.RsReportCategoryChildrenByPid, method = RequestMethod.GET)
    List<MRsReportCategory> getChildrenByPid(
            @ApiParam(name = "pid", value = "父级ID")
            @RequestParam("pid") Integer pid);

    @ApiOperation(value = "根据条件获取资源报表分类")
    @RequestMapping(value = ServiceApi.Resources.RsReportCategories, method = RequestMethod.GET)
    ResponseEntity<Collection<MRsReportCategory>> search(
            @ApiParam(name = "filters", value = "筛选条件")
            @RequestParam(value = "filters", required = false) String filters);

    @ApiOperation(value = "获取资源报表分类的树形下拉框数据")
    @RequestMapping(value = ServiceApi.Resources.RsReportCategoryComboTree, method = RequestMethod.GET)
    public List<MRsReportCategory> getComboTreeData(
//            @ApiParam(name = "name", value = "资源报表分类名称")
//            @RequestParam(value = "name", required = false) String name
    );

    @ApiOperation("新增资源报表分类")
    @RequestMapping(value = ServiceApi.Resources.RsReportCategorySave, method = RequestMethod.POST)
    MRsReportCategory add(
            @ApiParam(name = "mrsReportCategory", value = "报表分类JSON字符串", required = true)
            @RequestBody String mrsReportCategory);

    @ApiOperation("更新资源报表分类")
    @RequestMapping(value = ServiceApi.Resources.RsReportCategorySave, method = RequestMethod.PUT)
    MRsReportCategory update(
            @ApiParam(name = "mrsReportCategory", value = "报表分类JSON字符串", required = true)
            @RequestBody String mrsReportCategory);

    @ApiOperation("删除资源报表分类")
    @RequestMapping(value = ServiceApi.Resources.RsReportCategoryDelete, method = RequestMethod.DELETE)
    boolean delete(
            @ApiParam(name = "id", value = "主键", required = true)
            @PathVariable(value = "id") Integer id);


    @ApiOperation("验证资源报表分类编码是否唯一")
    @RequestMapping(value = ServiceApi.Resources.RsReportCategoryIsUniqueCode, method = RequestMethod.GET)
    Boolean isUniqueCode(
            @ApiParam(name = "id", value = "资源报表分类ID", required = true)
            @RequestParam("id") Integer id,
            @ApiParam(name = "code", value = "资源报表分类编码", required = true)
            @RequestParam("code") String code);

    @ApiOperation("验证资源报表分类名称是否唯一")
    @RequestMapping(value = ServiceApi.Resources.RsReportCategoryIsUniqueName, method = RequestMethod.GET)
    Boolean isUniqueName(
            @ApiParam(name = "id", value = "资源报表分类ID", required = true)
            @RequestParam("id") Integer id,
            @ApiParam(name = "name", value = "资源报表分类名称", required = true)
            @RequestParam("name") String name);

}
