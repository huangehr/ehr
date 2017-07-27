package com.yihu.ehr.quota.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.tj.QuotaReportModel;
import com.yihu.ehr.agModel.tj.ReultModel;
import com.yihu.ehr.agModel.tj.TjQuotaModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.model.tj.MTjQuotaModel;
import com.yihu.ehr.model.tj.MTjQuotaWarn;
import com.yihu.ehr.portal.service.function.GetInfoClient;
import com.yihu.ehr.portal.service.function.OrganizationClient;
import com.yihu.ehr.portal.service.function.UserClient;
import com.yihu.ehr.quota.service.TjQuotaClient;
import com.yihu.ehr.quota.service.TjQuotaJobClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by janseny
 */
@RequestMapping(ApiVersion.Version1_0 +"/portal/quota")
@RestController
@Api(value = "protalQuota", description = "protalWarn", tags = {"云门户-指标统计"})
public class TjQuotaController extends BaseController {

    @Autowired
    TjQuotaClient tjQuotaClient;
    @Autowired
    TjQuotaJobClient tjQuotaJobClient;
    @Autowired
    UserClient userClient;
    @Autowired
    GetInfoClient getInfoClient;
    @Autowired
    OrganizationClient organizationClient;
    @Autowired
    ObjectMapper objectMapper;


    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaWarn, method = RequestMethod.GET)
    @ApiOperation(value = "获取指标预警信息", notes = "获取指标预警信息")
    public Result getTjQuotaWarn(@RequestParam(value = "userId") String userId){
        try {
//            //获取用户所拥有的
//            List<String> orgList = getInfoClient.getOrgCode(userId);
//            //获取用户所拥有的区域
//            List<String> districtList = getInfoClient.getDistrictByUserId (userId);
            Map<String,String> param = new HashMap<>();
            Collection<MOrganization> organizations = organizationClient.getOrgsByAddress("福建省","厦门市" ,null );
            java.util.Iterator it = organizations.iterator();
            String filters = "";
            while(it.hasNext()){
                MOrganization mOrganization = (MOrganization)it.next();
                filters = filters + mOrganization.getOrgCode() + ",";
            }
            param.put("org",filters);
            //后续过滤距离现在一个月

            List<MTjQuotaWarn> mTjQuotaWarns = tjQuotaClient.getTjQuotaWarn(userId);
            if (mTjQuotaWarns == null) {
                return Result.error("获取失败!");
            }else {
                for(MTjQuotaWarn mTjQuotaWarn : mTjQuotaWarns){
                    MTjQuotaModel mTjQuotaModel= tjQuotaClient.getByCode(mTjQuotaWarn.getQuotaCode());
                    Envelop envelop = tjQuotaJobClient.getQuotaReport(Integer.valueOf(mTjQuotaModel.getId().toString()),objectMapper.writeValueAsString(param));
                    QuotaReportModel quotaReportModels = objectMapper.convertValue(envelop.getObj(),QuotaReportModel.class);
                    List<ReultModel> reultModels = null;
                    int quotaCount = 0;
                    if(quotaReportModels!=null){
                       reultModels = quotaReportModels.getReultModelList();
                       for(ReultModel reult :reultModels){
                           quotaCount = quotaCount +Integer.valueOf(reult.getValue());
                       }
                    }
                    mTjQuotaWarn.setQuotaCount(quotaCount);
                    if(Double.valueOf(mTjQuotaWarn.getValue()) < mTjQuotaWarn.getQuotaCount()){
                        mTjQuotaWarn.setStatus(1);
                    }else {
                        mTjQuotaWarn.setStatus(0);
                    }
                }
            }
            ObjectResult result = new ObjectResult(true,"获取成功！");
            result.setData(mTjQuotaWarns);
            return result;
        }
        catch (Exception ex){
            ex.printStackTrace();
            return Result.error(ex.getMessage());
        }
    }



    @ApiOperation(value = "获取指标单天统计结果")
    @RequestMapping(value = ServiceApi.TJ.GetQuotaReport, method = RequestMethod.GET)
    public Envelop getQuotaReport(
            @ApiParam(name = "id", value = "指标任务ID", required = true)
            @RequestParam(value = "id" , required = true) int id,
            @ApiParam(name = "filters", value = "检索条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters
    ) {
        if(filters !=null && filters.equals("bread")){
            return tjQuotaJobClient.getQuotaBreadReport(id,filters);
        }
        return tjQuotaJobClient.getQuotaReport(id,filters);
    }


}
