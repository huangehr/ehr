package com.yihu.ehr.adaption.orgdictitem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.adaption.orgdictitem.service.OrgDictItem;
import com.yihu.ehr.adaption.orgdictitem.service.OrgDictItemManager;
import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.constrant.Result;
import com.yihu.ehr.util.controller.BaseRestController;
import com.yihu.ehr.util.operator.StringUtil;
import com.yihu.ehr.util.parm.PageModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Created by lincl on 2016.1.29
 */
@RestController
@RequestMapping(ApiVersionPrefix.CommonVersion + "/orgdictitem")
@Api(protocols = "https", value = "orgdictitem", description = "�����ֵ������ӿ�", tags = {"�����ֵ���"})
public class OrgDictItemController extends BaseRestController {

    @Autowired
    private OrgDictItemManager orgDictItemManager;

    /**
     * ����id��ѯʵ��
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ApiOperation(value = "����id��ѯʵ��")
    public Object getOrgDictItem(
            @ApiParam(name = "id", value = "��ѯ����", defaultValue = "")
            @RequestParam(value = "id", required = false) long id) {
        Result result = new Result();
        try {
            OrgDictItem orgDictItem = orgDictItemManager.getOrgDictItem(id);
            result.setObj(orgDictItem);
            result.setSuccessFlg(true);
        } catch (Exception ex) {
            result.setSuccessFlg(false);
        }
        return result;
    }


    /**
     * ���������ֵ�����
     *
     * @param apiVersion
     * @param orgDictSeq
     * @param orgCode
     * @param code
     * @param name
     * @param description
     * @param sort
     * @param userId
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ApiOperation(value = "���������ֵ�����")
    public Object createOrgDictItem(
            @ApiParam(name = "api_version", value = "API�汾��", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "orgDictSeq", value = "orgDictSeq", defaultValue = "")
            @RequestParam(value = "orgDictSeq") int orgDictSeq,
            @ApiParam(name = "orgCode", value = "orgCode", defaultValue = "")
            @RequestParam(value = "orgCode") String orgCode,
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "description", value = "description", defaultValue = "")
            @RequestParam(value = "description") String description,
            @ApiParam(name = "sort", value = "sort", defaultValue = "")
            @RequestParam(value = "sort") String sort,
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId) {

        Result result = new Result();
        try {
            boolean isExist = orgDictItemManager.isExistOrgDictItem(orgDictSeq, orgCode, code);   //�ظ�У��

            if (isExist) {
                result.setSuccessFlg(false);
                result.setErrorMsg("���ֵ����Ѵ��ڣ�");
                return false;
            }
            OrgDictItem orgDictItem = new OrgDictItem();
            int nextSort;
            if (StringUtil.isEmpty(sort)) {
                nextSort = orgDictItemManager.getNextSort(orgDictSeq);
            } else {
                nextSort = Integer.parseInt(sort);
            }
            orgDictItem.setCode(code);
            orgDictItem.setName(name);
            orgDictItem.setSort(nextSort);
            orgDictItem.setOrgDict(orgDictSeq);
            orgDictItem.setCreateDate(new Date());
            orgDictItem.setCreateUser(userId);
            orgDictItem.setDescription(description);
            orgDictItem.setOrganization(orgCode);
            orgDictItemManager.createOrgDictItem(orgDictItem);
            return true;
        } catch (Exception ex) {
            result.setSuccessFlg(false);
            return false;
        }
    }

    /**
     * ɾ�������ֵ�����
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.DELETE)
    @ApiOperation(value = "ɾ�������ֵ�����")
    public Object deleteOrgDictItem(
            @ApiParam(name = "id", value = "��ѯ����", defaultValue = "")
            @RequestParam(value = "id", required = false) long id) {

        Result result = new Result();
        try {
            orgDictItemManager.deleteOrgDictItem(id);
            return true;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg("ɾ���ֵ���ʧ�ܣ�");
            return false;
        }
    }

    /**
     * ����ɾ�������ֵ�����
     *
     * @param ids
     * @return
     */
    @RequestMapping(value = "/batch", method = RequestMethod.DELETE)
    @ApiOperation(value = "����ɾ�������ֵ�����")
    public Object deleteOrgDictItemList(
            @ApiParam(name = "ids", value = "��ż�", defaultValue = "")
            @RequestParam(value = "ids[]") Long[] ids) {
        Result result = new Result();

        if (ids == null || ids.length == 0) {
            return true;
        } else {
            try {
                orgDictItemManager.deleteOrgDictItemList(ids);
                return true;
            } catch (Exception e) {
                result.setSuccessFlg(false);
                result.setErrorMsg("ɾ���ֵ���ʧ�ܣ�");
                return false;
            }
        }
    }

