package com.yihu.ehr.adaption.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by Administrator on 2015/8/12.
 */
@RestController
@RequestMapping(ApiVersionPrefix.CommonVersion + "/orgdict")
@Api(protocols = "https", value = "orgdict", description = "机构字典管理接口", tags = {"机构字典"})
public class OrgDictController extends BaseRestController {

//    @Resource(name = Services.OrgDictManager)
//    private XOrgDictManager orgDictManager;
//
//    @Resource(name = Services.OrgDictItemManager)
//    private XOrgDictItemManager orgDictItemManager;
//
//    @Resource(name = Services.OrgManager)
//    private XOrgManager orgManager;


    /**
     * 根据id查询实体
     * @param id
     * @return
     */
    @RequestMapping(value = "/orgDict" , method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询实体")
    public Object getOrgDict(String id) {
//        Result result = new Result();
//        try {
//            OrgDict orgDict  = (OrgDict) orgDictManager.getOrgDict(Long.parseLong(id));
//            OrgDictModel model = new OrgDictModel();
//            model.setCode(orgDict.getCode());
//            model.setName(orgDict.getName());
//            model.setDescription(orgDict.getDescription());
//            result.setObj(model);
//            result.setSuccessFlg(true);
//        }catch (Exception ex){
//            result.setSuccessFlg(false);
//        }
//
//        return result.toJson();
        return null;
    }

    /**
     * 创建机构字典
     * @param apiVersion
     * @param code
     * @param name
     * @param description
     * @param userId
     * @return
     */
    @RequestMapping(value = "/orgDict" , method = RequestMethod.POST)
    @ApiOperation(value = "创建机构字典")
    public Object createOrgDict(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @RequestParam(value = "code") Long code,
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "description", value = "description", defaultValue = "")
            @RequestParam(value = "description") String description,
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId){

//        Result result = new Result();
//        try {
//            boolean isExist = orgDictManager.isExistOrgDict(orgCode,code,name);   //重复校验
//
//            if(isExist){
//                result.setSuccessFlg(false);
//                result.setErrorMsg("该字典已存在！");
//                return  result.toJson();
//            }
//            OrgDict orgDict = new OrgDict();
//            orgDict.setCode(code);
//            orgDict.setName(name);
//            orgDict.setDescription(description);
//            orgDict.setOrganization(orgCode);
//            orgDict.setCreateDate(new Date());
//            orgDict.setCreateUser(user);
//            if(orgDictManager.createOrgDict(orgDict)==null){
//
//                result.setSuccessFlg(false);
//                result.setErrorMsg("创建字典失败！");
//                return  result.toJson();
//            }
//            OrgDictModel model = new OrgDictModel();
//            model.setCode(orgDict.getCode());
//            model.setName(orgDict.getName());
//            model.setDescription(orgDict.getDescription());
//            result.setSuccessFlg(true);
//            result.setObj(model);
//        }catch (Exception ex){
//            result.setSuccessFlg(false);
//        }
//        return  result.toJson();
        return null;
    }

    /**
     * 删除机构字典
     * @param id
     * @return
     */
    @RequestMapping(value = "/orgDict" , method = RequestMethod.DELETE)
    @ApiOperation(value = "删除机构字典")
    public Object deleteOrgDict(long id) {

//        Result result = new Result();
//        try {
//            XOrgDict orgDict = orgDictManager.getOrgDict(id);
//            if(orgDict == null){
//                result.setSuccessFlg(false);
//                result.setErrorMsg("该字典不存在！");
//                return  result.toJson();
//            }
//            orgDictManager.deleteOrgDict(id);
//
//            result.setSuccessFlg(true);
//            return  result.toJson();
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//            result.setErrorMsg("删除字典失败！");
//            return  result.toJson();
//        }
        return null;
    }

