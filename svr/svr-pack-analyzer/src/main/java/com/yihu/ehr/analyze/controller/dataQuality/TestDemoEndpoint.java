package com.yihu.ehr.analyze.controller.dataQuality;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 *  质控管理- 首页
 * @author HZY
 * @created 2018/8/17 11:24
 */
@RestController("/demo")
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "TestDemoEndpoint", description = "测试es聚合demo", tags = {"临时测试demo"})
public class TestDemoEndpoint extends EnvelopRestEndPoint {

    @Autowired
    private TestDemo testDemo;


    @RequestMapping(value = "test", method = RequestMethod.GET)
    @ApiOperation(value = "test(测试数据获取")
    public   List<Map<String, Object>>  test(
            @ApiParam(name = "start", value = "开始时间，（接收时间）")
            @RequestParam(value = "start", required = false) String start,
            @ApiParam(name = "end", value = "结束时间，（接收时间）")
            @RequestParam(value = "end", required = false) String end ,
            @ApiParam(name = "org_area", value = "org_area")
            @RequestParam(value = "org_area", required = false) String org_area
    ) throws Exception {
        List<Map<String, Object>> result = testDemo.testAgg(start, end, org_area);
        return result;
    }

    @RequestMapping(value = "test2", method = RequestMethod.GET)
    @ApiOperation(value = "test(测试数据获取")
    public   List<Map<String, Object>>  test2(
            @ApiParam(name = "start", value = "开始时间，（接收时间）")
            @RequestParam(value = "start", required = false) String start,
            @ApiParam(name = "end", value = "结束时间，（接收时间）")
            @RequestParam(value = "end", required = false) String end ,
            @ApiParam(name = "org_area", value = "org_area")
            @RequestParam(value = "org_area", required = false) String org_area
    ) throws Exception {
//        List<Map<String, Object>> result = testDemo.testAgg(start, end, org_area);
        List<Map<String, Object>> result = testDemo.testAgg2(start, end);
        return result;
    }

    @RequestMapping(value = "testAdd", method = RequestMethod.GET)
    @ApiOperation(value = "testAdd(测试数据添加")
    public   void testAdd(
            @ApiParam(name = "org_area", value = "org_area")
            @RequestParam(value = "org_area", required = false) String org_area
    ) throws Exception {
//        List<Map<String, Object>> result = testDemo.testAgg(start, end, org_area);
        testDemo.testadd(org_area);
    }

    @RequestMapping(value = "testQuery", method = RequestMethod.GET)
    @ApiOperation(value = "testQuery(测试数据查询")
    public    List<Map<String, Object>> testQuery(
    ) throws Exception {
        List<Map<String, Object>> map = testDemo.testSearch();
        return map;
    }

    @RequestMapping(value = "testQuer2y", method = RequestMethod.GET)
    @ApiOperation(value = "testQuery(测试数据查询2")
    public    List<Map<String, Object>> testQuery2(
    ) throws Exception {
        List<Map<String, Object>> map = testDemo.testSearch2();
        return map;
    }

    @RequestMapping(value = "testDel", method = RequestMethod.DELETE)
    @ApiOperation(value = "testAdd(测试数据删除")
    public   void testDel(
    ) throws Exception {
//        List<Map<String, Object>> result = testDemo.testAgg(start, end, org_area);
        testDemo.testDel();
    }
}
