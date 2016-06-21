package com.yihu.ehr.esb.controller;

import com.yihu.ehr.agModel.esb.HosSqlTaskModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.esb.client.HosSqlTaskClient;
import com.yihu.ehr.model.esb.MHosSqlTask;
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
@Api(value = "his穿透管理接口", description = "his穿透管理接口" ,tags = {"his穿透管理接口"})
public class HosSqlTaskController extends BaseController {

    @Autowired
    private HosSqlTaskClient hosSqlTaskClient;


    @RequestMapping(value = "/searchHosSqlTasks", method = RequestMethod.GET)
    @ApiOperation(value = "根据条件进行his穿透查询", notes = "根据条件进行his穿透查询")
    public Envelop searchHosSqlTasks(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception {
        ResponseEntity<List<MHosSqlTask>> responseEntity = hosSqlTaskClient.searchHosSqlTasks(fields,filters,sorts,size,page);
        List<MHosSqlTask> hosSqlTasks = responseEntity.getBody();

        List<HosSqlTaskModel> hosSqlTaskModels = new ArrayList<>();
        for(MHosSqlTask mHosSqlTask : hosSqlTasks) {
            HosSqlTaskModel hosSqlTaskModel = null;
            try {
                hosSqlTaskModel = convertToModelDetail(mHosSqlTask,HosSqlTaskModel.class);
                hosSqlTaskModel.setCreateTime(DateTimeUtil.simpleDateTimeFormat(mHosSqlTask.getCreateTime()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            hosSqlTaskModels.add(hosSqlTaskModel);
        }
        Envelop envelop = getResult(hosSqlTaskModels, getTotalCount(responseEntity), page, size);
        return envelop;
    }

    @RequestMapping(value = "/hosSqlTask/{id}",method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取his穿透信息",notes = "根据id获取his穿透信息")
    public Envelop getHosSqlTask(
            @ApiParam(name = "id",value ="",defaultValue = "")
            @PathVariable(value = "id") String id) throws  Exception{
        Envelop envelop = new Envelop();
        MHosSqlTask mHosSqlTask = hosSqlTaskClient.getHosSqlTask(id);
        try{
            HosSqlTaskModel hosSqlTaskModel = convertToModelDetail(mHosSqlTask,HosSqlTaskModel.class);
            hosSqlTaskModel.setCreateTime(DateTimeUtil.simpleDateTimeFormat(mHosSqlTask.getCreateTime()));
            envelop.setObj(hosSqlTaskModel);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }


    @RequestMapping(value = "/createHosSqlTask", method = RequestMethod.POST)
    @ApiOperation(value = "创建his穿透信息", notes = "创建his穿透信息")
    public Envelop createHosSqlTask(
            @ApiParam(name = "json_data", value = "", defaultValue = "")
            @RequestParam(value = "json_data", required = true) String jsonData) throws Exception {
        Envelop envelop = new Envelop();
        HosSqlTaskModel hosSqlTaskModel = toEntity(jsonData,HosSqlTaskModel.class);
        try {
            MHosSqlTask hosSqlTask = convertToMModel(hosSqlTaskModel,MHosSqlTask.class);
            hosSqlTaskClient.createHosSqlTask(toJson(hosSqlTask));
            convertToModelDetail(hosSqlTask,HosSqlTaskModel.class);
            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = "/updateHosSqlTask", method = RequestMethod.PUT)
    @ApiOperation(value = "修改his穿透信息", notes = "修改his穿透信息")
    public Envelop updateHosSqlTask(
            @ApiParam(name = "json_data", value = "")
            @RequestParam(value = "json_data") String jsonData) throws Exception {
        Envelop envelop = new Envelop();
        HosSqlTaskModel hosSqlTaskModel = toEntity(jsonData,HosSqlTaskModel.class);
        try {
            MHosSqlTask hosSqlTask = convertToMModel(hosSqlTaskModel,MHosSqlTask.class);
            hosSqlTaskClient.updateHosSqlTask(toJson(hosSqlTask));
            convertToModelDetail(hosSqlTask,HosSqlTaskModel.class);
            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }


    @RequestMapping(value = "/deleteHosSqlTask/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除his穿透信息", notes = "删除his穿透信息")
    public Envelop deleteHosSqlTask(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        Envelop envelop = new Envelop();
        try {
            hosSqlTaskClient.deleteHosSqlTask(id);
            envelop.setSuccessFlg(true);
        }catch (Exception e){e.printStackTrace();
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;    }
}