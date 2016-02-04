package com.yihu.ehr.adaption.orgdataset.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Administrator on 2015/8/12.
 */
@RestController
@RequestMapping(ApiVersionPrefix.CommonVersion + "/orgdataset")
@Api(protocols = "https", value = "orgdataset", description = "机构数据集管理接口", tags = {"机构数据集"})

public class OrgDataSetController extends BaseRestController {

    @Autowired
    private OrgDataSetManager orgDataSetManager;

//    @Resource(name = Services.OrgMetaDataManager)
//    private XOrgMetaDataManager orgMetaDataManager;


    /**
     * 根据id查询实体
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/orgDataSet" , method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询实体")
    public Object getOrgDataSet(String id) {
//        Envelop result = new Envelop();
//        try{
//            OrgDataSet orgDataSet = (OrgDataSet) orgDataSetManager.getOrgDataSet(Long.parseLong(id));
//            OrgDataSetModel model = new OrgDataSetModel();
//            model.setCode(orgDataSet.getCode());
//            model.setName(orgDataSet.getName());
//            model.setDescription(orgDataSet.getDescription());
//            result.setObj(model);
//            result.setSuccessFlg(true);
//        }catch (Exception es){
//            result.setSuccessFlg(false);
//        }
//
//        return result.toJson();
        return null;
    }

    /**
     * 创建机构数据集
     * @param apiVersion
     * @param code
     * @param name
     * @param description
     * @param orgCode
     * @param userId
     * @return
     */
    @RequestMapping(value = "/orgDataSet" , method = RequestMethod.POST)
    @ApiOperation(value = "创建机构数据集")
    public Object createOrgDataSet(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @RequestParam(value = "code") Long code,
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "description", value = "description", defaultValue = "")
            @RequestParam(value = "description") String description,
            @ApiParam(name = "orgCode", value = "orgCode", defaultValue = "")
            @RequestParam(value = "orgCode") String orgCode,
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId) {

//        Envelop result = new Envelop();
//
//        try {
//            boolean isExist = orgDataSetManager.isExistOrgDataSet(orgCode, code, name);   //重复校验
//            if (isExist) {
//                result.setSuccessFlg(false);
//                result.setErrorMsg("该数据集已存在！");
//                return result.toJson();
//            }
//            OrgDataSet orgDataSet = new OrgDataSet();
//            orgDataSet.setCode(code);
//            orgDataSet.setName(name);
//            orgDataSet.setDescription(description);
//            orgDataSet.setOrganization(orgCode);
//            orgDataSet.setCreateDate(new Date());
//            orgDataSet.setCreateUser(user);
//
//            if (orgDataSetManager.createOrgDataSet(orgDataSet) == null) {
//                result.setSuccessFlg(false);
//                result.setErrorMsg("创建数据集失败！");
//                return result.toJson();
//            }
//
//            OrgDataSetModel model = new OrgDataSetModel();
//            model.setCode(orgDataSet.getCode());
//            model.setName(orgDataSet.getName());
//            model.setDescription(orgDataSet.getDescription());
//            result.setSuccessFlg(true);
//            result.setObj(model);
//        }catch (Exception ex){
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
        return null;
    }

    /**
     * 删除机构数据集
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/orgDataSet" , method = RequestMethod.DELETE)
    @ApiOperation(value = "删除机构数据集")
    public Object deleteOrgDataSet(long id) {

//        Envelop result = new Envelop();
//        XOrgDataSet orgDataSet = orgDataSetManager.getOrgDataSet(id);
//
//        if (orgDataSet == null) {
//            result.setSuccessFlg(false);
//            result.setErrorMsg("该数据集不存在！");
//            return result.toJson();
//        }
//        try {
//            orgDataSetManager.deleteOrgDataSet(id);
//            result.setSuccessFlg(true);
//            return result.toJson();
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//            result.setErrorMsg("删除数据集失败！");
//            return result.toJson();
//        }
        return null;
    }

    /**
     * 修改机构数据集
     * @param apiVersion
     * @param orgCode
     * @param id
     * @param code
     * @param name
     * @param description
     * @param userId
     * @return
     */
    @RequestMapping(value = "/orgDataSet" , method = RequestMethod.PUT)
    @ApiOperation(value = "修改机构数据集")
    public Object updateOrgDataSet(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "orgCode", value = "orgCode", defaultValue = "")
            @RequestParam(value = "orgCode") Long orgCode,
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam(value = "id") Long id,
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @RequestParam(value = "code") Long code,
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "description", value = "description", defaultValue = "")
            @RequestParam(value = "description") String description,
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId) {

//        Envelop result = new Envelop();
//        try{
//            XOrgDataSet orgDataSet = orgDataSetManager.getOrgDataSet(id);
//            if(orgDataSet == null){
//                result.setSuccessFlg(false);
//                result.setErrorMsg("该数据集不存在！");
//            }else {
//                //重复校验
//                boolean updateFlg = orgDataSet.getCode().equals(code) || !orgDataSetManager.isExistOrgDataSet(orgCode, code, name);
//                if (updateFlg) {
//                    orgDataSet.setCode(code);
//                    orgDataSet.setName(name);
//                    orgDataSet.setDescription(description);
//                    orgDataSet.setUpdateDate(new Date());
//                    orgDataSet.setUpdateUser(user);
//                    orgDataSetManager.updateOrgDataSet(orgDataSet);
//                    result.setSuccessFlg(true);
//                } else {
//                    result.setSuccessFlg(false);
//                    result.setErrorMsg("该数据集已存在！");
//                }
//            }
//            return  result.toJson();
//        }catch (Exception e) {
//            result.setSuccessFlg(false);
//            result.setErrorMsg("修改数据集失败！");
//            return result.toJson();
//        }
        return null;
    }


    /**
     * 条件查询
     *
     * @param codename
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping(value = "/orgDataSets" , method = RequestMethod.GET)
    @ApiOperation(value = "条件查询")
    public Object searchOrgDataSets(String orgCode, String codename, int page, int rows) {
//        Envelop result = new Envelop();
//        try {
//            Map<String, Object> conditionMap = new HashMap<>();
//            conditionMap.put("orgCode", orgCode);
//            conditionMap.put("code", codename);
//            conditionMap.put("page", page);
//            conditionMap.put("rows", rows);
//            List<OrgDataSetModel> orgDataSetModel = orgDataSetManager.searchOrgDataSets(conditionMap);
//            Integer totalCount = orgDataSetManager.searchTotalCount(conditionMap);
//            result = getResult(orgDataSetModel, totalCount, page, rows);
//            result.setSuccessFlg(true);
//        }catch (Exception ex){
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
        return null;
    }


    //---------------------------以上是机构数据集部分，以下是机构数据元详情部分---------------------------


    /**
     * 根据id查询实体
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/orgMetaData" , method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询实体")
    public Object getOrgMetaData(String id) {
//        Envelop result = new Envelop();
//        try{
//            OrgMetaData orgMetaData = (OrgMetaData) orgMetaDataManager.getOrgMetaData(Long.parseLong(id));
//            OrgMetaDataModel model = new OrgMetaDataModel();
//            model.setCode(orgMetaData.getCode());
//            model.setName(orgMetaData.getName());
//            model.setDescription(orgMetaData.getDescription());
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
     * 创建机构数据元
     *
     * @param orgDataSetSeq
     * @param code
     * @param name
     * @param description
     * @param user
     * @return
     */
    @RequestMapping(value = "/orgMetaData" , method = RequestMethod.POST)
    @ApiOperation(value = "创建机构数据元")
    public Object createOrgMetaData(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "orgDataSetSeq", value = "orgDataSetSeq", defaultValue = "")
            @RequestParam(value = "orgDataSetSeq") Integer orgDataSetSeq,
            @ApiParam(name = "orgCode", value = "orgCode", defaultValue = "")
            @RequestParam(value = "orgCode") Long orgCode,
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam(value = "id") Long id,
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @RequestParam(value = "code") Long code,
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "description", value = "description", defaultValue = "")
            @RequestParam(value = "description") String description,
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId) {

//        Envelop result = new Envelop();
//        try {
//            boolean isExist = orgMetaDataManager.isExistOrgMetaData(orgDataSetSeq,orgCode, code, name);   //重复校验
//
//            if (isExist) {
//                result.setSuccessFlg(false);
//                result.setErrorMsg("该数据元已存在！");
//                return result.toJson();
//            }
//            OrgMetaData orgMetaData = new OrgMetaData();
//            orgMetaData.setCode(code);
//            orgMetaData.setName(name);
//            orgMetaData.setOrgDataSet(orgDataSetSeq);
//            orgMetaData.setCreateDate(new Date());
//            orgMetaData.setCreateUser(user);
//            orgMetaData.setOrganization(orgCode);
//            orgMetaData.setDescription(description);
//            if (orgMetaDataManager.createOrgMetaData(orgMetaData) == null) {
//                result.setSuccessFlg(false);
//                result.setErrorMsg("创建数据元失败！");
//                return result.toJson();
//            }
//            result.setSuccessFlg(true);
//        }catch (Exception ex){
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
        return null;
    }

    /**
     * 删除机构数据元
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/orgMetaData" , method = RequestMethod.DELETE)
    @ApiOperation(value = "删除机构数据元")
    public Object deleteOrgMetaData(long id) {

//        Envelop result = new Envelop();
//        try {
//            XOrgMetaData orgMetaData = orgMetaDataManager.getOrgMetaData(id);
//
//            if (orgMetaData == null) {
//                result.setSuccessFlg(false);
//                result.setErrorMsg("该数据元不存在！");
//                return result.toJson();
//            }
//            orgMetaDataManager.deleteOrgMetaData(id);
//            result.setSuccessFlg(true);
//            return result.toJson();
//
//        } catch (Exception e) {
//            result.setSuccessFlg(false);
//            result.setErrorMsg("删除数据元失败！");
//            return result.toJson();
//        }
        return null;
    }

    /**
     * 批量删除机构数据元
     *
     * @param ids
     * @return
     */
    @RequestMapping(value = "/orgMetaDataList" , method = RequestMethod.DELETE)
    @ApiOperation(value = "批量删除机构数据元")
    public Object deleteOrgMetaDataList(@RequestParam("ids[]") Long[] ids) {
//        Envelop result = new Envelop();
//
//        if (StringUtil.isEmpty(ids)) {
//            result.setSuccessFlg(false);
//            return result.toJson();
//        } else {
//            try {
//                orgMetaDataManager.deleteOrgMetaDataList(ids);
//            } catch (Exception e) {
//                result.setSuccessFlg(false);
//                result.setErrorMsg("删除数据元失败！");
//                return result.toJson();
//            }
//            result.setSuccessFlg(true);
//
//            return result.toJson();
//        }
        return null;
    }

    /**
     * 修改机构数据元
     * @param apiVersion
     * @param orgDataSetSeq
     * @param orgCode
     * @param id
     * @param code
     * @param name
     * @param description
     * @param userId
     * @return
     */
    @RequestMapping(value = "/orgMetaData" , method = RequestMethod.PUT)
    @ApiOperation(value = "修改机构数据元")
    public Object updateOrgMetaData(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "orgDataSetSeq", value = "orgDataSetSeq", defaultValue = "")
            @RequestParam(value = "orgDataSetSeq") Integer orgDataSetSeq,
            @ApiParam(name = "orgCode", value = "orgCode", defaultValue = "")
            @RequestParam(value = "orgCode") Long orgCode,
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam(value = "id") Long id,
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @RequestParam(value = "code") Long code,
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "description", value = "description", defaultValue = "")
            @RequestParam(value = "description") String description,
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId) {


//        Envelop result = new Envelop();
//        try{
//            XOrgMetaData orgMetaData = orgMetaDataManager.getOrgMetaData(id);
//            if(orgMetaData == null){
//                result.setSuccessFlg(false);
//                result.setErrorMsg("该数据元不存在！");
//            }else {
//                //重复校验
//                boolean updateFlg = orgMetaData.getCode().equals(code) || !orgMetaDataManager.isExistOrgMetaData(orgDataSetSeq, orgCode, code, name);
//                if (updateFlg) {
//                    orgMetaData.setCode(code);
//                    orgMetaData.setName(name);
//                    orgMetaData.setDescription(description);
//                    orgMetaData.setUpdateDate(new Date());
//                    orgMetaData.setUpdateUser(user);
//                    orgMetaData.setOrganization(orgCode);
//                    orgMetaDataManager.updateOrgMetaData(orgMetaData);
//                    result.setSuccessFlg(true);
//                } else {
//                    result.setSuccessFlg(false);
//                    result.setErrorMsg("该数据元已存在！");
//                }
//            }
//            return  result.toJson();
//        }catch (Exception e) {
//            result.setSuccessFlg(false);
//            result.setErrorMsg("修改数据元失败！");
//            return result.toJson();
//        }
        return null;
    }

    /**
     * 条件查询
     *
     * @param codename
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping(value = "/orgMetaDatas" , method = RequestMethod.GET)
    @ApiOperation(value = "条件查询")
    public Object searchOrgMetaDatas(String orgCode,Integer orgDataSetSeq, String codename, int page, int rows) {
//        Envelop result =new Envelop();
//        try {
//            Map<String, Object> conditionMap = new HashMap<>();
//            conditionMap.put("orgCode", orgCode);
//            conditionMap.put("orgDataSetSeq", orgDataSetSeq);
//            conditionMap.put("code", codename);
//            conditionMap.put("page", page);
//            conditionMap.put("rows", rows);
//            List<OrgMetaDataModel> orgMetaDatas = orgMetaDataManager.searchOrgMetaDatas(conditionMap);
//            Integer totalCount = orgMetaDataManager.searchTotalCount(conditionMap);
//
//            result = getResult(orgMetaDatas, totalCount, page, rows);
//            result.setSuccessFlg(true);
//        }catch (Exception es){
//            result.setSuccessFlg(false);
//        }
//        return result.toJson();
        return null;
    }


}
