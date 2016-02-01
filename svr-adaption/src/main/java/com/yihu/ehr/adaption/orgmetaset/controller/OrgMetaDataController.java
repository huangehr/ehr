package com.yihu.ehr.adaption.orgmetaset.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.adaption.orgmetaset.service.OrgMetaData;
import com.yihu.ehr.adaption.orgmetaset.service.OrgMetaDataManager;
import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.constrant.Result;
import com.yihu.ehr.util.controller.BaseRestController;
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
@RequestMapping(ApiVersionPrefix.CommonVersion + "/orgmetadata")
@Api(protocols = "https", value = "orgmetadata", description = "��������Ԫ����ӿ�", tags = {"��������Ԫ"})
public class OrgMetaDataController extends BaseRestController {

    @Autowired
    private OrgMetaDataManager orgMetaDataManager;


    /**
     * ����id��ѯʵ��
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    @ApiOperation(value = "����id��ѯʵ��")
    public Object getOrgMetaData(
            @ApiParam(name = "id", value = "���", defaultValue = "")
            @RequestParam(value = "id") long id) {
        Result result = new Result();
        try {
            OrgMetaData orgMetaData = orgMetaDataManager.getOrgMetaData(id);
            result.setObj(orgMetaData);
            result.setSuccessFlg(true);
        } catch (Exception ex) {
            result.setSuccessFlg(false);
        }
        return result;
    }


    /**
     * ������������Ԫ
     *
     * @param orgDataSetSeq
     * @param code
     * @param name
     * @param description
     * @param userId
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ApiOperation(value = "������������Ԫ")
    public Object createOrgMetaData(
            @ApiParam(name = "api_version", value = "API�汾��", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "orgDataSetSeq", value = "orgDataSetSeq", defaultValue = "")
            @RequestParam(value = "orgDataSetSeq") int orgDataSetSeq,
            @ApiParam(name = "orgCode", value = "orgCode", defaultValue = "")
            @RequestParam(value = "orgCode") String orgCode,
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam(value = "id") long id,
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "description", value = "description", defaultValue = "")
            @RequestParam(value = "description") String description,
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId) {

        Result result = new Result();
        try {
            boolean isExist = orgMetaDataManager.isExistOrgMetaData(orgDataSetSeq, orgCode, code);   //�ظ�У��

            if (isExist) {
                result.setSuccessFlg(false);
                result.setErrorMsg("������Ԫ�Ѵ��ڣ�");
                return false;
            }
            OrgMetaData orgMetaData = new OrgMetaData();
            orgMetaData.setCode(code);
            orgMetaData.setName(name);
            orgMetaData.setOrgDataSet(orgDataSetSeq);
            orgMetaData.setCreateDate(new Date());
            orgMetaData.setCreateUser(userId);
            orgMetaData.setOrganization(orgCode);
            orgMetaData.setDescription(description);
            orgMetaDataManager.createOrgMetaData(orgMetaData);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }


    /**
     * ɾ����������Ԫ
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.DELETE)
    @ApiOperation(value = "ɾ����������Ԫ")
    public Object deleteOrgMetaData(
            @ApiParam(name = "id", value = "���", defaultValue = "")
            @RequestParam(value = "id") long id) {

        Result result = new Result();
        try {
            orgMetaDataManager.deleteOrgMetaData(id);
            return true;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg("ɾ������Ԫʧ�ܣ�");
            return false;
        }
    }

    /**
     * ����ɾ����������Ԫ
     *
     * @param ids
     * @return
     */
    @RequestMapping(value = "/batch", method = RequestMethod.DELETE)
    @ApiOperation(value = "����ɾ����������Ԫ")
    public Object deleteOrgMetaDataList(
            @ApiParam(name = "ids", value = "��ż�", defaultValue = "")
            @RequestParam(value = "ids[]") Long[] ids) {

        Result result = new Result();
        if (ids == null || ids.length == 0)
            return true;

        try {
            orgMetaDataManager.deleteOrgMetaDataList(ids);
            return true;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg("ɾ������Ԫʧ�ܣ�");
            return false;
        }
    }

    /**
     * �޸Ļ�������Ԫ
     *
     * @param apiVersion
     * @param orgDataSetSeq
     * @param orgCode
     * @param id
     * @param code
     * @param name
     * @param description
     * @param userId
     * @return
     */
    @RequestMapping(value = "/orgMetaData", method = RequestMethod.PUT)
    @ApiOperation(value = "�޸Ļ�������Ԫ")
    public Object updateOrgMetaData(
            @ApiParam(name = "api_version", value = "API�汾��", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "orgDataSetSeq", value = "orgDataSetSeq", defaultValue = "")
            @RequestParam(value = "orgDataSetSeq") Integer orgDataSetSeq,
            @ApiParam(name = "orgCode", value = "orgCode", defaultValue = "")
            @RequestParam(value = "orgCode") String orgCode,
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam(value = "id") long id,
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "description", value = "description", defaultValue = "")
            @RequestParam(value = "description") String description,
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId) {

        Result result = new Result();
        try {
            OrgMetaData orgMetaData = orgMetaDataManager.getOrgMetaData(id);
            if (orgMetaData == null) {
                result.setSuccessFlg(false);
                result.setErrorMsg("������Ԫ�����ڣ�");
                return false;
            } else {
                //�ظ�У��
                boolean updateFlg = orgMetaData.getCode().equals(code) || !orgMetaDataManager.isExistOrgMetaData(orgDataSetSeq, orgCode, code);
                if (updateFlg) {
                    orgMetaData.setCode(code);
                    orgMetaData.setName(name);
                    orgMetaData.setDescription(description);
                    orgMetaData.setUpdateDate(new Date());
                    orgMetaData.setUpdateUser(userId);
                    orgMetaData.setOrganization(orgCode);
                    orgMetaDataManager.updateOrgMetaData(orgMetaData);
                    return true;
                }
                return false;
            }
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg("�޸�����Ԫʧ�ܣ�");
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
    public Object searchOrgMetaDatas(
            @ApiParam(name = "parmJson", value = "��ѯ����", defaultValue = "")
            @RequestParam(value = "parmJson", required = false) String parmJson) {
        Result result = new Result();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            PageModel pageModel = objectMapper.readValue(parmJson, PageModel.class);
            pageModel.setModelClass(OrgMetaData.class);
            List<OrgMetaData> orgMetaDatas = orgMetaDataManager.searchOrgMetaDatas(pageModel);
            Integer totalCount = orgMetaDataManager.searchTotalCount(pageModel);
            result = getResult(orgMetaDatas, totalCount, pageModel.getPage(), pageModel.getRows());
            result.setSuccessFlg(true);
        } catch (Exception ex) {
            result.setSuccessFlg(false);
        }
        return result;
    }


}
