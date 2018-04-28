package com.yihu.ehr.basic.org.controller;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.basic.address.service.AddressDictService;
import com.yihu.ehr.basic.org.model.Organization;
import com.yihu.ehr.basic.org.service.OrgSaasService;
import com.yihu.ehr.basic.org.service.OrgService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.address.AddressDict;
import com.yihu.ehr.entity.organizations.OrgSaas;
import com.yihu.ehr.model.common.ListResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "OrgSaas", description = "机构saas", tags = {"机构saas"})
public class OrgSaasEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private OrgSaasService orgSaasService;
    @Autowired
    private AddressDictService addressDictService;
    @Autowired
    private OrgService orgService;

    /**
     * 根据机构获取saas机构数据
     * @param orgCode
     * @return
     */
    @RequestMapping(value = "/OrgSaasByOrg", method = RequestMethod.GET)
    @ApiOperation(value = "根据机构获取saas机构数据")
    public ListResult getOrgSaasByorgCode(
            @ApiParam(name = "orgCode", value = "机构", defaultValue = "")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @ApiParam(name = "type", value = "类别", defaultValue = "")
            @RequestParam(value = "type", required = false) String type,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception  {
        ListResult result = new ListResult();
        List<Object> OrgSaasList = orgSaasService.getOrgSaasByorgCode(orgCode,type);
        result.setDetailModelList(OrgSaasList);
        result.setSuccessFlg(true);
        result.setCode(200);
        return result;
    }

    /**
     * 机构授权检查并保存
     * @return
     */
    @RequestMapping(value = "/orgSaasSave", method = RequestMethod.POST)
    @ApiOperation(value = "机构授权检查,如果被授权的机构或者区域在指定机构总不存在，这新增这条记录，否则返回地址id")
    public boolean saveOrgSaas(
            @ApiParam(name = "orgCode", value = "机构", defaultValue = "")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @ApiParam(name = "type", value = "类别", defaultValue = "")
            @RequestParam(value = "type", required = false) String type,
            @ApiParam(name = "jsonData", value = "json数据", defaultValue = "")
            @RequestBody  String jsonData) throws Exception{
        //根据机构code和type值删除既存数据
     //   orgSaasService.deleteOrgSaas(orgCode,type);
        ObjectMapper objectMapper = new ObjectMapper();
        String[] jsonDatalist=jsonData.split("jsonData=");
        //将json串转换成对象，放进list里面
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, OrgSaas.class);
       List<OrgSaas> models = objectMapper.readValue(jsonDatalist[1],javaType);
        String addressId ="";
        boolean saveFlag=false;
        addressId=orgSaasService.saveOrgSaas(models,orgCode,type);
        if((models.size()>0&&addressId!=null)||!(models.size()>0)){
            saveFlag=true;
        }
        return saveFlag;

    }

    /**
     * 根据用户的机构id，获取Saas化的机构或者区域id
     * @param userOrgCode
     * @return
     */
    @RequestMapping(value = ServiceApi.Org.getUserOrgSaasByUserOrgCode, method = RequestMethod.GET)
    @ApiOperation(value = "根据用户的机构id，获取Saas化的机构或者区域id")
    public List<String> getUserOrgSaasByUserOrgCode(
            @ApiParam(name = "userOrgCode", value = "用户所在机构")
            @RequestParam(value = "userOrgCode", required = false)  List<String> userOrgCode,
            @ApiParam(name = "type", value = "saas类型")
            @RequestParam(value = "type", required = false) String type) throws Exception  {
        List<String> OrgSaasList = orgSaasService.getOrgSaasCodeByorgCode(userOrgCode, type);
        return OrgSaasList;
    }

    // ----------------------------- 用户登陆根据地区获取层级机构权限 ------------------------------------
    @RequestMapping(value = ServiceApi.Org.childOrgSaasByAreaCode, method = RequestMethod.POST)
    @ApiOperation(value = "根据地区获取层级机构权限")
    public List<String> childOrgSaasByAreaCode (
            @ApiParam(name = "area", value = "地区列表", required = true)
            @RequestParam(value = "area") String area) throws Exception  {
        String [] areaArr = area.split(",");
        List<String> orgSaas = new ArrayList<>();
        for (String _area : areaArr) {
            AddressDict addressDict = addressDictService.findById(_area);
            String province = "";
            String city = "";
            String district = "";
            if (addressDict.getLevel() == 1){
                province =  addressDict.getName();
            } else if (addressDict.getLevel() == 2){
                city =  addressDict.getName();
            } else if (addressDict.getLevel() == 3){
                district =  addressDict.getName();
            }
            List<Organization> orgList = orgService.searchByAddress(province, city, district);
            orgList.forEach(item -> {
                if (!orgSaas.contains(item)) {
                    orgSaas.add(item.getOrgCode());
                }
            });
        }
        return orgSaas;
    }

}
