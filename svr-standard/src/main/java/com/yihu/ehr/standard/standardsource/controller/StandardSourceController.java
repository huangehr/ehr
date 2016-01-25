package com.yihu.ehr.standard.standardsource.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.constrant.Result;
import com.yihu.ehr.standard.standardsource.model.StandardSourceModel;
import com.yihu.ehr.standard.standardsource.service.StandardSource;
import com.yihu.ehr.standard.standardsource.service.StandardSourceManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.1.23
 */
@RestController
@RequestMapping(ApiVersionPrefix.CommonVersion + "/standard/source")
@Api(protocols = "https", value = "standard/source", description = "标准来源", tags = {"标准来源"})
public class StandardSourceController {


    @Autowired
    private StandardSourceManager standardSourceManager;

    /**
     * 标准来源分页搜索
     *
     * @param searchNm
     * @param searchType
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("searchStdSource")
    @ApiOperation(value = "标准来源分页搜索(名称或代码、类型)")
    public Object searchStdSource(String searchNm, String searchType, int page, int rows) {
        List<StandardSource> standardSources = standardSourceManager.getSourceByKey(searchNm, searchType, page, rows);
        List<StandardSourceModel> standardSourceModels = new ArrayList<>();
        for (StandardSource standardSource : standardSources) {
            StandardSourceModel standardSourceModel = standardSourceManager.getSourceByKey(standardSource);
            standardSourceModels.add(standardSourceModel);
        }
        Integer totalCount = standardSourceManager.getSourceByKeyInt(searchNm, searchType);
        return getResult(standardSourceModels, totalCount, page, rows);
    }

    /**
     * 根据id获取标准来源信息
     *
     * @param id
     * @return
     */
    @RequestMapping("getStdSource")
    @ApiOperation(value = "根据id获取标准来源信息")
    public Object getStdSource(String id) {
        Result result = new Result();
        try {
            StandardSource xStandardSources = standardSourceManager.getSourceBySingleId(id);
            xStandardSources = xStandardSources==null? new StandardSource():xStandardSources;
            result.setSuccessFlg(true);
            result.setObj(xStandardSources);
        }catch (Exception e){
            result.setSuccessFlg(false);
        }
        return result;
    }

    /**
     * 更新标准来源
     *
     * @param id
     * @param code
     * @param name
     * @param type
     * @param description
     * @return
     */
    @RequestMapping("updateStdSource")
    @ApiOperation(value = "更新标准来源，通过id取数据，取不到数据时新增，否则修改")
    public Object updateStdSource(String id, String code, String name, String type, String description) {
        try {
            Result result = new Result();
            StandardSource standardSource = standardSourceManager.getSourceBySingleId(id);
            boolean checkCode = true;
            if (standardSource == null) {
                standardSource = new StandardSource();
            } else {
                if (code != null && code.equals(standardSource.getCode()))
                    checkCode = false;
            }
            if (checkCode) {
                if (standardSourceManager.isSourceCodeExist(code)) {
                    result.setErrorMsg("codeNotUnique");
                    return result;
                }
            }
            standardSource.setCode(code);
            standardSource.setName(name);
            standardSource.setSourceType(type);
            standardSource.setDescription(description);
            standardSource.setUpdateDate(new Date());
            standardSourceManager.saveSourceInfo(standardSource);
            return getSuccessResult(true);
        } catch (Exception e) {
            return getSuccessResult(false);
        }
    }

    /**
     * 通过id组删除标准来源
     *
     * @param id
     * @return
     */
    @RequestMapping("delStdSource")
    @ApiOperation(value = "通过id组删除标准来源，多个id以,分隔")
    public Object delStdSource(String id) {
        String ids[] = id.split(",");
        int rtn = standardSourceManager.deleteSource(Arrays.asList(ids));
        Result result = rtn == -1 ? getSuccessResult(false) : getSuccessResult(true);
        return result;
    }





    private Result getResult(List detaiModelList, int totalCount, int currPage, int rows) {
        Result result = new Result();
        result.setSuccessFlg(true);
        result.setDetailModelList(detaiModelList);
        result.setTotalCount(totalCount);
        result.setCurrPage(currPage);
        result.setPageSize(rows);
        if (result.getTotalCount() % result.getPageSize() > 0) {
            result.setTotalPage((result.getTotalCount() / result.getPageSize()) + 1);
        } else {
            result.setTotalPage(result.getTotalCount() / result.getPageSize());
        }
        return result;
    }

    private Result getSuccessResult(Boolean flg) {
        Map<String, Object> mp = new HashMap<String, Object>();
        Result result = new Result();
        result.setSuccessFlg(flg);
        return result;
    }
}
