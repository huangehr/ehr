package com.yihu.quota.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.quota.service.job.JobService;
import com.yihu.quota.vo.SaveModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 任务启动
 * @author janseny
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(description = "后台-任务控制")
public class JobController extends BaseController {
    @Autowired
    private JobService jobService;
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 启动任务
     * @param id
     * @return
     */
    @ApiOperation(value = "根据ID执行任务")
    @RequestMapping(value = "/job/execuJob", method = RequestMethod.GET)
    public boolean execuJob(
            @ApiParam(name = "id", value = "指标任务ID", required = true)
            @RequestParam(value = "id", required = true) Integer id) {
        try {
            jobService.execuJob(id);
            return true;
        } catch (Exception e) {
            error(e);
            invalidUserException(e, -1, "启动失败:" + e.getMessage());
            return false;
        }
    }


    /**
     * 查询结果
     * @param id
     * @return
     */
    @ApiOperation(value = "获取指标执行结果")
    @RequestMapping(value = "/tj/tjGetQuotaResult", method = RequestMethod.GET)
    public Envelop getQuotaResult(
            @ApiParam(name = "id", value = "指标任务ID", required = true)
            @RequestParam(value = "id" , required = true) int id,
            @ApiParam(name = "filters", value = "检索条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "pageNo", value = "页码", defaultValue = "0")
            @RequestParam(value = "pageNo" , required = false ,defaultValue = "0") int pageNo,
            @ApiParam(name = "pageSize", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "pageSize" , required = false ,defaultValue ="15") int pageSize
    ) {
        Envelop envelop = new Envelop();
        try {
            List<Map<String, Object>> resultList = jobService.getQuotaResult(id,filters,pageNo,pageSize);
            List<SaveModel> saveModelList = new ArrayList<SaveModel>();
            for(Map<String, Object> map : resultList){
                SaveModel saveModel =  objectMapper.convertValue(map, SaveModel.class);
                if(saveModel != null){
                    saveModelList.add(saveModel);
                }
            }
            int totalCount = jobService.getQuotaTotalCount();
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(saveModelList);
            envelop.setCurrPage(pageNo);
            envelop.setPageSize(pageSize);
            envelop.setTotalCount(totalCount);
            return envelop;
        } catch (Exception e) {
            error(e);
            invalidUserException(e, -1, "查询失败:" + e.getMessage());
        }
        envelop.setSuccessFlg(false);
        return envelop;
    }

}
