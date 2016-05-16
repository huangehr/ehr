package com.yihu.ehr.profile.controller.indices;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.profile.persist.ProfileIndices;
import com.yihu.ehr.profile.persist.ProfileIndicesService;
import com.yihu.ehr.profile.persist.ProfileService;
import com.yihu.ehr.util.DateTimeUtils;
import com.yihu.ehr.util.controller.BaseRestEndPoint;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
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
    @Autowired
    ProfileIndicesService indicesService;

    @ApiOperation(value = "搜索档案", notes = "返回符合条件的档案列表")
    @RequestMapping(value = ServiceApi.HealthProfile.Profiles, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
    public Collection<ProfileIndices> searchProfile(
            @ApiParam(value = "搜索参数")
            @RequestParam("filter") String filter,
            @ApiParam(value = "起始日期", defaultValue = "2015-10-01")
            @RequestParam("since") @DateTimeFormat(pattern = "yyyy-MM-dd") Date since,
            @ApiParam(value = "结束日期", defaultValue = "2016-10-01")
            @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd") Date to,
            @ApiParam(value = "页码", defaultValue = "1")
            @RequestParam("to") int page,
            @ApiParam(value = "页大小", defaultValue = "15")
            @RequestParam("to") int size,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {



        Page<ProfileIndices> profileIndices = searchProfile(query, since, to, page, size);
        if (profileIndices == null || profileIndices.getContent().isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "No profile found with this query!");
        }

        pagedResponse(request, response, (long) (profileIndices.getContent().size()), 1, 100);

        return profileIndices.getContent();
    }

    public Page<ProfileIndices> searchProfile(MProfileSearch query, Date since, Date to, int page, int size) throws ParseException {
        String demographicId = query.getDemographicId();
        String orgCode = query.getOrganizationCode();
        String patientId = query.getPatientId();
        String eventNo = query.getEventNo();
        String name = query.getName();
        String telephone = query.getTelephone();
        String gender = query.getGender();
        Date birthday = DateTimeUtils.simpleDateParse(query.getBirthday());

        Pageable pageable = new PageRequest(page, size);
        Page<ProfileIndices> profileIndices = indicesService.findByOrganizationIndices(orgCode, patientId, eventNo, since, to, pageable);
        if (profileIndices == null || profileIndices.getContent().isEmpty()) {
            profileIndices = indicesService.findByDemographic(demographicId, orgCode, name, telephone, gender, birthday, since, to, pageable);
        }

        return profileIndices;
    }
}
