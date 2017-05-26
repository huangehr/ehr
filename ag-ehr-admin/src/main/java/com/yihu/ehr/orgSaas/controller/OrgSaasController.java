package com.yihu.ehr.orgSaas.controller;

import com.yihu.ehr.agModel.geogrephy.GeographyDictModel;
import com.yihu.ehr.agModel.geogrephy.GeographyModel;
import com.yihu.ehr.agModel.org.OrgModel;
import com.yihu.ehr.agModel.orgSaas.AreaSaasModel;
import com.yihu.ehr.agModel.orgSaas.OrgSaasModel;
import com.yihu.ehr.agModel.thirdpartystandard.AdapterOrgDetailModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.entity.organizations.OrgSaas;
import com.yihu.ehr.geography.service.AddressClient;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.geography.MGeography;
import com.yihu.ehr.model.geography.MGeographyDict;
import com.yihu.ehr.model.orgSaas.MAreaSaas;
import com.yihu.ehr.orgSaas.service.OrgSaasClient;
import com.yihu.ehr.organization.service.OrganizationClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by zdm on 2017/5/26.
 */
@EnableFeignClients
@RequestMapping(ApiVersion.Version1_0+"/admin")
@RestController
@Api(value = "OrgSaas", description = "机构Saas授权", tags = {"基础信息"})
public class OrgSaasController extends BaseController{
    @Autowired
    private OrgSaasClient orgSaasClient;
    @Autowired
    private OrganizationClient orgClient;
    @Autowired
    private AddressClient addressClient;

    @RequestMapping(value = "/OrgSaasByOrg", method = RequestMethod.GET)
    @ApiOperation(value = "根据机构获取机构相关授权")
    public Envelop getOrgSaasByorgCode(
            @ApiParam(name = "orgCode", value = "机构", defaultValue = "")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @ApiParam(name = "type", value = "类别", defaultValue = "")
            @RequestParam(value = "type", required = false) String type)throws Exception {
        Envelop envelop = new Envelop();
        //根据机构id在asaas表中获取数据
        ListResult listResult =orgSaasClient.getOrgSaasByorgCode(orgCode,type);
        List<Map<String,Object>> saasList = listResult.getDetailModelList();
        Map<String,Object> orgSaasMap=new TreeMap();
        if(null!=saasList&&saasList.size()>0){
            for(int i=0;i<saasList.size();i++){
                Object obj=(Object)saasList.get(i);
                orgSaasMap.put(((ArrayList) obj).get(2).toString(),obj);
            }
        }
            //授权类型 1区域 2机构
        OrgSaasModel osm=null;
        List<OrgSaasModel> OrgSaasModelList=new ArrayList<>();
        if(!"".equals(type)&&type.equals("2")){
            //在机构表中获取所有机构数据
            List<Map<String,Object>> orgList =orgClient.getAllOrgs();
            if(null!=orgList&&orgList.size()>0){
                for(int i=0;i<orgList.size();i++){
                    Map<String,Object> obj=orgList.get(i);
                    osm=new OrgSaasModel();
                    osm.setId(obj.get("id").toString());
                    osm.setOrgCode(orgCode);
                    osm.setType(type);
                    osm.setLevel(obj.get("levelId").toString());
                    if(null!=obj.get("orgCode")){
                        osm.setSaasCode(obj.get("orgCode").toString());
                    }

                    if(null!=obj.get("fullName")){
                        osm.setSaasName(obj.get("fullName").toString());
                    }

                    if(null!=obj.get("parentHosId")){
                        osm.setParent_hos_id(obj.get("parentHosId").toString());
                    }

                    if(null!=obj.get("orgCode")&&null!=orgSaasMap.get(obj.get("orgCode").toString())){
                        osm.setIschecked(true);
                    }else{
                        osm.setIschecked(false);
                    }
                    OrgSaasModelList.add(osm);
                }

            }
            if(OrgSaasModelList.size()>0){
                envelop.setDetailModelList(OrgSaasModelList);
                envelop.setSuccessFlg(true);
            }else {

            }


        }else{
            //查找所有区域
            List<MGeographyDict> mGeographyDictList =new ArrayList<MGeographyDict>();
            List<AreaSaasModel> AreaSaasModelList=new ArrayList<>();
            AreaSaasModel asm=null;
            for(int i=1;i<4;i++ ){
                List<MGeographyDict> newMGeographyDictList = addressClient.getAddressByLevel(i);
                mGeographyDictList.addAll(newMGeographyDictList);
            }
            if(null!=mGeographyDictList&&mGeographyDictList.size()>0){
                for(int i=0;i<mGeographyDictList.size();i++){
                    MGeographyDict obj=mGeographyDictList.get(i);
                    asm=new AreaSaasModel();
                    asm.setId(String.valueOf(obj.getId()));
                    asm.setOrgCode(orgCode);
                    asm.setType(type);
                    asm.setSaasCode(String.valueOf(obj.getId()));
                    asm.setSaasName(obj.getName());
                    asm.setParent_hos_id(String.valueOf(obj.getPid()));
                    asm.setLevel(obj.getLevel());
                    if(null!=orgSaasMap.get(String.valueOf(obj.getId()))){
                        asm.setIschecked(true);
                    }else{
                        asm.setIschecked(false);
                    }
                    AreaSaasModelList.add(asm);
                }
            }
            if(AreaSaasModelList.size()>0){
                envelop.setDetailModelList(AreaSaasModelList);
                envelop.setSuccessFlg(true);
            }else {

            }
        }

        return envelop;
    }



}
