package com.yihu.ehr.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.model.family.MMembers;
import com.yihu.ehr.service.Families;
import com.yihu.ehr.service.FamiliesService;
import com.yihu.ehr.service.Members;
import com.yihu.ehr.service.MembersService;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "members", description = "家庭成员管理接口")
public class MembersController extends BaseRestController {

    @Autowired
    private MembersService membersService;

    @RequestMapping(value = "/members", method = RequestMethod.GET)
    @ApiOperation(value = "获取家庭成员列表")
    public Collection<MMembers> searchMembers(
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
            HttpServletRequest request,
            HttpServletResponse response) throws ParseException {

        if(StringUtils.isEmpty(filters))
        {
            Page<Members> members = membersService.getMembers(sorts,reducePage(page),size);
            pagedResponse(request,response,members.getTotalElements(),page,size);
            return convertToModels(members.getContent(),new ArrayList<>(members.getNumber()),MMembers.class,fields);
        }
        else
        {
            List<Members> members = membersService.search(fields,filters,sorts,page,size);
            pagedResponse(request,response,membersService.getCount(filters),page,size);
            return convertToModels(members,new ArrayList<>(members.size()),MMembers.class,fields);
        }
    }

    @RequestMapping(value = "/families/{families_id}/members", method = RequestMethod.POST)
    @ApiOperation(value = "创建家庭成员")
    public MMembers createMember(
            @ApiParam(name = "families_id", value = "家庭关系ID", defaultValue = "")
            @PathVariable(value = "families_id") String familiesId,
            @ApiParam(name = "json_data", value = "", defaultValue = "")
            @RequestParam(value = "json_data") String jsonData) throws Exception {

        Members mb = toEntity(jsonData,Members.class);
        mb.setId(getObjectId(BizObject.Members));
        mb.setFamilyId(familiesId);
        membersService.createMembers(mb);
        return convertToModel(mb,MMembers.class);
    }

    @RequestMapping(value = "/families/{families_id}/members", method = RequestMethod.PUT)
    @ApiOperation(value = "修改家庭成员")
    public MMembers updateMember(
            @ApiParam(name = "families_id", value = "家庭关系ID", defaultValue = "")
            @PathVariable(value = "families_id") String familiesId,
            @ApiParam(name = "json_data", value = "", defaultValue = "")
            @RequestParam(value = "json_data") String jsonData) throws Exception {

        Members mb = toEntity(jsonData,Members.class);
        mb.setFamilyId(familiesId);
        membersService.updateMembers(mb);
        return convertToModel(mb,MMembers.class);
    }

    @RequestMapping(value = "/families/{families_id}/members/{id_card_no}", method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取家庭成员")
    public MMembers getMember(
            @ApiParam(name = "families_id", value = "家庭关系ID", defaultValue = "")
            @PathVariable(value = "families_id") String familiesId,
            @ApiParam(name = "id_card_no", value = "", defaultValue = "")
            @PathVariable(value = "id_card_no") String id_card_no) {

        Members mb = membersService.getMembers(familiesId,id_card_no);
        return convertToModel(mb,MMembers.class);
    }

    @RequestMapping(value = "/families/{families_id}/members/{id_card_no}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除家庭成员")
    public boolean deleteMember(
            @ApiParam(name = "families_id", value = "家庭关系ID", defaultValue = "")
            @PathVariable(value = "families_id") String familiesId,
            @ApiParam(name = "id_card_no", value = "用户编号", defaultValue = "")
            @PathVariable(value = "id_card_no") String id_card_no) throws Exception {

        membersService.deleteMembers(familiesId,id_card_no);
        return true;
    }


}