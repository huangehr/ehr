package com.yihu.ehr.esb.controller;

import com.yihu.ehr.agModel.esb.HosAcqTaskModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.esb.client.HosAcqTaskClient;
import com.yihu.ehr.model.esb.MHosAcqTask;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.ehr.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author linaz
 * @created 2016.05.12 18:00
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin/esb")
@RestController
@Api(value = "补采任务管理接口", description = "补采任务管理接口" ,tags = {"ESB管理 -补采任务管理接口"})
public class HosAcqTaskController extends BaseController {

    @Autowired
    private HosAcqTaskClient hosAcqTaskClient;

    @RequestMapping(value = "/searchHosAcqTasks", method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件获取补采任务信息", notes = "根据查询条件获取补采任务信息")
    public Envelop searchHosAcqTasks(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page)  {
        ResponseEntity<List<MHosAcqTask>> responseEntity = hosAcqTaskClient.searchHosAcqTasks(fields,filters,sorts,size,page);
        List<MHosAcqTask> hosAcqTasks = responseEntity.getBody();

        List<HosAcqTaskModel> hosAcqTaskModels = new ArrayList<>();
        for(MHosAcqTask mHosAcqTask : hosAcqTasks) {
            HosAcqTaskModel hosAcqTaskModel = null;
            try {
                hosAcqTaskModel = convertToModelDetail(mHosAcqTask,HosAcqTaskModel.class);
                hosAcqTaskModel = utcTimeChange(hosAcqTaskModel,mHosAcqTask);
            } catch (Exception e) {
                e.printStackTrace();
            }
            hosAcqTaskModels.add(hosAcqTaskModel);
        }
        Envelop envelop = getResult(hosAcqTaskModels, getTotalCount(responseEntity), page, size);
        return envelop;
    }

    /**
     * utc时间转化成yyyy-MM-dd HH:mm:ss
     */
    public HosAcqTaskModel utcTimeChange(HosAcqTaskModel hosAcqTaskModel,MHosAcqTask mHosAcqTask){
        hosAcqTaskModel.setStartTime(DateTimeUtil.simpleDateTimeFormat(mHosAcqTask.getStartTime()));
        hosAcqTaskModel.setEndTime(DateTimeUtil.simpleDateTimeFormat(mHosAcqTask.getEndTime()));
        hosAcqTaskModel.setCreateTime(DateTimeUtil.simpleDateTimeFormat(mHosAcqTask.getCreateTime()));
        return hosAcqTaskModel;
    }

    @RequestMapping(value = "/hosAcqTask/{id}",method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取补采任务信息",notes = "根据id获取补采任务信息")
    public Envelop getHosAcqTask(
            @ApiParam(name = "id",value ="",defaultValue = "")
            @PathVariable(value = "id") String id){
        Envelop envelop = new Envelop();
        MHosAcqTask mHosAcqTask = hosAcqTaskClient.getHosAcqTask(id);
        try{
            HosAcqTaskModel hosAcqTaskModel = convertToModelDetail(mHosAcqTask,HosAcqTaskModel.class);
            hosAcqTaskModel = utcTimeChange(hosAcqTaskModel,mHosAcqTask);
            envelop.setObj(hosAcqTaskModel);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = "/createHosAcqTask", method = RequestMethod.POST)
    @ApiOperation(value = "创建补采任务信息", notes = "创建补采任务信息")
    public Envelop createHosAcqTask(
            @ApiParam(name = "json_data", value = "", defaultValue = "")
            @RequestParam(value = "json_data") String jsonData)  {
        Envelop envelop = new Envelop();
        HosAcqTaskModel hosAcqTaskModel = toEntity(jsonData,HosAcqTaskModel.class);
        try {
            MHosAcqTask hosAcqTask = convertToMModel(hosAcqTaskModel,MHosAcqTask.class);
            MHosAcqTask mHosAcqTaskNew = hosAcqTaskClient.createHosAcqTask(toJson(hosAcqTask));
            convertToModelDetail(mHosAcqTaskNew,HosAcqTaskModel.class);
            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;

    }

    @RequestMapping(value = "/updateHosAcqTask", method = RequestMethod.PUT)
    @ApiOperation(value = "修改补采任务信息", notes = "修改补采任务信息")
    public Envelop updateHosAcqTask(
            @ApiParam(name = "json_data", value = "")
            @RequestParam(value = "json_data") String jsonData)  {
        Envelop envelop = new Envelop();
        try {
            HosAcqTaskModel hosAcqTaskModel = toEntity(jsonData,HosAcqTaskModel.class);
            MHosAcqTask hosAcqTask = convertToMModel(hosAcqTaskModel,MHosAcqTask.class);
            hosAcqTaskClient.updateHosAcqTask(toJson(hosAcqTask));
            convertToModelDetail(hosAcqTask,HosAcqTaskModel.class);
            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }


    @RequestMapping(value = "/deleteHosAcqTask/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除补采任务信息", notes = "删除补采任务信息")
    public Envelop deleteHosAcqTask(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) {
        Envelop envelop = new Envelop();
        try {
            hosAcqTaskClient.deleteHosAcqTask(id);
            envelop.setSuccessFlg(true);
        }catch (Exception e){e.printStackTrace();

            envelop.setSuccessFlg(false);
        }
        return envelop;
    }


}