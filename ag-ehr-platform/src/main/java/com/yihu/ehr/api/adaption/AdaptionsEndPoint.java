package com.yihu.ehr.api.adaption;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.feign.AdapterDispatchClient;
import com.yihu.ehr.feign.SecurityClient;
import com.yihu.ehr.feign.StandardDispatchClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * 标准适配。标准适配分为两个方面：机构数据标准与适配方案。前者用于定义医疗机构信息化系统的数据视图，
 * 后者根据此视图与平台的健康档案数据标准进行匹配与映射。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.02.03 14:15
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/adaptions")
@Api(protocols = "https", value = "adaptions", description = "标准适配服务")
public class AdaptionsEndPoint {
    @Autowired
    private SecurityClient securityClient;

    @Autowired
    private StandardDispatchClient standardDispatchClient;

    @Autowired
    private AdapterDispatchClient adapterDispatchClient;

    @RequestMapping(value = "/{org_code}", method = RequestMethod.GET)
    @ApiOperation(value = "获取机构最新适配", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, notes = "此包内容包含：平台标准，机构标准与二者适配")
    public ResponseEntity<String> getOrgAdaption(
            @ApiParam(name = "org_code", value = "机构编码")
            @PathVariable(value = "org_code") String orgCode,
            HttpServletResponse response) throws Exception {
        try {
            InputStream is = null;
            if (is == null) return new ResponseEntity<>((String) null, HttpStatus.NOT_FOUND);

            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader("Content-Disposition", "attachment; filename=" + orgCode + ".zip");

            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException ex) {
            throw new ApiException(HttpStatus.NOT_ACCEPTABLE, ErrorCode.SystemError,
                    "Cannot download adaption package from server. " + ex.getMessage());
        } catch (Exception ex) {
            throw new ApiException(HttpStatus.NOT_ACCEPTABLE, ErrorCode.SystemError,
                    "Cannot download adaption package from server. " + ex.getMessage());
        }

        return new ResponseEntity<>((String) null, HttpStatus.OK);
    }
}
