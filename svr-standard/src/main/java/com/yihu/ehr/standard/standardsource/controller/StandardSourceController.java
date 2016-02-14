//package com.yihu.ehr.standard.standardsource.controller;
//
//import com.yihu.ehr.constants.ApiVersionPrefix;
//import com.yihu.ehr.constrant.Result;
//import com.yihu.ehr.model.dict.MConventionalDict;
//import com.yihu.ehr.standard.standardsource.Client.ConventionalDictClient;
//import com.yihu.ehr.standard.standardsource.model.StandardSourceModel;
//import com.yihu.ehr.standard.standardsource.service.StandardSource;
//import com.yihu.ehr.standard.standardsource.service.StandardSourceManager;
//import com.yihu.ehr.util.controller.BaseController;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiParam;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.*;
//
///**
// * @author lincl
// * @version 1.0
// * @created 2016.1.23
// */
//@RestController
//@RequestMapping(ApiVersionPrefix.CommonVersion + "/standard/source")
//@Api(protocols = "https", value = "standard/source", description = "标准来源", tags = {"标准来源"})
//public class StandardSourceController extends BaseController{
//
//    @Autowired
//    private StandardSourceManager standardSourceManager;
//    @Autowired
//    ConventionalDictClient conventionalDictClient;
//    /**
//     * 标准来源分页搜索
//     *
//     * @param searchCode
//     * @param searchName
//     * @param searchType
//     * @param page
//     * @param rows
//     * @return
//     */
//    @RequestMapping("searchStdSource")
//    @ApiOperation(value = "标准来源分页搜索(名称或代码、类型)")
//    public Object searchStdSource(
//            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
//            @PathVariable( value = "api_version") String apiVersion,
//            @ApiParam(name = "searchCode", value = "代码搜索值", defaultValue = "")
//            @RequestParam(value = "searchCode") String searchCode,
//            @ApiParam(name = "searchName", value = "名称搜索值", defaultValue = "")
//            @RequestParam(value = "searchName") String searchName,
//            @ApiParam(name = "searchType", value = "类型搜索值", defaultValue = "")
//            @RequestParam(value = "searchType") String searchType,
//            @ApiParam(name = "page", value = "当前页", defaultValue = "1")
//            @RequestParam(value = "page") int page,
//            @ApiParam(name = "rows", value = "每页行数", defaultValue = "15")
//            @RequestParam(value = "rows") int rows ) {
//        List<StandardSource> standardSources = standardSourceManager.getSourceByKey(searchCode, searchName, searchType, page, rows);
//        List<StandardSourceModel> standardSourceModels = new ArrayList<>();
//        for (StandardSource standardSource : standardSources) {
//            StandardSourceModel standardSourceModel = standardSourceManager.getSourceByKey(standardSource);
//            standardSourceModels.add(standardSourceModel);
//        }
//        Integer totalCount = standardSourceManager.getSourceByKeyInt(searchCode, searchName, searchType);
//        return getResult(standardSourceModels, totalCount, page, rows);
//    }
//
//    @RequestMapping("getAllStdSource")
//    @ApiOperation(value = "获取所有标准来源")
//    public Object getAllStdSource(
//            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
//            @PathVariable( value = "api_version") String apiVersion ) {
//        List<StandardSource> standardSources = Arrays.asList(standardSourceManager.getSourceList());
//        List<StandardSourceModel> standardSourceModels = new ArrayList<>();
//        for (StandardSource standardSource : standardSources) {
//            StandardSourceModel standardSourceModel = standardSourceManager.getSourceByKey(standardSource);
//            standardSourceModels.add(standardSourceModel);
//        }
//        return getResult(standardSourceModels, standardSourceModels.size(), 1, standardSourceModels.size());
//    }
//
//    /**
//     * 根据id获取标准来源信息
//     *
//     * @param id
//     * @return
//     */
//    @RequestMapping("getStdSource")
//    @ApiOperation(value = "根据id获取标准来源信息")
//    public Object getStdSource(
//            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
//            @PathVariable( value = "api_version") String apiVersion,
//            @ApiParam(name = "id", value = "标准来源编号", defaultValue = "")
//            @RequestParam(value = "id") String id) {
//        Result result = new Result();
//        try {
//            StandardSource xStandardSources = standardSourceManager.getSourceBySingleId(id);
//            if(xStandardSources!=null){
//                if(!StringUtils.isEmpty(xStandardSources.getSourceType())){
//                    MConventionalDict dict = conventionalDictClient.getStdSourceType(apiVersion, xStandardSources.getSourceType());
//                    xStandardSources.setSourceValue(dict.getValue());
//                }
//            }
//            else
//                xStandardSources = new StandardSource();
//            result.setSuccessFlg(true);
//            result.setObj(xStandardSources);
//        }catch (Exception e){
//            result.setSuccessFlg(false);
//        }
//        return result;
//    }
//
//    /**
//     * 更新标准来源
//     *
//     * @param id
//     * @param code
//     * @param name
//     * @param type
//     * @param description
//     * @return
//     */
//    @RequestMapping("updateStdSource")
//    @ApiOperation(value = "保存标准来源，通过id取数据，取不到数据时新增，否则修改")
//    public Object updateStdSource(
//            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
//            @PathVariable( value = "api_version") String apiVersion,
//            @ApiParam(name = "id", value = "标准来源编号", defaultValue = "")
//            @RequestParam(value = "id") String id,
//            @ApiParam(name = "code", value = "编码", defaultValue = "")
//            @RequestParam(value = "code") String code,
//            @ApiParam(name = "name", value = "名称", defaultValue = "")
//            @RequestParam(value = "name") String name,
//            @ApiParam(name = "type", value = "类型", defaultValue = "")
//            @RequestParam(value = "type") String type,
//            @ApiParam(name = "description", value = "描述", defaultValue = "")
//            @RequestParam(value = "description") String description) {
//        try {
//            Result result = new Result();
//            StandardSource standardSource = standardSourceManager.getSourceBySingleId(id);
//            boolean checkCode = true;
//            if (standardSource == null) {
//                standardSource = new StandardSource();
//            } else {
//                if (code != null && code.equals(standardSource.getCode()))
//                    checkCode = false;
//            }
//            if (checkCode) {
//                if (standardSourceManager.isSourceCodeExist(code)) {
//                    result.setErrorMsg("codeNotUnique");
//                    return result;
//                }
//            }
//            standardSource.setCode(code);
//            standardSource.setName(name);
//            standardSource.setSourceType(type);
//            standardSource.setDescription(description);
//            standardSource.setUpdateDate(new Date());
//            standardSourceManager.saveSourceInfo(standardSource);
//            return getSuccessResult(true);
//        } catch (Exception e) {
//            return getSuccessResult(false);
//        }
//    }
//
//    /**
//     * 通过id组删除标准来源
//     *
//     * @param id
//     * @return
//     */
//    @RequestMapping("delStdSource")
//    @ApiOperation(value = "通过id组删除标准来源，多个id以,分隔")
//    public Object delStdSource(
//            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
//            @PathVariable( value = "api_version") String apiVersion,
//            @ApiParam(name = "id", value = "标准来源编号", defaultValue = "")
//            @RequestParam(value = "id") String id) {
//        String ids[] = id.split(",");
//        int rtn = standardSourceManager.deleteSource(Arrays.asList(ids));
//        Result result = rtn == -1 ? getSuccessResult(false) : getSuccessResult(true);
//        return result;
//    }
//
//
//
//
//
//
//}
