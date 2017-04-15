package com.yihu.ehr.portal.controller.function;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.model.portal.MPortalStandards;
import com.yihu.ehr.portal.service.function.PortalStandardsClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by yeshijie on 2017/2/21.
 */
@EnableFeignClients
@RequestMapping(ApiVersion.Version1_0 + "/portal")
@RestController
@Api(value = "portalStandards", description = "PortalStandards", tags = {"云门户-标准规范管理接口"})
public class PortalStandardsController extends BaseController{

    @Autowired
    private PortalStandardsClient portalStandardsClient;


    @RequestMapping(value = "/portalStandards", method = RequestMethod.GET)
    @ApiOperation(value = "获取标准规范管理列表", notes = "根据查询条件获取标准规范管理列表在前端表格展示")
    public Result searchPortalStandards(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception {

        ResponseEntity<List<MPortalStandards>> responseEntity = portalStandardsClient.searchPortalStandards(fields, filters, sorts, size, page);


        ListResult re = new ListResult(page, size);
        re.setTotalCount(getTotalCount(responseEntity));
        re.setDetailModelList(responseEntity.getBody());

        return re;
    }


    @RequestMapping(value = "portalStandards/admin/{portalStandard_id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取标准规范管理信息", notes = "标准规范管理信息")
    public Result getPortalStandard(
            @ApiParam(name = "portalStandard_id", value = "", defaultValue = "")
            @PathVariable(value = "portalStandard_id") Long portalStandardId) throws Exception {

            MPortalStandards mPortalStandards = portalStandardsClient.getPortalStandard(portalStandardId);
            if (mPortalStandards == null) {
                return Result.error("标准规范管理信息获取失败!");
            }

            ObjectResult re= new ObjectResult(true,"标准规范管理信息获取成功!");
            re.setData(mPortalStandards);
            return re;

    }

    @RequestMapping(value = "/portalStandards/admin/{portalStandard_id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除标准规范管理", notes = "根据标准规范管理id删除医生")
    public Result deletePortalStandard(
            @ApiParam(name = "portalStandard_id", value = "标准规范管理编号", defaultValue = "")
            @PathVariable(value = "portalStandard_id") String portalStandardId) throws Exception {

            boolean result = portalStandardsClient.deletePortalStandard(portalStandardId);
            if (!result) {
                return Result.error("删除失败!");
            }
            return Result.success("删除成功!");

    }


    @RequestMapping(value = "/portalStandard", method = RequestMethod.POST)
    @ApiOperation(value = "创建标准规范管理", notes = "重新绑定标准规范管理信息")
    public Result createPortalStandard(
            @ApiParam(name = "portalStandard_json_data", value = "", defaultValue = "")
            @RequestParam(value = "portalStandard_json_data") String portalStandardJsonData) throws Exception {

            MPortalStandards mPortalStandard = portalStandardsClient.createPortalStandard(portalStandardJsonData);
            if (mPortalStandard == null) {
                return Result.error("保存失败!");
            }

            ObjectResult re= new ObjectResult(true,"保存成功!");
            re.setData(mPortalStandard);
            return re;

    }


    @RequestMapping(value = "/portalStandard", method = RequestMethod.PUT)
    @ApiOperation(value = "修改标准规范管理", notes = "重新绑定标准规范管理信息")
    public Result updatePortalStandard(
            @ApiParam(name = "portalStandard_json_data", value = "", defaultValue = "")
            @RequestParam(value = "portalStandard_json_data") String portalStandardJsonData) throws Exception {


            MPortalStandards mPortalStandard = portalStandardsClient.updatePortalStandard(portalStandardJsonData);
            if(mPortalStandard==null){
                return Result.error("修改失败!");
            }

            ObjectResult re= new ObjectResult(true,"修改成功!");
            re.setData(mPortalStandard);
            return re;

    }


}
