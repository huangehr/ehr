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
 * 此功能有毒！
 *
 * @author Sand
 * @created 2016.05.25 10:09
 */
/*@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "历史档案服务", description = "维护/获取历史档案")*/
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
            @RequestParam("organization_code") String organizationCode,
            @ApiParam(value = "data_sets", defaultValue = "HDSA00_01,HDSC01_02,HDSC01_03,HDSC01_04,HDSC01_05,HDSC01_06,HDSC02_09,HDSC02_17,HDSC02_03,HDSC02_05,HDSC02_07,HDSC02_08,HDSC02_11,HDSC02_12,HDSC02_14,HDSC02_15,HDSC02_16,HDSD00_08,HDSD01_01,HDS01_02,HDSD02_01,HDSD02_02,HDSD02_03,HDSD02_04,HDSD02_05,HDSF00_01")
            @RequestParam("data_sets") String dataSets) throws Throwable {

        String tables[] = dataSets.split(",");

        TableBundle bundle = new TableBundle();
        for (String table : tables){
            String[] rowKeys = hBaseDao.findRowKeys(table, "^" + organizationCode);
            if (rowKeys != null){
                bundle.clear();
                bundle.addRows(rowKeys);
                hBaseDao.delete(table, bundle);
            }

            table = table + "_ORIGIN";

            rowKeys = hBaseDao.findRowKeys(table, "^" + organizationCode);
            if (rowKeys != null){
                bundle.clear();
                bundle.addRows(rowKeys);
                hBaseDao.delete(table, bundle);
            }
        }

        String[] rowKeys = hBaseDao.findRowKeys(Table, "^" + organizationCode);
        if (rowKeys != null){
            bundle.clear();
            bundle.addRows(rowKeys);
            hBaseDao.delete(Table, bundle);
        }
    }
}