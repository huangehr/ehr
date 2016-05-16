package com.yihu.ehr.esb.controller;

import com.yihu.ehr.agModel.dict.SystemDictModel;
import com.yihu.ehr.agModel.esb.HosAcqTaskModel;
import com.yihu.ehr.constants.AgAdminConstants;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.esb.client.HosAcqTaskClient;
import com.yihu.ehr.model.dict.MSystemDict;
import com.yihu.ehr.model.esb.MHosAcqTask;
import com.yihu.ehr.model.esb.MHosLog;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author linaz
 * @created 2016.05.12 18:00
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin/esb")
@RestController
@Api(value = "补采任务管理接口", description = "补采任务管理接口" ,tags = {"补采任务管理接口"})
public class HosAcqTaskController extends BaseController {

    @Autowired
    private HosAcqTaskClient hosAcqTaskClient;

    @RequestMapping(value = "/searchHosAcqTasks", method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件获取补采任务信息", notes = "根据查询条件获取补采任务信息")
    public Envelop searchHosAcqTasks(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws ParseException {
        ResponseEntity<List<MHosAcqTask>> responseEntity = hosAcqTaskClient.searchHosAcqTasks(fields,filters,sorts,size,page);
        List<MHosAcqTask> hosAcqTasks = responseEntity.getBody();

        List<HosAcqTaskModel> hosAcqTaskModels = new ArrayList<>();
        for(MHosAcqTask mHosAcqTask : hosAcqTasks) {
            HosAcqTaskModel hosAcqTaskModel = convert2DtoModel(mHosAcqTask);
            hosAcqTaskModels.add(hosAcqTaskModel);
        }
        Envelop envelop = getResult(hosAcqTaskModels, getTotalCount(responseEntity), page, size);
        return envelop;
    }



    @RequestMapping(value = "/createHosAcqTask", method = RequestMethod.POST)
    @ApiOperation(value = "创建补采任务信息", notes = "创建补采任务信息")
    public MHosAcqTask createHosAcqTask(
            @ApiParam(name = "json_data", value = "", defaultValue = "")
            @RequestParam(value = "json_data", required = true) String jsonData) throws Exception {
        return hosAcqTaskClient.createHosAcqTask(jsonData.trim());
    }

    @RequestMapping(value = "/updateHosAcqTask", method = RequestMethod.PUT)
    @ApiOperation(value = "修改补采任务信息", notes = "修改补采任务信息")
    public MHosAcqTask updateHosAcqTask(
            @ApiParam(name = "json_data", value = "")
            @RequestParam(value = "json_data") String jsonData) throws Exception {
        return hosAcqTaskClient.updateHosAcqTask(jsonData.trim());
    }


    @RequestMapping(value = "/deleteHosAcqTask/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除补采任务信息", notes = "删除补采任务信息")
    public boolean deleteHosAcqTask(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        return hosAcqTaskClient.deleteHosAcqTask(id);
    }


    public HosAcqTaskModel convert2DtoModel(MHosAcqTask hosAcqTask) {
        if(hosAcqTask==null) {
            return null;
        }
        HosAcqTaskModel hosAcqTaskModel = convertToModel(hosAcqTask,HosAcqTaskModel.class);
        hosAcqTaskModel.setCreateTime(DateToString(hosAcqTask.getCreateTime(), AgAdminConstants.DateTimeFormat));
        hosAcqTaskModel.setStartTime(DateToString(hosAcqTask.getStartTime(), AgAdminConstants.DateTimeFormat));
        hosAcqTaskModel.setEndTime(DateToString(hosAcqTask.getEndTime(), AgAdminConstants.DateTimeFormat));
        return hosAcqTaskModel;
    }
}