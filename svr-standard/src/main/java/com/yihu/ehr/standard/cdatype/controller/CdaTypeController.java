package com.yihu.ehr.standard.cdatype.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.constrant.Result;
import com.yihu.ehr.standard.cdatype.service.CDATypeForInterface;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by AndyCai on 2015/12/14.
 */
@RestController
@RequestMapping(ApiVersionPrefix.CommonVersion + "/cdatype")
@Api(protocols = "https", value = "cdatype", description = "cda类型", tags = {"cda类型"})
public class CdaTypeController {

//    @Resource(name = Services.CDATypeManager)
//    private XCDATypeManager xcdaTypeManager;



    @RequestMapping("getTreeGridData")
    @ApiOperation(value = "获取cda类型树形结构数据)")
    public String getTreeGridData(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion) {
        String strResult = "";

//        try {
//            List<XCDAType> listType = xcdaTypeManager.getCDATypeListByParentId("");
//
//            if (listType != null) {
//                List<CDATypeTreeModel> listTree = getCdaTypeChild(listType);
//
//                ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
//                strResult = objectMapper.writeValueAsString(listTree);
//            }
//        } catch (Exception ex) {
//            LogService.getLogger(CdaTypeController.class).error(ex.getMessage());
//        }

        return strResult;
    }

//    /**
//     * 根据父级信息获取全部的子级信息
//     * @param info 父级信息
//     * @return 全部子级信息
//     */
//    public List<CDATypeTreeModel> getCdaTypeChild(List<XCDAType> info) {
//        List<CDATypeTreeModel> treeInfo = new ArrayList<>();
//
//        try {
//            for (int i = 0; i < info.size(); i++) {
//                CDAType typeInfo = (CDAType) info.retrieve(i);
//                CDATypeTreeModel tree = new CDATypeTreeModel();
//                tree.setId(typeInfo.getId());
//                tree.setCode(typeInfo.getCode());
//                tree.setName(typeInfo.getName());
//                tree.setDescription(typeInfo.getDescription());
//                List<XCDAType> listChild = xcdaTypeManager.getCDATypeListByParentId(typeInfo.getId());
//
//                List<CDATypeTreeModel> listChildTree = getCdaTypeChild(listChild);
//
//                tree.setChildren(listChildTree);
//
//                treeInfo.add(tree);
//            }
//        } catch (Exception ex) {
//            LogService.getLogger(CdaTypeController.class).error(ex.getMessage());
//        }
//
//        return treeInfo;
//    }

//    /**
//     * 根据父级类别获取全部的子类别ID，返回值包括父级的ID
//     * @param info 父级信息
//     * @param childrenIds   子级ID
//     * @return 全部子级
//     */
//    public String getCdaTypeChildId(List<XCDAType> info,String childrenIds) {
//        try {
//            for (int i = 0; i < info.size(); i++) {
//                CDAType typeInfo = (CDAType) info.retrieve(i);
//                childrenIds+=typeInfo.getId()+",";
//                List<XCDAType> listChild = xcdaTypeManager.getCDATypeListByParentId(typeInfo.getId());
//                if(listChild.size()>0)
//                    childrenIds=getCdaTypeChildId(listChild,childrenIds);
//            }
//        } catch (Exception ex) {
//            LogService.getLogger(CdaTypeController.class).error(ex.getMessage());
//        }
//        return childrenIds;
//    }

