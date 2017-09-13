package com.yihu.ehr.saas.controller;

import com.yihu.ehr.agModel.orgSaas.AreaSaasModel;
import com.yihu.ehr.agModel.orgSaas.OrgSaasModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.geography.service.AddressClient;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.geography.MGeographyDict;
import com.yihu.ehr.redis.client.RedisUpdateClient;
import com.yihu.ehr.saas.service.OrgSaasClient;
import com.yihu.ehr.organization.service.OrganizationClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
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
    @Autowired
    private RedisUpdateClient redisUpdateClient;

    @RequestMapping(value = "/OrgSaasByOrg", method = RequestMethod.GET)
    @ApiOperation(value = "根据机构获取机构相关授权")
    public Envelop getOrgSaasByorgCode(
            @ApiParam(name = "orgCode", value = "机构", defaultValue = "")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @ApiParam(name = "type", value = "类别", defaultValue = "")
            @RequestParam(value = "type", required = false) String type,
            @ApiParam(name = "saasName", value = "名称", defaultValue = "")
            @RequestParam(value = "saasName", required = false) String saasName)throws Exception {
        Envelop envelop = new Envelop();
        //根据机构id在asaas表中获取数据
        ListResult listResult =orgSaasClient.getOrgSaasByorgCode(orgCode,type);
        List<Map<String,Object>> saasList = listResult.getDetailModelList();
        Map<String,Object> orgSaasMap=new TreeMap();
        if(null!=saasList&&saasList.size()>0){
            for(int i=0;i<saasList.size();i++){
                Object obj=(Object)saasList.get(i);
                if(null!=((ArrayList) obj).get(3)){
                    orgSaasMap.put(((ArrayList) obj).get(3).toString(),obj);
                }
            }
        }
            //授权类型 1区域 2机构
        OrgSaasModel osm=null;
        List<OrgSaasModel> OrgSaasModelList=new ArrayList<>();
        List<OrgSaasModel> OrgSaasModelRightList=new ArrayList<>();
        if(!"".equals(type)&&type.equals("2")){
            List<Map<String,Object>> orgList =new ArrayList<>();
            //在机构表中获取所有机构数据
            if(null!=saasName&&!"".equals(saasName)){
                orgList =orgClient.getAllSaasOrgs(saasName);
            }else{
                orgList =orgClient.getAllOrgs();
            }

            if(null!=orgList&&orgList.size()>0){
                for(int i=0;i<orgList.size();i++){
                    Map<String,Object> obj=orgList.get(i);
                    osm=new OrgSaasModel();
                    if(null!=obj.get("id")){
                        osm.setId(obj.get("id").toString());
                    }
                    osm.setOrgCode(orgCode);
                    osm.setType(type);
                    if(null!=obj.get("levelId")){
                        osm.setLevel(obj.get("levelId").toString());
                    }

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
                        OrgSaasModelRightList.add(osm);
                    }else{
                        osm.setIschecked(false);
                    }
                    OrgSaasModelList.add(osm);
                }

            }
            if(OrgSaasModelList.size()>0){
                envelop.setDetailModelList(OrgSaasModelList);
                envelop.setObj(OrgSaasModelRightList);
                envelop.setSuccessFlg(true);
            }else {

            }


        }else{
            //查找所有区域
            List<MGeographyDict> mGeographyDictList =new ArrayList<MGeographyDict>();
            List<AreaSaasModel> AreaSaasModelList=new ArrayList<>();
            List<AreaSaasModel> AreaSaasModelRightList=new ArrayList<>();
            AreaSaasModel asm=null;
            List<MGeographyDict> newMGeographyDictList =new ArrayList<>();
            if(null!=saasName&&!"".equals(saasName)){
                newMGeographyDictList=addressClient.getOrgSaasAreaByname(saasName);
                mGeographyDictList.addAll(newMGeographyDictList);
            }else{
                for(int i=1;i<4;i++ ){
                    newMGeographyDictList = addressClient.getAddressByLevel(i);
                    mGeographyDictList.addAll(newMGeographyDictList);
                }
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
                        AreaSaasModelRightList.add(asm);
                    }else{
                        asm.setIschecked(false);
                    }
                    AreaSaasModelList.add(asm);
                }
            }
            if(AreaSaasModelList.size()>0){
                envelop.setDetailModelList(AreaSaasModelList);
                envelop.setObj(AreaSaasModelRightList);
                envelop.setSuccessFlg(true);
            }else {

            }
        }

        return envelop;

    }
    /**
     * 机构授权检查并保存
     * @return
     */
    @RequestMapping(value = "/orgSaasSave", method = RequestMethod.POST)
    @ApiOperation(value = "机构授权检查,如果被授权的机构或者区域在指定机构总不存在，这新增这条记录，否则返回地址id")
    public Envelop saveOrgSaas(
            @ApiParam(name = "orgCode", value = "机构", defaultValue = "")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @ApiParam(name = "type", value = "类别", defaultValue = "")
            @RequestParam(value = "type", required = false) String type,
            @ApiParam(name = "jsonData", value = "json数据", defaultValue = "")
            @RequestBody String jsonData) throws Exception{
        Envelop envelop = new Envelop();
        OrgSaasModel orgSaasModel = new OrgSaasModel();
        jsonData=URLDecoder.decode(jsonData);
        String[] newJsonData=jsonData.split("&");
        boolean succe = orgSaasClient.saveOrgSaas(orgCode,type,newJsonData[0]);
        if(succe){
            envelop.setSuccessFlg(true);
            //更新相关缓存数据（原则上如果更新失败不能影响实际更新结果）
            try {
                if (type.equals("1")) {
                    redisUpdateClient.updateOrgSaasArea(orgCode);
                } else {
                    redisUpdateClient.updateOrgSaasOrg(orgCode);
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
            //orgSaasModel.setId(id);
        }else{
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("机构授权新增失败");
        }
        return envelop;
    }







}
