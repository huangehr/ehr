package com.yihu.ehr.portal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.tj.QuotaReportModel;
import com.yihu.ehr.agModel.tj.ReultModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.model.tj.MTjQuotaModel;
import com.yihu.ehr.model.tj.MTjQuotaWarn;
import com.yihu.ehr.portal.client.PortalTjQuotaClient;
import com.yihu.ehr.portal.client.PortalTjQuotaJobClient;
import com.yihu.ehr.quota.service.TjQuotaClient;
import com.yihu.ehr.quota.service.TjQuotaJobClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by janseny
 */
@RequestMapping(ApiVersion.Version1_0 + "/portal/quota")
@RestController
@Api(value = "PortalTjQuotaController", description = "指标统计", tags = {"云门户-指标统计"})
public class PortalTjQuotaController extends BaseController {

    @Autowired
    private PortalTjQuotaClient portalTjQuotaClient;
    @Autowired
    private TjQuotaClient tjQuotaClient;
    @Autowired
    private PortalTjQuotaJobClient tjQuotaJobClient;
    @Autowired
    private ObjectMapper objectMapper;


    @RequestMapping(value = ServiceApi.Portal.TjQuotaWarn, method = RequestMethod.GET)
    @ApiOperation(value = "获取指标预警信息", notes = "获取指标预警信息")
    public Result getTjQuotaWarn(@RequestParam(value = "userId") String userId){
        String filters = "";
        try {
            Map<String,String> orgMap = new HashMap<>();
////            //获取用户所拥有的  带saaa权限
//            List<String> orgList = getInfoClient.getOrgCode(userId);
//            if(orgList != null && orgList.size() > 0){
//                for(String orgCode : orgList){
//                    orgMap.put(orgCode,orgCode);
//                }
//            }
////            //获取用户所拥有的区域   带saaa权限
            Map<String,String> param = new HashMap<>();
//            List<String> districtList = getInfoClient.getUserDistrictCode(userId);
//            if(districtList != null && districtList.size() > 0){
//                for(String code : districtList){
//                    MGeographyDict mGeographyDict = geographyDictClient.getAddressDictById(code);
//                    if(mGeographyDict != null){
//                        String province = "";
//                        String city = "";
//                        String district = "";
//                        if(mGeographyDict.getLevel() == 1){
//                            province =  mGeographyDict.getName();
//                        }else if(mGeographyDict.getLevel() == 2){
//                            city =  mGeographyDict.getName();
//                        }else if(mGeographyDict.getLevel() == 3){
//                            district =  mGeographyDict.getName();
//                        }
//                        Collection<MOrganization> organizations = organizationClient.getOrgsByAddress(province,city ,district );
//                        if(organizations !=null ){
//                                java.util.Iterator it = organizations.iterator();
//                                while(it.hasNext()){
//                                    MOrganization mOrganization = (MOrganization)it.next();
//                                    orgMap.put(mOrganization.getCode(),mOrganization.getCode());
//                                }
//                            }
//                    }
//                }
//            }
            param.put("org",filters);

            List<MTjQuotaWarn> mTjQuotaWarns = portalTjQuotaClient.getTjQuotaWarn(userId);
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

}
