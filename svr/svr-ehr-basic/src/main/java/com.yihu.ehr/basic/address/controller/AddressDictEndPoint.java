package com.yihu.ehr.basic.address.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.address.AddressDict;
import com.yihu.ehr.basic.address.service.AddressDictService;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.geography.MGeographyDict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "Geography-Dict", description = "行政区划地址", tags = {"基础信息-行政区划地址"})
public class AddressDictEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private AddressDictService geographyDictService;

    /**
     * 根据地址等级查询地址信息
     * @param level
     * @return
     */
    @RequestMapping(value = ServiceApi.Geography.AddressDictByLevel, method = RequestMethod.GET)
    @ApiOperation(value = "根据等级查询行政区划地址")
    public Collection<MGeographyDict> getAddressByLevel(
            @ApiParam(name = "level", value = "等级", defaultValue = "")
            @PathVariable(value = "level") Integer level) {
        List<AddressDict> addressDictList = geographyDictService.getLevelToAddr(level);
        return convertToModels(addressDictList,new ArrayList<>(addressDictList.size()), MGeographyDict.class,"");
    }

    @RequestMapping(value = ServiceApi.Geography.AddressDictByPid, method = RequestMethod.GET)
    @ApiOperation(value = "根据上级编号查询行政区划地址")
    public Collection<MGeographyDict> getAddressDictByPid(
        @ApiParam(name = "pid", value = "上级id", defaultValue = "")
        @PathVariable(value = "pid") Integer pid) {
        List<AddressDict> addressDictList = geographyDictService.getPidToAddr(pid);
        return convertToModels(addressDictList, new ArrayList<>(addressDictList.size()), MGeographyDict.class,"");
    }


    @RequestMapping(value = ServiceApi.Geography.AddressDict, method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询行政区划地址")
    public MGeographyDict getAddressDictById(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) {
        AddressDict geographyDict =  geographyDictService.findById(id);
        return convertToModel(geographyDict, MGeographyDict.class);
    }

    @RequestMapping(value = ServiceApi.Geography.AddressDictList, method = RequestMethod.POST)
    @ApiOperation(value = "获取多条行政区划地址")
    public List<MGeographyDict> getAddressDictByIdList(
            @ApiParam(name = "idList", value = "idList", defaultValue = "")
            @RequestParam(value = "idList") List<String> idList) {
        List<MGeographyDict> list=new ArrayList<>();
        for(int i=0;i<idList.size();i++) {
            AddressDict geographyDict = geographyDictService.findById(idList.get(i));
            list.add(convertToModel(geographyDict, MGeographyDict.class));
        }
        return list;
    }

    @RequestMapping(value = ServiceApi.Geography.AddressDictAll, method = RequestMethod.POST)
    @ApiOperation(value = "获取全部行政区划地址")
    public List<AddressDict> getAllAddressDict(){
       return geographyDictService.getAllAddressDict();
    }



    @RequestMapping(value = ServiceApi.Geography.AddressDictByFields, method = RequestMethod.GET)
    @ApiOperation(value = "根据地址中文名 查询地址编号")
    Collection<MGeographyDict> getAddressDict(
            @ApiParam(name = "fields", value = "fields", defaultValue = "")
            @RequestParam(value = "fields") String[] fields ,
            @ApiParam(name = "values", value = "values", defaultValue = "")
            @RequestParam(value = "values") String[] values){
        List<AddressDict> geographyDictList = geographyDictService.findByFields(fields,values);
        return convertToModels(geographyDictList,new ArrayList<>(geographyDictList.size()), MGeographyDict.class,"");
    }

    @RequestMapping(value = ServiceApi.Geography.GetAddressNameByCode, method = RequestMethod.GET)
    @ApiOperation(value = "根据地址中文名 查询地址编号")
    ObjectResult getAddressNameByCode(
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @RequestParam(value = "name") String name){
        AddressDict geographyDict = geographyDictService.findByName(name);
        if(geographyDict != null){
            ObjectResult objectResult = new ObjectResult();
            objectResult.setData(geographyDict);
            objectResult.setSuccessFlg(true);
            return objectResult;
        }
        return null;
    }
    /**
     * 根据地址等级查询地址信息
     * @param name
     * @return
     */
    @RequestMapping(value = "/OrgSaasAreaByname", method = RequestMethod.GET)
    @ApiOperation(value = "根据名称查询行政区划地址")
    public Collection<MGeographyDict> getOrgSaasAreaByname(
            @ApiParam(name = "name", value = "名称", defaultValue = "")
            @RequestParam(value = "name") String name) {
        List<AddressDict> addressDictList = geographyDictService.getAddrDictByname(name);
        return convertToModels(addressDictList,new ArrayList<>(addressDictList.size()), MGeographyDict.class,"");
    }
}
