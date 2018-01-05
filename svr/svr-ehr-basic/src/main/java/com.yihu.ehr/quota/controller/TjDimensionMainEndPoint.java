package com.yihu.ehr.quota.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.quota.TjDimensionMain;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.quota.service.TjDimensionMainService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * Created by janseny on 2017/5/8.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "TjDimensionMain", description = "主维度", tags = {"主维度"})
public class TjDimensionMainEndPoint extends EnvelopRestEndPoint {

    @Autowired
    ObjectMapper objectMapper;
    
    @Autowired
    TjDimensionMainService tjDimensionMainService;

    @RequestMapping(value = ServiceApi.TJ.GetTjDimensionMainList, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询主维度")
    public ListResult getTjDimensionMainList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ListResult listResult = new ListResult();
        List<TjDimensionMain> qtjDimensionMainList = tjDimensionMainService.search(fields, filters, sorts, page, size);
        if(qtjDimensionMainList != null){
            listResult.setDetailModelList(qtjDimensionMainList);
            listResult.setTotalCount((int)tjDimensionMainService.getCount(filters));
            listResult.setCode(200);
            listResult.setCurrPage(page);
            listResult.setPageSize(size);
        }else{
            listResult.setCode(200);
            listResult.setMessage("查询无数据");
            listResult.setTotalCount(0);
        }
        return listResult;
    }

    @RequestMapping(value = ServiceApi.TJ.TjDimensionMain, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增&修改主维度")
    public ObjectResult add(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody String model) throws Exception{
        TjDimensionMain obj = objectMapper.readValue(model, TjDimensionMain.class);
        if(obj.getId() != 0){
            obj.setUpdateTime(new Date());
        }else{
            obj.setCreateTime(new Date());
        }
        obj = tjDimensionMainService.save(obj);
        return Result.success("主维度更新成功！", obj);
    }


    @RequestMapping(value = ServiceApi.TJ.TjDimensionMain, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除主维度")
    public Result delete(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") Long id) throws Exception{
        TjDimensionMain tjDimensionMain = tjDimensionMainService.getTjDimensionMain(Integer.valueOf(id.toString()));
        tjDimensionMain.setStatus(-1);
        tjDimensionMainService.save(tjDimensionMain);
        return Result.success("主维度删除成功！");
    }

    @RequestMapping(value = ServiceApi.TJ.TjDimensionMainId, method = RequestMethod.GET)
    @ApiOperation(value = "获取主维度信息", notes = "获取主维度信息")
    TjDimensionMain getTjDimensionMain(
            @PathVariable(value = "id") Integer id){
        return tjDimensionMainService.getTjDimensionMain(id);
    };

    @RequestMapping(value = ServiceApi.TJ.TjDimensionMainCode, method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "获取主维度")
    public TjDimensionMain getTjDimensionMain(
            @ApiParam(name = "code", value = "code")
            @RequestParam(value = "code") String code) throws Exception {
        String filter = "code=" + code;
        List<TjDimensionMain> tjDimensionMains = tjDimensionMainService.search(filter);
        if(tjDimensionMains != null && tjDimensionMains.size() >0){
            return  tjDimensionMains.get(0);
        }else{
            return null;
        }
    }

    @RequestMapping(value = ServiceApi.TJ.TjDimensionMainName,method = RequestMethod.GET)
    @ApiOperation(value = "验证名称是否存在")
    public boolean isNameExists(
            @ApiParam(value = "name")
            @RequestParam(value = "name") String name)throws Exception {
        String filter = "name=" + name;
        List<TjDimensionMain> tjDimensionMains = tjDimensionMainService.search(filter);
        if(tjDimensionMains != null && tjDimensionMains.size() >0){
            return  true;
        }else{
            return false;
        }
    }

    @RequestMapping(value = ServiceApi.TJ.TjDimensionMainIsExist,method = RequestMethod.GET)
    @ApiOperation("获取已存在主维度编码")
    public List TjDimensionMainIsExist(
            @ApiParam(name="mainCode",value="mainCode")
            @RequestBody String mainCode) throws Exception {
        List values = tjDimensionMainService.tjDimensionMainIsExist(toEntity(mainCode, String[].class));
        return values;
    }
}
