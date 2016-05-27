package com.yihu.ehr.profile.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.data.hbase.HBaseDao;
import com.yihu.ehr.data.hbase.ResultUtil;
import com.yihu.ehr.data.hbase.TableBundle;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.util.controller.BaseRestEndPoint;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import javafx.scene.control.Tab;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;
import java.util.Iterator;

/**
 * @author Sand
 * @created 2016.05.25 10:09
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "历史档案服务", description = "维护/获取历史档案")
public class LegacyProfileEndPoint extends BaseRestEndPoint {
    private String Table = "HealthArchives";

    @Autowired
    HBaseDao hBaseDao;

    @ApiOperation(value = "搜索档案ID", notes = "根据机构代码搜索档案")
    @RequestMapping(value = "/legacy_profile", method = RequestMethod.GET)
    public String[] searchProfile(
            @ApiParam(value = "organization_code", defaultValue = "test001_test00000001")
            @RequestParam("organization_code") String organizationCode,
            HttpServletRequest request,
            HttpServletResponse response) throws Throwable {
        String[] rowKeys = hBaseDao.findRowKeys(Table, "^" + organizationCode);

        pagedResponse(request, response, (long) rowKeys.length, 1, rowKeys.length);

        return rowKeys;
    }

    @ApiOperation(value = "删除机构档案", notes = "根据机构代码删除档案")
    @RequestMapping(value = "/legacy_profile", method = RequestMethod.DELETE)
    public void deleteProfile(
            @ApiParam(value = "organization_code", defaultValue = "test001_test00000001")
            @RequestParam("organization_code") String organizationCode) throws Throwable {
        ObjectMapper objectMapper = SpringContext.getService("objectMapper");

        String[] rowKeys = hBaseDao.findRowKeys(Table, "^" + organizationCode);
        TableBundle bundle = new TableBundle();
        bundle.addRows(rowKeys);

        for (String rowKey : rowKeys){
            bundle.clear();
            bundle.addColumns(rowKey, "basic", "data_sets");

            Object[] objects = hBaseDao.get(Table, bundle);
            for (Object object : objects){
                ResultUtil result = new ResultUtil(object);
                String dataSets = result.getCellValue("basic", "data_sets", "");

                ObjectNode root = (ObjectNode) objectMapper.readTree(dataSets);
                Iterator<String> tables = root.fieldNames();
                while (tables.hasNext()){
                    String table = tables.next();
                    String[] dataSetRowKeys = root.get(table).asText().split(",");

                    bundle.clear();
                    bundle.addRows(dataSetRowKeys);
                    hBaseDao.delete(table, bundle);
                }
            }

            bundle.clear();
            bundle.addRows(rowKey);
            hBaseDao.delete(Table, bundle);
        }
    }
}