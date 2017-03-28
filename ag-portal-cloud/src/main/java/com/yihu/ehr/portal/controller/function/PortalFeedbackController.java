package com.yihu.ehr.portal.controller.function;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.model.portal.MPortalFeedback;
import com.yihu.ehr.portal.service.function.PortalFeedbackClient;
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
@Api(value = "portalFeedback", description = "PortalFeedback", tags = {"意见反馈接口"})
public class PortalFeedbackController extends BaseController{

    @Autowired
    private PortalFeedbackClient portalFeedbackClient;


    @RequestMapping(value = "/portalFeedback", method = RequestMethod.GET)
    @ApiOperation(value = "获取意见反馈列表", notes = "根据查询条件获取意见反馈列表在前端表格展示")
    public Result searchPortalFeedback(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception
    {
        ResponseEntity<List<MPortalFeedback>> responseEntity = portalFeedbackClient.searchPortalFeedback(fields, filters, sorts, size, page);
        List<MPortalFeedback> mPortalFeedbackList = responseEntity.getBody();
        ListResult re = new ListResult(1, 10);
        re.setTotalCount(mPortalFeedbackList.size());
        re.setDetailModelList(mPortalFeedbackList);

        return re;
    }


    @RequestMapping(value = "portalFeedback/admin/{portalFeedback_id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取意见反馈信息", notes = "意见反馈信息")
    public Result getPortalFeedback(
            @ApiParam(name = "portalFeedback_id", value = "", defaultValue = "")
            @PathVariable(value = "portalFeedback_id") Long portalFeedbackId) throws Exception {

            MPortalFeedback mPortalFeedback = portalFeedbackClient.getPortalFeedback(portalFeedbackId);
            if (mPortalFeedback == null) {
                return Result.error("意见反馈信息获取失败!");
            }

            ObjectResult re = new ObjectResult(true,"通知公告信息获取成功！");
            re.setData(mPortalFeedback);
            return re;

    }

    @RequestMapping(value = "/portalFeedback/admin/{portalFeedback_id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除意见反馈", notes = "根据意见反馈id删除")
    public Result deletePortalFeedback(
            @ApiParam(name = "portalFeedback_id", value = "意见反馈编号", defaultValue = "")
            @PathVariable(value = "portalFeedback_id") String portalFeedbackId) throws Exception {

            boolean result = portalFeedbackClient.deletePortalFeedback(portalFeedbackId);
            if (!result) {
                return Result.error("删除失败!");
            }
            return Result.success("删除成功!");

    }


    @RequestMapping(value = "/portalFeedback", method = RequestMethod.POST)
    @ApiOperation(value = "创建意见反馈", notes = "重新绑定意见反馈信息")
    public Result createPortalFeedback(
            @ApiParam(name = "portalFeedback_json_data", value = "", defaultValue = "")
            @RequestParam(value = "portalFeedback_json_data") String portalFeedbackJsonData) throws Exception {

            MPortalFeedback mPortalFeedback = objectMapper.readValue(portalFeedbackJsonData, MPortalFeedback.class);

            portalFeedbackClient.createPortalFeedback(objectMapper.writeValueAsString(mPortalFeedback));
            if (mPortalFeedback == null) {
                return Result.error("创建失败");
            }

            ObjectResult re = new ObjectResult(true,"创建意见反馈成功！");
            re.setData(mPortalFeedback);
            return re;

    }


    @RequestMapping(value = "/portalFeedback", method = RequestMethod.PUT)
    @ApiOperation(value = "修改意见反馈", notes = "重新绑定意见反馈信息")
    public Result updatePortalFeedback(
            @ApiParam(name = "portalFeedback_json_data", value = "", defaultValue = "")
            @RequestParam(value = "portalFeedback_json_data") String portalFeedbackJsonData) throws Exception {

            MPortalFeedback mPortalFeedback = objectMapper.readValue(portalFeedbackJsonData, MPortalFeedback.class);

            portalFeedbackClient.updatePortalFeedback(objectMapper.writeValueAsString(mPortalFeedback));
            if(mPortalFeedback==null){
                return Result.error("保存失败!");
            }
            ObjectResult re = new ObjectResult(true,"修改意见反馈成功！");
            re.setData(mPortalFeedback);
            return re;

    }


}
