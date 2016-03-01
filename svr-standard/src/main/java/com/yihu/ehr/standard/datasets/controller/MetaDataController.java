package com.yihu.ehr.standard.datasets.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.standard.MStdMetaData;
import com.yihu.ehr.standard.commons.ExtendController;
import com.yihu.ehr.standard.datasets.service.IMetaData;
import com.yihu.ehr.standard.datasets.service.MetaDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.3
 */
@RestController
@RequestMapping(ApiVersion.Version1_0 + "/std")
@Api(protocols = "https", value = "metadata", description = "标准数据元", tags = {"标准数据元"})
public class MetaDataController extends ExtendController<MStdMetaData> {

    @Autowired
    private MetaDataService metaDataService;

    private Class getServiceEntity(String version){

        return  metaDataService.getServiceEntity(version);
    }

    @RequestMapping(value = "/metadatas", method = RequestMethod.GET)
    @ApiOperation(value = "查询数据元")
    public Collection<MStdMetaData> searchDataSets(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception{

        Class entityClass = getServiceEntity(version);
        List ls = metaDataService.search(entityClass, fields, filters, sorts, page, size);
        pagedResponse(request, response, metaDataService.getCount(entityClass, filters), page, size);
        return convertToModels(ls, new ArrayList<>(ls.size()), MStdMetaData.class, fields);
    }


    @RequestMapping(value = "/metadatas", method = RequestMethod.DELETE)
    @ApiOperation(value = "批量删除数据元")
    public boolean deleteMetaDatas(
            @ApiParam(name = "ids", value = "编号集", defaultValue = "")
            @RequestParam(value = "ids") String ids,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version) throws Exception{

        metaDataService.delete(strToLongArr(ids), getServiceEntity(version));
        return true;
    }

    @RequestMapping(value = "/dataset/{dataSetId}/metadata", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据集关联的数据元")
    public boolean deleteMetaDataByDataSet(
            @ApiParam(name = "dataSetId", value = "数据集编号", defaultValue = "")
            @PathVariable(value = "dataSetId") long dataSetId,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version) throws Exception{

        return metaDataService.deleteByField("dataSetId", dataSetId, getServiceEntity(version)) > 0;
    }

    @RequestMapping(value = "/metadata/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据元")
    public boolean deleteMetaData(
            @ApiParam(name = "id", value = "编号集", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version) throws Exception{

        metaDataService.delete(id, getServiceEntity(version));
        return true;
    }

    @RequestMapping(value = "/metadata/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取数据元")
    public MStdMetaData getMetaData(
            @ApiParam(name = "id", value = "数据元编号", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version) throws Exception{

        return getModel(metaDataService.retrieve(id, getServiceEntity(version)));
    }


    @RequestMapping(value = "/metadata", method = RequestMethod.PUT)
    @ApiOperation(value = "更新数据元")
    public MStdMetaData updataMetaSet(
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "model", value = "数据源模型", defaultValue = "")
            @RequestParam(value = "model", required = false) String model) throws Exception{

        Class entityClass = getServiceEntity(version);
        IMetaData metaDataModel = (IMetaData) jsonToObj(model, entityClass);
        IMetaData metaData = metaDataService.retrieve(metaDataModel.getId(), entityClass);
        if(metaData.getId()==0)
            throw errNotFound();

        if(!metaData.getCode().equals(metaDataModel.getCode())
                && metaDataService.isColumnValExsit(metaDataModel.getDataSetId(), "code", metaDataModel.getCode(), entityClass))
            throw errRepeatCode();

        BeanUtils.copyProperties(model, metaData);
        metaDataService.save(metaData);
        return getModel(metaData);
    }

    @RequestMapping(value = "/metadata", method = RequestMethod.POST)
    @ApiOperation(value = "新增数据元")
    public MStdMetaData saveMetaSet(
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "model", value = "数据源模型", defaultValue = "")
            @RequestParam(value = "model", required = false) String model) throws Exception{

        IMetaData metaData = (IMetaData) jsonToObj(model, getServiceEntity(version));
        if(metaDataService.isColumnValExsit(metaData.getDataSetId(), "code", metaData.getCode(), getServiceEntity(version)))
            throw errRepeatCode();
        if(metaDataService.saveMetaData(metaData, version))
            return getModel(model);
        return null;
    }


    @RequestMapping(value = "/metadata/validate/code", method = RequestMethod.GET)
    @ApiOperation(value = "验证数据元代码是否重复")
    public boolean validateCode(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "dataSetId", value = "数据集编号", defaultValue = "")
            @RequestParam(value = "dataSetId") long dataSetId,
            @ApiParam(name = "code", value = "查询代码", defaultValue = "")
            @RequestParam(value = "code") String code) throws Exception{

        return metaDataService.isColumnValExsit(dataSetId, "code", code, getServiceEntity(version));
    }

    @RequestMapping(value = "/metadata/validate/name", method = RequestMethod.GET)
    @ApiOperation(value = "验证数据元名称是否重复")
    public boolean validatorName(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "dataSetId", value = "数据集编号", defaultValue = "")
            @RequestParam(value = "dataSetId") long dataSetId,
            @ApiParam(name = "name", value = "查询名称", defaultValue = "")
            @RequestParam(value = "name") String name) throws Exception {

        return metaDataService.isColumnValExsit(dataSetId, "name", name, getServiceEntity(version));
    }

    @RequestMapping(value = "/metadatas/map", method = RequestMethod.GET)
    @ApiOperation(value = "获取数据元 id-name : map集")
    public Map getMetaDataMapByIds(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "medaIds", value = "数据元编号", defaultValue = "")
            @RequestParam(value = "medaIds") String metaIds) {

        return metaDataService.getMetaDataMapByIds(strToLongArr(metaIds), version);
    }
}