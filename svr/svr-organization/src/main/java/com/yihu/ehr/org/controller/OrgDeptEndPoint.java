package com.yihu.ehr.org.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.org.MOrgDept;
import com.yihu.ehr.org.service.OrgDeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/15.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "orgDept", description = "组织机构部门管理服务", tags = {"部门管理"})
public class OrgDeptEndPoint {

    @Autowired
    private OrgDeptService orgDeptService;

    @RequestMapping(value = "/orgDept/list", method = RequestMethod.POST)
    @ApiOperation(value = "根据条件查询机构下的部门列表")
    public List<MOrgDept> searchOrgDepts(
            @ApiParam(name = "orgId", value = "机构ID")
            @RequestParam(value = "orgId", required = true) String orgId,
            @ApiParam(name = "sorts", value = "排序 ", defaultValue = "+name")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
//        List<MOrgDept> orgDepts = orgDeptService.search(fields, filters, sorts, page, size);
        return null;
    }




}
