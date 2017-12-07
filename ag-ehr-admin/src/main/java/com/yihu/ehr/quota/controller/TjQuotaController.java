package com.yihu.ehr.quota.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.adapter.utils.ExtendController;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.entity.quota.TjQuota;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.resource.MRsMetadata;
import com.yihu.ehr.model.tj.MQuotaCategory;
import com.yihu.ehr.model.tj.MTjQuotaLog;
import com.yihu.ehr.model.tj.MTjQuotaModel;
import com.yihu.ehr.organization.service.OrganizationClient;
import com.yihu.ehr.quota.service.QuotaCategoryClient;
import com.yihu.ehr.quota.service.TjQuotaClient;
import com.yihu.ehr.quota.service.TjQuotaJobClient;
import com.yihu.ehr.quota.service.TjQuotaLogClient;
import com.yihu.ehr.resource.client.RsMetadataClient;
import com.yihu.ehr.systemdict.service.ConventionalDictEntryClient;
import com.yihu.ehr.util.FeignExceptionUtils;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.util.*;

/**
 * Created by Administrator on 2017/6/9.
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api( value = "TjQuota", description = "统计指标表", tags = {"统计指标管理-统计指标表"})
public class TjQuotaController extends ExtendController<MTjQuotaModel> {
    @Autowired
    private TjQuotaClient tjQuotaClient;
    @Autowired
    private TjQuotaJobClient tjQuotaJobClient;
    @Autowired
    private ConventionalDictEntryClient conventionalDictClient;
    @Autowired
    private QuotaCategoryClient quotaCategoryClient;
    @Autowired
    private RsMetadataClient metadataClient;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    TjQuotaLogClient tjQuotaLogClient;
    @Autowired
    OrganizationClient organizationClient;

    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaList, method = RequestMethod.GET)
    @ApiOperation(value = "统计指标表")
    public Envelop search(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page){

        ListResult listResult = tjQuotaClient.search(fields, filters, sorts, size, page);
        List<MTjQuotaModel> mainModelList  = new ArrayList<>();
        if(listResult.getTotalCount() != 0){
            List<Map<String,Object>> list = listResult.getDetailModelList();
            for(Map<String,Object> map : list){
                MTjQuotaModel tjQuotaModel = objectMapper.convertValue(map,MTjQuotaModel.class);
                if(tjQuotaModel.getCreateTime() != null){
                    Date createTime = DateUtil.parseDate(tjQuotaModel.getCreateTime(), "yyyy-MM-dd'T'HH:mm:ss'Z'Z");
                    tjQuotaModel.setCreateTime( DateTimeUtil.simpleDateTimeFormat(createTime));
                }
                if(tjQuotaModel.getUpdateTime() != null){
                    Date updateTime = DateUtil.parseDate(tjQuotaModel.getUpdateTime(),"yyyy-MM-dd'T'HH:mm:ss'Z'Z");
                    tjQuotaModel.setUpdateTime( DateTimeUtil.simpleDateTimeFormat(updateTime));
                }
                if(tjQuotaModel.getExecTime() != null){
                    Date execTime = DateUtil.parseDate(tjQuotaModel.getExecTime(),"yyyy-MM-dd'T'HH:mm:ss'Z'Z");
                    tjQuotaModel.setExecTime( DateTimeUtil.simpleDateTimeFormat(execTime));
                }
                //获取类别字典
                MConventionalDict dict = conventionalDictClient.getTjQuotaExecTypeList(tjQuotaModel.getExecType());
                tjQuotaModel.setExecTypeName(dict == null ? "" : dict.getValue());
                MConventionalDict dict2 = conventionalDictClient.getDimensionStatusList(String.valueOf(tjQuotaModel.getStatus()));
                tjQuotaModel.setStatusName(dict2 == null ? "" : dict2.getValue());
                MConventionalDict dict3 = conventionalDictClient.getTjQuotaDataLevelList(String.valueOf(tjQuotaModel.getDataLevel()));
                tjQuotaModel.setDataLevelName(dict3 == null ? "" : dict3.getValue());

                if(tjQuotaModel.getQuotaType() != null){
                    MQuotaCategory mQuotaCategory = quotaCategoryClient.searchQuotaCategoryDetail(tjQuotaModel.getQuotaType());
                    tjQuotaModel.setQuotaTypeName(mQuotaCategory == null ? "" :mQuotaCategory.getName());
                }

                mainModelList.add(tjQuotaModel);
            }
            return getResult(mainModelList, listResult.getTotalCount(), listResult.getCurrPage(), listResult.getPageSize());
        }else{
            Envelop envelop = new Envelop();
            return envelop;
        }
    }


    @RequestMapping(value = ServiceApi.TJ.AddTjQuota, method = RequestMethod.POST)
    @ApiOperation(value = "新增&修改统计指标表")
    public Envelop add(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam("model") String model) {
        try {
            ObjectResult objectResult = tjQuotaClient.add(model);
            if (objectResult.getCode() == 200) {
                MTjQuotaModel tjQuotaModel = objectMapper.readValue(model, MTjQuotaModel.class);
                if(tjQuotaModel.getId()==null){
                    int maxId = metadataClient.getMaxIdNumber();
                    maxId = maxId + 1;
                    String newId = "" + maxId;
                    for(int i=newId.length() ; i< 6 ;i++){
                        newId = "0" + newId;
                    }
                    //保存数据元
                    MRsMetadata metadata = new MRsMetadata();
                    metadata.setDomain("04");
                    metadata.setId("EHR_"+newId);
                    metadata.setStdCode("EHR_"+newId);
                    metadata.setName(tjQuotaModel.getName());
                    metadata.setColumnType("VARCHAR");
                    metadata.setNullAble("1");
                    metadata.setValid("1");
                    metadata.setDataSource(2);
                    metadata.setDescription("统计指标:" + tjQuotaModel.getName());
                    metadataClient.updateMetadata(objectMapper.writeValueAsString(metadata));
                    //更新指标表中的数据元code
                    TjQuota tjQuota = objectMapper.convertValue(objectResult.getData(), TjQuota.class);
                    tjQuota.setMetadataCode(metadata.getId());
                    tjQuotaClient.update(objectMapper.writeValueAsString(tjQuota));
                }
                return successObj(objectResult.getData());
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }

    @RequestMapping(value = ServiceApi.TJ.DeleteTjQuota, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除统计指标")
    public Envelop delete(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") Long id) {
        try {
            Result result = tjQuotaClient.delete(id);
            if(result.getCode() == 200){
                return successMsg(result.getMessage());
            }else{
                return failed("统计指标删除失败！");
            }
        }catch (Exception e){
            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }

    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaById, method = RequestMethod.GET)
    @ApiOperation(value = "根据ID查询指标")
    public Envelop getById(@PathVariable(value = "id") Long id) {
        try {
            MTjQuotaModel tjQuotaModel = tjQuotaClient.getById(id);
            if (null == tjQuotaModel) {
                return failed("获取指标失败");
            }
            if(tjQuotaModel.getQuotaType() != null){
                MQuotaCategory mQuotaCategory = quotaCategoryClient.searchQuotaCategoryDetail(tjQuotaModel.getQuotaType());
                tjQuotaModel.setQuotaTypeName(mQuotaCategory.getName());
            }
            //获取字典
            MConventionalDict dict = conventionalDictClient.getTjQuotaAlgorithm(tjQuotaModel.getJobClazz());
            tjQuotaModel.setJobClazzName(dict == null ? "" : dict.getCode());
            return success(tjQuotaModel);
        } catch (Exception e) {
            e.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = ServiceApi.TJ.TjQuotaExistsName, method = RequestMethod.GET)
    @ApiOperation(value = "校验name是否存在")
    public boolean hasExistsName(@PathVariable("name") String name) {
        return tjQuotaClient.hasExistsName(name);
    }

    @RequestMapping(value = ServiceApi.TJ.TjQuotaExistsCode, method = RequestMethod.GET)
    @ApiOperation(value = "校验code是否存在")
    public boolean hasExistsCode(@PathVariable("code") String code) {
        return tjQuotaClient.hasExistsCode(code);
    }



    @RequestMapping(value = ServiceApi.TJ.TjQuotaExecute, method = RequestMethod.GET)
    @ApiOperation(value = "指标执行")
    public Envelop execuJob(
            @ApiParam(name = "id")
            @RequestParam(value = "id") int id) throws Exception {
         tjQuotaJobClient.tjQuotaExecute(id);
         Date date = new Date();
         MTjQuotaModel quotaModel = tjQuotaClient.getById(Long.valueOf(String.valueOf(id)));
        Envelop envelop = new Envelop();
        MTjQuotaLog mTjQuotaLog = null;
        boolean flag = true;
        int count = 1;
        while (flag) {
            Thread.sleep(5 * 1000L);
            mTjQuotaLog = tjQuotaLogClient.getRecentRecord(quotaModel.getCode(),date.toString());
            if(mTjQuotaLog != null ){
                flag = false;
            }else {
                count ++;
            }
            if(count > 3){
                flag = false;
            }
         }
        if(mTjQuotaLog != null ){
            if(mTjQuotaLog.getStatus().equals("1")){
                envelop.setSuccessFlg(true);
            }else {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg(mTjQuotaLog.getContent());
            }
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(mTjQuotaLog.getContent());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.TJ.TjGetQuotaResult, method = RequestMethod.GET)
    @ApiOperation(value = "获取指标执行结果")
    public Envelop getQuotaResult(
            @ApiParam(name = "id" ,value = "指标ID" )
            @RequestParam(value = "id" , required = true) int id,
            @ApiParam(name = "userOrgList" ,value = "用户拥有机构权限" )
            @RequestParam(value = "userOrgList" , required = false) List<String> userOrgList,
            @ApiParam(name = "filters", value = "检索条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "pageNo", value = "页码", defaultValue = "0")
            @RequestParam(value = "pageNo" , required = false ,defaultValue = "0") int pageNo,
            @ApiParam(name = "pageSize", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "pageSize" , required = false ,defaultValue ="15") int pageSize
    ) throws Exception {
        //-----------------用户数据权限 start
        String org = "";
        if( userOrgList != null ){
            if( !(userOrgList.size()==1 && userOrgList.get(0).equals("null")) ){
                org = StringUtils.strip(String.join(",", userOrgList), "[]");
            }
        }
        Map<String, Object> params  = new HashMap<>();
        if(org.length() > 0){
            if(StringUtils.isNotEmpty(filters)){
                params  = objectMapper.readValue(filters, new TypeReference<Map>() {});
            }
            Object pOrg = params.get("org");
            if(pOrg == null ){
                params.put("org",org);
            }
            filters = objectMapper.writeValueAsString(params);
        }
        //-----------------用户数据权限 end
        if(filters != null){
            filters = URLEncoder.encode(filters, "UTF-8");
        }
        return tjQuotaJobClient.getQuotaResult(id,filters,pageNo,pageSize);
    }

    @RequestMapping(value = ServiceApi.TJ.TjHasConfigDimension, method = RequestMethod.GET)
    @ApiOperation(value = "校验code是否存在")
    public boolean hasConfigDimension(
            @ApiParam(name = "quotaCode", value = "指标编码")
            @RequestParam(value = "quotaCode") String quotaCode) {
        return tjQuotaClient.hasConfigDimension(quotaCode);
    }

}