    /**
     * �޸Ļ����ֵ�����
     *
     * @param apiVersion
     * @param id
     * @param orgDictSeq
     * @param orgCode
     * @param code
     * @param name
     * @param description
     * @param sort
     * @param userId
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ApiOperation(value = "�޸Ļ����ֵ�����")
    public Object updateDictItem(
            @ApiParam(name = "api_version", value = "API�汾��", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam(value = "id") Long id,
            @ApiParam(name = "orgDictSeq", value = "orgDictSeq", defaultValue = "")
            @RequestParam(value = "orgDictSeq") Integer orgDictSeq,
            @ApiParam(name = "orgCode", value = "orgCode", defaultValue = "")
            @RequestParam(value = "orgCode") String orgCode,
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "description", value = "description", defaultValue = "")
            @RequestParam(value = "description") String description,
            @ApiParam(name = "sort", value = "sort", defaultValue = "")
            @RequestParam(value = "sort") String sort,
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId) {

        Result result = new Result();
        try {
            OrgDictItem orgDictItem = orgDictItemManager.getOrgDictItem(id);
            if (orgDictItem == null) {
                result.setSuccessFlg(false);
                result.setErrorMsg("���ֵ�����ڣ�");
                return false;
            } else {
                //�ظ�У��
                boolean updateFlg = orgDictItem.getCode().equals(code) || !orgDictItemManager.isExistOrgDictItem(orgDictSeq, orgCode, code);
                if (updateFlg) {
                    orgDictItem.setCode(code);
                    orgDictItem.setName(name);
                    orgDictItem.setDescription(description);
                    orgDictItem.setUpdateDate(new Date());
                    orgDictItem.setUpdateUser(userId);
                    orgDictItem.setSort(Integer.parseInt(sort));
                    orgDictItem.setOrganization(orgCode);
                    orgDictItemManager.updateOrgDictItem(orgDictItem);
                    return true;
                }
                result.setSuccessFlg(false);
                result.setErrorMsg("���ֵ����Ѵ��ڣ�");
                return false;
            }
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg("�޸��ֵ���ʧ�ܣ�");
            return false;
        }
    }


    /**
     * ������ѯ
     *
     * @param parmJson
     * @return
     */
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    @ApiOperation(value = "������ѯ")
    public Object searchOrgDictItems(
            @ApiParam(name = "parmJson", value = "��ѯ����", defaultValue = "")
            @RequestParam(value = "parmJson", required = false) String parmJson) {
        Result result = new Result();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            PageModel pageModel = objectMapper.readValue(parmJson, PageModel.class);
            pageModel.setModelClass(OrgDictItem.class);
            List<OrgDictItem> detailModelList = orgDictItemManager.searchOrgDictItems(pageModel);
            Integer totalCount = orgDictItemManager.searchTotalCount(pageModel);
            result = getResult(detailModelList, totalCount, pageModel.getPage(), pageModel.getRows());
            result.setSuccessFlg(true);
        } catch (Exception ex) {
            result.setSuccessFlg(false);
        }
        return result;
    }
}
