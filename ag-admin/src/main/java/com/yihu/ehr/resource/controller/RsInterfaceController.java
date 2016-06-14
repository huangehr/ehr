package com.yihu.ehr.resource.controller;

import com.yihu.ehr.agModel.resource.RsInterfaceModel;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.resource.MRsInterface;
import com.yihu.ehr.resource.client.RsInterfaceClient;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author linaz
 * @created 2016.05.23 17:11
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(value = "interfaces", description = "资源服务接口")
public class RsInterfaceController extends BaseController {
    @Autowired
    private RsInterfaceClient rsInterfaceClient;

    @RequestMapping(value = ServiceApi.Resources.Interfaces, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件获取资源列表", notes = "根据查询条件获取资源列表")
    public Envelop searchRsInterfaces(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size) throws Exception {
        Envelop envelop = new Envelop();
        try {
            ResponseEntity<List<MRsInterface>> responseEntity = rsInterfaceClient.searchRsInterfaces(fields,filters,sorts,page,size);
            List<MRsInterface> rsInterfaces = responseEntity.getBody();
            envelop = getResult(rsInterfaces, getTotalCount(responseEntity), page, size);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }


    @RequestMapping(value = ServiceApi.Resources.Interfaces, method = RequestMethod.POST)
    @ApiOperation(value = "创建资源", notes = "创建资源")
    public Envelop createRsInterface(
            @ApiParam(name = "json_data", value = "", defaultValue = "")
            @RequestParam(value = "json_data") String jsonData) throws Exception {
        Envelop envelop = new Envelop();
        try{
            MRsInterface rsInterface = rsInterfaceClient.createRsInterface(jsonData);
            envelop.setObj(rsInterface);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.Interfaces, method = RequestMethod.PUT)
    @ApiOperation(value = "修改资源", notes = "修改资源")
    public Envelop updateRsInterface(
            @ApiParam(name = "json_data", value = "")
            @RequestParam(value = "json_data") String jsonData) throws Exception {
        Envelop envelop = new Envelop();
        try{
            MRsInterface rsInterface = rsInterfaceClient.updateRsInterface(jsonData);
            envelop.setObj(rsInterface);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.InterfaceById, method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取获取标准字典")
    public Envelop getRsInterfaceById(
            @ApiParam(name = "id", value = "", defaultValue = "")
            @PathVariable(value = "id") String id) {
        Envelop envelop = new Envelop();
        try{
            MRsInterface mRsInterface = rsInterfaceClient.getRsInterfaceById(id);
            RsInterfaceModel rsInterface = convertToModel(mRsInterface,RsInterfaceModel.class);
            if(rsInterface ==null){
                envelop.setSuccessFlg(false);
            }else {
                envelop.setObj(rsInterface);
                envelop.setSuccessFlg(true);
            }
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.Interface, method = RequestMethod.GET)
    @ApiOperation(value = "根据资源接口编码resourceInterface获取资源接口")
    public Envelop findByResourceInterface(
            @ApiParam(name = "resource_interface", value = "", defaultValue = "")
            @RequestParam(value = "resource_interface") String resourceInterface) {
        Envelop envelop = new Envelop();
        try{
            MRsInterface mRsInterface = rsInterfaceClient.findByResourceInterface(resourceInterface);
            RsInterfaceModel rsInterface = convertToModel(mRsInterface,RsInterfaceModel.class);
            if(rsInterface ==null){
                envelop.setSuccessFlg(false);
            }else {
                envelop.setObj(rsInterface);
                envelop.setSuccessFlg(true);
            }
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.InterfaceNameExistence, method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取获取标准字典")
    public Envelop isExistenceName(
            @ApiParam(name = "name", value = "", defaultValue = "")
            @RequestParam(value = "name") String name) {
        Envelop envelop = new Envelop();
        try{
            boolean bo = rsInterfaceClient.isExistenceName(name);
            if(bo){
                envelop.setSuccessFlg(true);
            }else {
                envelop.setSuccessFlg(false);
            }
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.InterfaceById, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除资源", notes = "删除资源")
    public Envelop deleteRsInterface(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        Envelop envelop = new Envelop();
        try{
            rsInterfaceClient.deleteRsInterface(id);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }
}