    @RequestMapping("GetCdaTypeListByKey")
    @ApiOperation(value = "查询cda类型列表")
    public Object GetCdaTypeListByKey(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "strKey", value = "搜索值", defaultValue = "")
            @RequestParam(value = "strKey") String strKey,
            @ApiParam(name = "page", value = "当前页", defaultValue = "1")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "rows", value = "每页行数", defaultValue = "15")
            @RequestParam(value = "rows") int rows) {
        Result result = new Result();
//        try {
//            Map<String, Object> mapKey = new HashMap<>();
//            mapKey.put("key", strKey);
//            mapKey.put("page", page);
//            mapKey.put("rows", rows);
//            List<XCDAType> listType = xcdaTypeManager.getCDATypeListByKey(mapKey);
//
//            if (listType == null) {
//                result.setSuccessFlg(false);
//                result.setErrorMsg("数据获取失败!");
//                return result;
//            }
//
//            List<CDATypeForInterface> listInfo = getTypeForInterface(listType);
//
//            if(rows<=0)
//            {
//                rows=1;
//            }
//            result = getResult(listInfo, 1, page, rows);
//        } catch (Exception ex) {
//            LogService.getLogger(CdaTypeController.class).error(ex.getMessage());
//            result.setSuccessFlg(false);
//            result.setErrorMsg("系统错误，请联系管理员!");
//        }
        return result;
    }

    @RequestMapping("getCdaTypeById")
    @ApiOperation(value = "根据编号查询cda类型")
    public Object getCdaTypeById(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "strIds", value = "编号", defaultValue = "")
            @RequestParam(value = "strIds") String strIds) {
        Result result = new Result();
//        result.setSuccessFlg(false);
//
//        try {
//            List<XCDAType> listType = xcdaTypeManager.getCdatypeInfoByIds(strIds);
//            if (listType.size() > 0) {
//                result.setSuccessFlg(true);
//
//                List<CDATypeForInterface> listInfo = getTypeForInterface(listType);
//
//                result.setObj(listInfo.retrieve(0));
//            }
//        } catch (Exception ex) {
//            LogService.getLogger(CdaController.class).error(ex.getMessage());
//            result.setErrorMsg("系统错误，请联系管理员!");
//        }
        return result;
    }

    @RequestMapping("delteCdaTypeInfo")
    @ApiOperation(
            value = "删除CDA类别，若该类别存在子类别，将一并删除子类别" +
                    "先根据当前的类别ID获取全部子类别ID，再进行删除")
    public Object delteCdaTypeInfo(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "ids", value = "cdaType编号", defaultValue = "")
            @RequestParam(value = "ids") String ids) {
        Result result = new Result();
//        try {
//            String strErrorMsg = "";
//
//            if (ids == null || ids == "") {
//                strErrorMsg += "请先选择将要删除的数据！";
//            }
//
//            if (strErrorMsg != "") {
//                result.setSuccessFlg(false);
//                result.setErrorMsg(strErrorMsg);
//            }
//            List<XCDAType> listParentType = xcdaTypeManager.getCdatypeInfoByIds(ids);
//            String childrenIds = getCdaTypeChildId(listParentType, "");
//            if(childrenIds.length()>0) {
//                childrenIds = childrenIds.substring(0, childrenIds.length() - 1);
//            }
//
//            boolean reault = xcdaTypeManager.deleteCdaType(childrenIds);
//            if (reault) {
//                result.setSuccessFlg(true);
//            } else {
//                result.setSuccessFlg(false);
//                result.setErrorMsg("删除失败!");
//            }
//        } catch (Exception ex) {
//            LogService.getLogger(CdaController.class).error(ex.getMessage());
//
//            result.setSuccessFlg(false);
//            result.setErrorMsg("系统错误，请联系管理员!");
//        }
        return result;
    }


    @RequestMapping("SaveCdaType")
    @ApiOperation(value = "保存cdaType")
    public Object SaveCdaType(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "info", value = "cdaType编号", defaultValue = "")
            @RequestParam(value = "info") CDATypeForInterface info) {
        Result result = new Result();
//        try {
//            String strErrorMsg = "";
//            if (info.getCode() == null || info.getCode() == "") {
//                strErrorMsg += "代码不能为空!";
//            }
//            if (info.getName() == null || info.getName() == "") {
//                strErrorMsg += "名称不能为空!";
//            }
//            XCDAType xcdaType = new CDAType();
//            //id 不为空则先获取数据，再进行修改
//            if (!StringUtil.isEmpty(info.getId())) {
//                List<XCDAType> listType = xcdaTypeManager.getCdatypeInfoByIds(info.getId());
//                if (listType.size() > 0) {
//                    xcdaType = listType.retrieve(0);
//                }
//                if(!info.getCode().equals(xcdaType.getCode()) && xcdaTypeManager.isCodeExist(info.getCode())){
//                    strErrorMsg = "代码已存在!";
//                }
//                xcdaType.setUpdateUser(info.getUserId());
//                xcdaType.setUpdateDate(new Date());
//            } else {
//                if(xcdaTypeManager.isCodeExist(info.getCode())){
//                    strErrorMsg = "代码已存在!";
//                }
//                xcdaType.setCreateUser(info.getUserId());
//                xcdaType.setCreateDate(new Date());
//            }
//            xcdaType.setCode(info.getCode());
//            xcdaType.setName(info.getName());
//            xcdaType.setParentId(info.getParentId());
//            xcdaType.setDescription(info.getDescription());
//
//            if (!strErrorMsg.equals("")) {
//                result.setSuccessFlg(false);
//                result.setErrorMsg(strErrorMsg);
//                return result;
//            }
//
//            boolean iResult = false;
//            if (info.getId() == "") {
//                XEnvironmentOption environmentOption = ServiceFactory.getService(Services.EnvironmentOption);
//                Object objectID = new ObjectId(Short.parseShort(environmentOption.getOption(EnvironmentOptions.AdminRegion)), BizObject.StdArchive);
//
//                xcdaType.setId(objectID.toString());
//                iResult = xcdaTypeManager.insertCdaType(xcdaType);
//
//            } else {
//                iResult = xcdaTypeManager.updateCdaType(xcdaType);
//            }
//
//            if (iResult) {
//                result.setSuccessFlg(true);
//            } else {
//                result.setSuccessFlg(false);
//                result.setErrorMsg("保存失败!");
//            }
//
//        } catch (Exception ex) {
//            LogService.getLogger(CdaController.class).error(ex.getMessage());
//
//            result.setSuccessFlg(false);
//            result.setErrorMsg("系统错误，请联系管理员!");
//        }
        return result;
    }

    @RequestMapping("getParentType")
    @ApiOperation(value = "获取cdaType父节点")
    public String getParentType(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "strId", value = "cdaType编号", defaultValue = "")
            @RequestParam(value = "strId") String strId,
            @ApiParam(name = "key", value = "cdaType编号", defaultValue = "")
            @RequestParam(value = "key") String key) {
        String strResult = "";
//        try {
//            List<XCDAType> listParentType = xcdaTypeManager.getCdatypeInfoByIds(strId);
//            String childrenIds = getCdaTypeChildId(listParentType,"");
//            if(childrenIds.length()>0) {
//                childrenIds = childrenIds.substring(0, childrenIds.length() - 1);
//            }
//
//            List<XCDAType> listType = xcdaTypeManager.getParentType(childrenIds,key);
//            if (listType != null) {
//                List<CDATypeForInterface> listInfo = getTypeForInterface(listType);
//
//                ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
//                strResult = objectMapper.writeValueAsString(listInfo);
//            }
//        } catch (Exception ex) {
//            LogService.getLogger(CdaTypeController.class).error(ex.getMessage());
//
//        }
        return strResult;
    }

    @RequestMapping("getCDATypeListByParentId")
    @ApiOperation(value = "获取cdaType所有子节点")
    public String getCDATypeListByParentId(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "ids", value = "cdaType编号", defaultValue = "")
            @RequestParam(value = "ids") String ids ) {
        String strResult = "";

//        try {
//            List<XCDAType> listType = xcdaTypeManager.getCDATypeListByParentId(ids);
//            List<CDATypeForInterface> cdaTypeModels = getTypeForInterface(listType);
//            if (listType != null) {
//                ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
//                strResult = objectMapper.writeValueAsString(cdaTypeModels);
//            }
//
//        } catch (Exception ex) {
//                LogService.getLogger(CdaController.class).error(ex.getMessage());
//        }

        return strResult;
    }


//    public List<CDATypeForInterface> getTypeForInterface(List<XCDAType> listType) {
//        if (listType == null) {
//            return null;
//        }
//        List<CDATypeForInterface> listInfo = new ArrayList<>();
//        for (int i = 0; i < listType.size(); i++) {
//            CDAType cdaType = (CDAType) listType.retrieve(i);
//            CDATypeForInterface info = new CDATypeForInterface();
//            info.setId(cdaType.getId());
//            info.setCode(cdaType.getCode());
//            info.setName(cdaType.getName());
//            info.setParentId(cdaType.getParentId());
//            String strParentName = "";
//            if (cdaType.getParentId() != null && !cdaType.getParentId().equals("")) {
//                strParentName = cdaType.getParentCdaType()!=null?cdaType.getParentCdaType().getName():"";
//            }
//            info.setParentName(strParentName);
//            info.setDescription(cdaType.getDescription());
//            listInfo.add(info);
//        }
//        return listInfo;
//    }

}
