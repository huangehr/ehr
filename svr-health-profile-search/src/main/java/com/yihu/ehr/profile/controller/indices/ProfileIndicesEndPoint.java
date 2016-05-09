package com.yihu.ehr.profile.controller.indices;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.profile.persist.ProfileIndices;
import com.yihu.ehr.profile.persist.ProfileService;
import com.yihu.ehr.util.controller.BaseRestEndPoint;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Date;

/**
 * 档案接口。提供就诊数据的原始档案，以CDA文档配置作为数据内容架构。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.12.26 16:08
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "健康档案服务", description = "提供档案搜索及完整档案下载")
public class ProfileIndicesEndPoint extends BaseRestEndPoint {
    private final static String SampleQuery = "{\n" +
            "\"demographicId\": \"412726195111306268\",\n" +
            "\"organizationCode\": \"41872607-9\",\n" +
            "\"patientId\": \"10295435\",\n" +
            "\"eventNo\": \"000622450\",\n" +
            "\"name\": \"段廷兰\",\n" +
            "\"telephone\": \"11\",\n" +
            "\"gender\": \"女\",\n" +
            "\"birthday\": \"1951-11-30\"\n" +
            "}";

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ProfileIndicesUtil indicesUtil;

    @ApiOperation(value = "搜索档案", notes = "返回符合条件的档案列表")
    @RequestMapping(value = ServiceApi.HealthProfile.Profiles, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.POST)
    public Collection<Object> searchProfile(
            @ApiParam(value = "搜索参数", defaultValue = SampleQuery)
            @RequestBody MProfileSearch query,
            @ApiParam(value = "起始日期", defaultValue = "2015-10-01")
            @RequestParam("since") @DateTimeFormat(pattern = "yyyy-MM-dd") Date since,
            @ApiParam(value = "结束日期", defaultValue = "2016-10-01")
            @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd") Date to,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        Page<ProfileIndices> profileIndices = indicesUtil.searchProfile(query, since, to);
        if (profileIndices == null || profileIndices.getContent().isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "No profile found with this query!");
        }

        pagedResponse(request, response, (long)(profileIndices.getContent().size()), 1, 100);

        return null;
    }
}
