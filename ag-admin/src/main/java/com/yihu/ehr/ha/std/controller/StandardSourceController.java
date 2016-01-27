package com.yihu.ehr.ha.std.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

/**
 * Created by AndyCai on 2016/1/25.
 */
@RequestMapping(ApiVersionPrefix.CommonVersion + "/stdSource")
@RestController
public class StandardSourceController extends BaseRestController {

    @RequestMapping(value = "/standardSources",method = RequestMethod.GET)
    public Object getStdSourceList(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                   @PathVariable(value = "apiVersion") String apiVersion) {
        return null;
    }

    @RequestMapping(value = "/standardSource",method = RequestMethod.GET)
    public Object getStdSourceById(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                   @PathVariable(value = "apiVersion") String apiVersion,
                                   @ApiParam(name = "id",value = "标准来源ID")
                                   @RequestParam(value = "id")String id){
        return null;
    }
    @RequestMapping(value = "/getStdSource",method = RequestMethod.GET)
    public String getStandardSourceByCodeOrName(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                      @PathVariable(value = "apiVersion") String apiVersion,
                                  @ApiParam(name = "code",value = "来源代码")
                                  @RequestParam(value = "code")String code,
                                  @ApiParam(name = "name",value = "来源名称")
                                      @RequestParam(value = "name")String name,
                                  @ApiParam(name = "type",value = "来源类型")
                                      @RequestParam(value = "type")String type,
                                  @ApiParam(name = "page", value = "当前页", defaultValue = "1")
                                      @RequestParam(value = "page") int page,
                                  @ApiParam(name = "rows", value = "每页行数", defaultValue = "20")
                                      @RequestParam(value = "rows") int rows) {
        return null;
    }

    @RequestMapping(value = "/standardSource",method = RequestMethod.POST)
    public Object saveStandardSource(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                                     @PathVariable(value = "apiVersion") String apiVersion,
                                     @ApiParam(name = "id",value = "id")
                                     @RequestParam(value = "id")String id,
                                     @ApiParam(name = "code",value = "代码")
                                     @RequestParam(value = "code")String code,
                                     @ApiParam(name = "name",value = "名称")
                                     @RequestParam(value = "name")String name,
                                     @ApiParam(name = "type",value = "类别")
                                     @RequestParam(value = "type")String type,
                                     @ApiParam(name = "description",value = "说明")
                                     @RequestParam(value = "description")String description){

        return null;
    }


    @RequestMapping(value = "/standardSource",method = RequestMethod.DELETE)
    public String delStdSource(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
                               @PathVariable(value = "apiVersion") String apiVersion,
                               @ApiParam(name = "id", value = "id")
                               @RequestParam(value = "id") String id) {
        return null;
    }
}
