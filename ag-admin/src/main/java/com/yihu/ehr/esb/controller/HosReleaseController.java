package com.yihu.ehr.esb.controller;

import com.yihu.ehr.agModel.esb.HostReleaseModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.esb.client.HosReleaseClient;
import com.yihu.ehr.model.esb.MHosEsbMiniRelease;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.ehr.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
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
@Api(value = "程序版本发布信息管理接口", description = "程序版本发布信息管理接口" ,tags = {"ESB管理 -程序版本发布信息管理接口"})
public class HosReleaseController extends BaseController {

    @Autowired
    private HosReleaseClient hosReleaseClient;


    @RequestMapping(value = "/searchHosEsbMiniReleases", method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件获取程序版本发布信息", notes = "根据查询条件获取程序版本发布信息")
    public Envelop searchHosEsbMiniReleases(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws ParseException {
        ResponseEntity<List<MHosEsbMiniRelease>> responseEntity = hosReleaseClient.searchHosEsbMiniReleases(fields,filters,sorts,size,page);
        List<MHosEsbMiniRelease> mHosEsbMiniReleases = responseEntity.getBody();
        List<HostReleaseModel>  hostReleaseModels = new ArrayList<>();
        for(MHosEsbMiniRelease mHosEsbMiniRelease: mHosEsbMiniReleases){
            HostReleaseModel  hostReleaseModel = new HostReleaseModel();
            BeanUtils.copyProperties(mHosEsbMiniRelease,hostReleaseModel);
            if(mHosEsbMiniRelease.getReleaseTime()!=null) {
                hostReleaseModel.setReleaseDate(DateTimeUtil.simpleDateTimeFormat(mHosEsbMiniRelease.getReleaseTime()));
            }
            hostReleaseModels.add(hostReleaseModel);
        }
        int totalCount = getTotalCount (responseEntity);
        return getResult(hostReleaseModels, totalCount, page, size);


    }

    @RequestMapping(value = "/deleteHosEsbMiniRelease/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除程序版本发布信息", notes = "删除程序版本发布信息")
    public Envelop  deleteHosEsbMiniRelease(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
            String result =  hosReleaseClient.deleteHosEsbMiniRelease(id);
            if(result!="success"&&!"success".equals(result)){
                return failed(result);
            }else{
                return success("删除成功!");
            }
    }

    @RequestMapping(value = "/saveReleaseInfo", method = RequestMethod.POST)
    public Envelop saveDict(
            @ApiParam(name = "json_data", value = "软件版本信息")
            @RequestParam(value = "json_data") String jsonData) {

        try {
            MHosEsbMiniRelease mHosEsbMiniRelease = objectMapper.readValue(jsonData, MHosEsbMiniRelease.class);
            String errorMsg = "";
            if (StringUtils.isEmpty(mHosEsbMiniRelease.getSystemCode())) {
                errorMsg += "系统代码为空!";
            }
            if (StringUtils.isEmpty(mHosEsbMiniRelease.getFile())) {
                errorMsg += "文件路径不能为空!";
            }
            if (StringUtils.isEmpty(mHosEsbMiniRelease.getVersionName())) {
                errorMsg += "版本名称不能为空！";
            }
            if (StringUtils.isEmpty(""+mHosEsbMiniRelease.getVersionCode())) {
                errorMsg += "版本编号不能为空！";
            }
            if (StringUtils.isNotEmpty(errorMsg)) {
                return failed(errorMsg);
            }

            //系统代码唯一性校验


            if (StringUtils.isBlank(mHosEsbMiniRelease.getId())) {
                ResponseEntity<List<MHosEsbMiniRelease>> responseEntity = hosReleaseClient.searchHosEsbMiniReleases("","systemCode="+mHosEsbMiniRelease.getSystemCode(),"",1,1);
                List<MHosEsbMiniRelease> mHosEsbMiniReleases = responseEntity.getBody();
                if(mHosEsbMiniReleases.size()>0){
                    return failed("系统编码已经存在请修改！");
                }
                mHosEsbMiniRelease = hosReleaseClient.createHosEsbMiniRelease(objectMapper.writeValueAsString(mHosEsbMiniRelease));
            } else {
                ResponseEntity<List<MHosEsbMiniRelease>> responseEntity = hosReleaseClient.searchHosEsbMiniReleases("","systemCode="+mHosEsbMiniRelease.getSystemCode()+";id<>"+mHosEsbMiniRelease.getId(),"",1,1);
                List<MHosEsbMiniRelease> mHosEsbMiniReleases = responseEntity.getBody();
                if(mHosEsbMiniReleases.size()>0){
                    return failed("系统编码已经存在请修改！");
                }
                mHosEsbMiniRelease = hosReleaseClient.updateHosEsbMiniRelease(objectMapper.writeValueAsString(mHosEsbMiniRelease));
            }
             MHosEsbMiniRelease hosEsbMiniRelease = convertToModel(mHosEsbMiniRelease, MHosEsbMiniRelease.class);
            if (hosEsbMiniRelease == null) {
                return failed("保存失败!");
            }
            return success(hosEsbMiniRelease);
        } catch (Exception ex) {
            return failedSystem();
        }
    }
}