    /**
     * 修改机构字典
     * @param apiVersion
     * @param orgCode
     * @param id
     * @param code
     * @param name
     * @param description
     * @param userId
     * @return
     */
    @RequestMapping(value = "/orgDict" , method = RequestMethod.PUT)
    @ApiOperation(value = "修改机构字典")
    public Object updateOrgDict(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "orgCode", value = "orgCode", defaultValue = "")
            @RequestParam(value = "orgCode") String orgCode,
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam(value = "id") String id,
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @RequestParam(value = "code") Long code,
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "description", value = "description", defaultValue = "")
            @RequestParam(value = "description") String description,
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId) {

//        Result result = new Result();
//        try{
//            XOrgDict orgDict = orgDictManager.getOrgDict(id);
//            if(orgDict == null){
//                result.setSuccessFlg(false);
//                result.setErrorMsg("该字典不存在！");
//            }else {
//                //重复校验
//                boolean updateFlg = orgDict.getCode().equals(code) || !orgDictManager.isExistOrgDict(orgCode, code, name);
//                if (updateFlg) {
//                    orgDict.setCode(code);
//                    orgDict.setName(name);
//                    orgDict.setDescription(description);
//                    orgDict.setUpdateDate(new Date());
//                    orgDict.setUpdateUser(user);
//                    orgDictManager.updateOrgDict(orgDict);
//                    result.setSuccessFlg(true);
//                } else {
//                    result.setSuccessFlg(false);
//                    result.setErrorMsg("该字典已存在！");
//                }
//            }
//            return  result.toJson();
//        }catch (Exception e) {
//            result.setSuccessFlg(false);
//            result.setErrorMsg("修改字典失败！");
//            return result.toJson();
//        }
        return null;
    }


    /**
     * 条件查询
     * @param codename
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping(value = "/orgDicts" , method = RequestMethod.GET)
    @ApiOperation(value = "条件查询")
    public Object searchOrgDicts(String orgCode,String codename,int page, int rows) {
//        Result result=new Result();
//        try {
//            Map<String, Object> conditionMap = new HashMap<>();
//            conditionMap.put("orgCode", orgCode);
//            conditionMap.put("code", codename);
//            conditionMap.put("page", page);
//            conditionMap.put("rows", rows);
//            List<OrgDictModel> detailModelList = orgDictManager.searchOrgDicts(conditionMap);
//            Integer totalCount = orgDictManager.searchTotalCount(conditionMap);
//            result = getResult(detailModelList, totalCount, page, rows);
//            result.setSuccessFlg(true);
//        }catch (Exception ex){
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
        return null;
    }

    //---------------------------以上是机构字典部分，以下是机构字典详情部分---------------------------

    /**
     * 根据id查询实体
     * @param id
     * @return
     */
    @RequestMapping(value = "/orgDictItem" , method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询实体")
    public Object getOrgDictItem(String id) {
//        Result result = new Result();
//        try {
//            OrgDictItem orgDictItem  = (OrgDictItem) orgDictItemManager.getOrgDictItem(Long.parseLong(id));
//            OrgDictItemModel model = new OrgDictItemModel();
//            model.setCode(orgDictItem.getCode());
//            model.setName(orgDictItem.getName());
//            model.setSort(Integer.toString(orgDictItem.getSort()));
//            model.setDescription(orgDictItem.getDescription());
//            result.setObj(model);
//            result.setSuccessFlg(true);
//        }catch (Exception ex){
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
        return null;
    }


    /**
     * 创建机构字典数据
     * @param apiVersion
     * @param orgDictSeq
     * @param orgCode
     * @param code
     * @param name
     * @param description
     * @param sort
     * @param userId
     * @return
     */
    @RequestMapping(value = "/orgDictItem" , method = RequestMethod.POST)
    @ApiOperation(value = "创建机构字典数据")
    public Object createOrgDictItem(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "orgDictSeq", value = "orgDictSeq", defaultValue = "")
            @RequestParam(value = "orgDictSeq") Integer orgDictSeq,
            @ApiParam(name = "orgCode", value = "orgCode", defaultValue = "")
            @RequestParam(value = "orgCode") String orgCode,
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @RequestParam(value = "code") Long code,
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "description", value = "description", defaultValue = "")
            @RequestParam(value = "description") String description,
            @ApiParam(name = "sort", value = "sort", defaultValue = "")
            @RequestParam(value = "sort") String sort,
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId){

//        Result result = new Result();
//        try {
//            boolean isExist = orgDictItemManager.isExistOrgDictItem(orgDictSeq,orgCode,code, name);   //重复校验
//
//            if(isExist){
//                result.setSuccessFlg(false);
//                result.setErrorMsg("该字典项已存在！");
//                return  result.toJson();
//            }
//            OrgDictItem orgDictItem = new OrgDictItem();
//            int nextSort;
//            if(StringUtil.isEmpty(sort)){
//                nextSort = orgDictItemManager.getNextSort(orgDictSeq);
//            }else{
//                nextSort = Integer.parseInt(sort);
//            }
//            orgDictItem.setCode(code);
//            orgDictItem.setName(name);
//            orgDictItem.setSort(nextSort);
//            orgDictItem.setOrgDict(orgDictSeq);
//            orgDictItem.setCreateDate(new Date());
//            orgDictItem.setCreateUser(user);
//            orgDictItem.setDescription(description);
//            orgDictItem.setOrganization(orgCode);
//            if(orgDictItemManager.createOrgDictItem(orgDictItem)==null){
//                result.setSuccessFlg(false);
//                result.setErrorMsg("创建字典项失败！");
//                return  result.toJson();
//            }
//            OrgDictItemModel model = new OrgDictItemModel();
//            model.setCode(orgDictItem.getCode());
//            model.setName(orgDictItem.getName());
//            model.setDescription(orgDictItem.getDescription());
//            result.setSuccessFlg(true);
//            result.setObj(model);
//        }catch (Exception ex){
//            result.setSuccessFlg(false);
//        }
//
//        return  result.toJson();
        return null;
    }

    /**
     * 删除机构字典数据
     * @param id
     * @return
     */
    @RequestMapping(value = "/orgDictItem" , method = RequestMethod.DELETE)
    @ApiOperation(value = "删除机构字典数据")
    public Object deleteOrgDictItem(long id) {

//        Result result = new Result();
//        try {
//            XOrgDictItem orgDictItem = orgDictItemManager.getOrgDictItem(id);
//            if(orgDictItem == null){
//                result.setSuccessFlg(false);
//                result.setErrorMsg("该字典项不存在！");
//                return  result.toJson();
//            }
//            orgDictItemManager.deleteOrgDictItem(id);
//            result.setSuccessFlg(true);
//            return  result.toJson();
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//            result.setErrorMsg("删除字典项失败！");
//            return  result.toJson();
//        }
        return null;
    }

    /**
     * 批量删除机构字典数据
     * @param ids
     * @return
     */
    @RequestMapping(value = "/orgDictItemList" , method = RequestMethod.DELETE)
    @ApiOperation(value = "批量删除机构字典数据")
    public Object deleteOrgDictItemList(@RequestParam("ids[]") Long[] ids) {
//        Result result = new Result();
//
//        if(StringUtil.isEmpty(ids)){
//            result.setSuccessFlg(false);
//            return result.toJson();
//        }else {
//            try {
//                orgDictItemManager.deleteOrgDictItemList(ids);
//            } catch (Exception e) {
//                result.setSuccessFlg(false);
//                result.setErrorMsg("删除字典项失败！");
//                return result.toJson();
//            }
//            result.setSuccessFlg(true);
//
//            return result.toJson();
//        }
        return null;
    }

    /**
     * 修改机构字典数据
     * @param apiVersion
     * @param id
     * @param orgDictSeq
     * @param orgCode
     * @param code
     * @param name
     * @param description
     * @param sort
     * @param userId
     * @return
     */
    @RequestMapping(value = "/dictItem" , method = RequestMethod.PUT)
    @ApiOperation(value = "修改机构字典数据")
    public Object updateDictItem(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam(value = "id") Long id,
            @ApiParam(name = "orgDictSeq", value = "orgDictSeq", defaultValue = "")
            @RequestParam(value = "orgDictSeq") Integer orgDictSeq,
            @ApiParam(name = "orgCode", value = "orgCode", defaultValue = "")
            @RequestParam(value = "orgCode") String orgCode,
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @RequestParam(value = "code") Long code,
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "description", value = "description", defaultValue = "")
            @RequestParam(value = "description") String description,
            @ApiParam(name = "sort", value = "sort", defaultValue = "")
            @RequestParam(value = "sort") String sort,
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId) {

//        Result result = new Result();
//        try{
//            XOrgDictItem orgDictItem = orgDictItemManager.getOrgDictItem(id);
//            if(orgDictItem == null){
//                result.setSuccessFlg(false);
//                result.setErrorMsg("该字典项不存在！");
//            }else {
//                //重复校验
//                boolean updateFlg = orgDictItem.getCode().equals(code) || !orgDictItemManager.isExistOrgDictItem(orgDictSeq, orgCode, code, name);
//                if (updateFlg) {
//                    orgDictItem.setCode(code);
//                    orgDictItem.setName(name);
//                    orgDictItem.setDescription(description);
//                    orgDictItem.setUpdateDate(new Date());
//                    orgDictItem.setUpdateUser(user);
//                    orgDictItem.setSort(Integer.parseInt(sort));
//                    orgDictItem.setOrganization(orgCode);
//                    orgDictItemManager.updateOrgDictItem(orgDictItem);
//                    result.setSuccessFlg(true);
//                } else {
//                    result.setSuccessFlg(false);
//                    result.setErrorMsg("该字典项已存在！");
//                }
//            }
//            return  result.toJson();
//        }catch (Exception e) {
//            result.setSuccessFlg(false);
//            result.setErrorMsg("修改字典项失败！");
//            return result.toJson();
//        }
        return null;
    }


    /**
     * 条件查询
     * @param codename
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping(value = "/orgDictItems" , method = RequestMethod.GET)
    @ApiOperation(value = "条件查询")
    public Object searchOrgDictItems(Integer orgDictSeq,String orgCode,String codename,int page, int rows) {
//        Result result=new Result();
//        try {
//            Map<String, Object> conditionMap = new HashMap<>();
//            conditionMap.put("orgDictSeq", orgDictSeq);
//            conditionMap.put("orgCode", orgCode);
//            conditionMap.put("code", codename);
//            conditionMap.put("page", page);
//            conditionMap.put("rows", rows);
//            List<OrgDictItemModel> detailModelList = orgDictItemManager.searchOrgDictItems(conditionMap);
//            Integer totalCount = orgDictItemManager.searchTotalCount(conditionMap);
//            result = getResult(detailModelList, totalCount, page, rows);
//            result.setSuccessFlg(true);
//        }catch (Exception ex){
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
        return null;
    }


    //获取机构列表
    @RequestMapping(value = "/organizationList" , method = RequestMethod.GET)
    @ApiOperation(value = "获取机构列表")
    public Object getOrganizationList(){
//        Result result = new Result();
//        try {
//            List<XOrganization> organizations = orgDictItemManager.getOrganizationList();
//            List<String> orgCodeName =new ArrayList<>();
//            for (XOrganization organization : organizations) {
//                orgCodeName.add(organization.getOrgCode()+','+organization.getFullName());
//            }
//            result.setObj(orgCodeName);
//            result.setSuccessFlg(true);
//        }catch (Exception ex){
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
        return null;
    }


}
