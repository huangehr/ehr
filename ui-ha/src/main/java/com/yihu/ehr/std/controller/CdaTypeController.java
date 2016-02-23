package com.yihu.ehr.std.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constants.RestAPI;
import com.yihu.ehr.constants.SessionAttributeKeys;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.HttpClientUtil;
import com.yihu.ehr.util.ResourceProperties;
import com.yihu.ehr.util.controller.BaseRestController;
import com.yihu.ehr.util.log.LogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.*;

/**
 * Created by AndyCai on 2015/12/14.
 */
@RequestMapping("/cdatype")
@Controller(RestAPI.StandardCDATypeController)
@SessionAttributes(SessionAttributeKeys.CurrentUser)
public class CdaTypeController extends BaseRestController {
    private static   String host = "http://"+ ResourceProperties.getProperty("serverip")+":"+ResourceProperties.getProperty("port");
    private static   String username = ResourceProperties.getProperty("username");
    private static   String password = ResourceProperties.getProperty("password");
    private static   String module = ResourceProperties.getProperty("module");
    private static   String version = ResourceProperties.getProperty("version");
    private static   String comUrl = host + module + version;

    @RequestMapping("index")
    public String cdaTypeInitial(Model model) {
        model.addAttribute("contentPage", "std/cdaType/index");
        return "pageView";
    }

    @RequestMapping("typeupdate")
    public String typeupdate(Model model, @ModelAttribute(SessionAttributeKeys.CurrentUser) XUser user) {
        model.addAttribute("User", user);
        model.addAttribute("contentPage", "std/cdaType/CdaTypeDetail");
        return "generalView";
    }

    @RequestMapping("getTreeGridData")
    @ResponseBody
    public String getTreeGridData() {
        String url = "/cdaType/getTreeGridData";
        String strResult = "";
        try{
            strResult = HttpClientUtil.doGet(comUrl+url,username,password);
        }catch(Exception ex){
            LogService.getLogger(CdaTypeController.class).error(ex.getMessage());
        }
        return strResult;

       /* try {
            List<XCDAType> listType = xcdaTypeManager.getCDATypeListByParentId("");
            if (listType != null) {
                List<CDATypeTreeModel> listTree = getCdaTypeChild(listType);
                ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
                strResult = objectMapper.writeValueAsString(listTree);
            }
        } catch (Exception ex) {
            LogService.getLogger(CdaTypeController_w.class).error(ex.getMessage());
        }
        return strResult;*/
    }

//    /**
//     * ���ݸ�����Ϣ��ȡȫ�����Ӽ���Ϣ
//     * @param info ������Ϣ
//     * @return ȫ���Ӽ���Ϣ
//     */
//    public List<CDATypeTreeModel> getCdaTypeChild(List<XCDAType> info) {
//        List<CDATypeTreeModel> treeInfo = new ArrayList<>();
//        try {
//            for (int i = 0; i < info.size(); i++) {
//                CDAType typeInfo = (CDAType) info.get(i);
//                CDATypeTreeModel tree = new CDATypeTreeModel();
//                tree.setId(typeInfo.getId());
//                tree.setCode(typeInfo.getCode());
//                tree.setName(typeInfo.getName());
//                tree.setDescription(typeInfo.getDescription());
//                List<XCDAType> listChild = xcdaTypeManager.getCDATypeListByParentId(typeInfo.getId());
//                List<CDATypeTreeModel> listChildTree = getCdaTypeChild(listChild);
//                tree.setChildren(listChildTree);
//                treeInfo.add(tree);
//            }
//        } catch (Exception ex) {
//            LogService.getLogger(CdaTypeController_w.class).error(ex.getMessage());
//        }
//        return treeInfo;
//    }

//    /**
//     * ���ݸ�������ȡȫ���������ID������ֵ����������ID
//
//     * @return ȫ���Ӽ�
//     */
//    public String getCdaTypeChildId(List<XCDAType> info,String childrenIds) {
//        try {
//            for (int i = 0; i < info.size(); i++) {
//                CDAType typeInfo = (CDAType) info.get(i);
//                childrenIds+=typeInfo.getId()+",";
//                List<XCDAType> listChild = xcdaTypeManager.getCDATypeListByParentId(typeInfo.getId());
//                if(listChild.size()>0)
//                    childrenIds=getCdaTypeChildId(listChild,childrenIds);
//            }
//        } catch (Exception ex) {
//            LogService.getLogger(CdaTypeController_w.class).error(ex.getMessage());
//        }
//        return childrenIds;
//    }

    @RequestMapping("GetCdaTypeListByKey")
    @ResponseBody
    public Object GetCdaTypeListByKey(String strKey, Integer page, Integer rows) {
        // TODO �޶�Ӧ
        Envelop result = new Envelop();
        String url = "/cdaType/***********";
        try{
            Map<String,Object> params = new HashMap<>();
            params.put("strKey",strKey);
            params.put("page",page);
            params.put("rows",rows);
            String _rus = HttpClientUtil.doGet(comUrl+url,params,username,password);
            if(StringUtils.isEmpty(_rus)){
                result.setSuccessFlg(false);
                result.setErrorMsg("cda����ȡʧ��");
            }else{
                //result.setSuccessFlg(true);
                return _rus;
            }
        }catch (Exception ex){
            LogService.getLogger(CdaTypeController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;

        /*Result result = new Result();
        try {
            Map<String, Object> mapKey = new HashMap<>();
            mapKey.put("key", strKey);
            mapKey.put("page", page);
            mapKey.put("rows", rows);
            List<XCDAType> listType = xcdaTypeManager.getCDATypeListByKey(mapKey);
            if (listType == null) {
                result.setSuccessFlg(false);
                result.setErrorMsg("���ݻ�ȡʧ��!");
                return result;
            }
            List<CDATypeForInterface> listInfo = getTypeForInterface(listType);
            if(rows<=0)
            {
                rows=1;
            }
            result = getResult(listInfo, 1, page, rows);
        } catch (Exception ex) {
            LogService.getLogger(CdaTypeController_w.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg("ϵͳ��������ϵ����Ա!");
        }
        return result;*/
    }

    @RequestMapping("getCdaTypeById")
    @ResponseBody
    public Object getCdaTypeById(String strIds) {
        Envelop result = new Envelop();
        String url = "/cdaType/cdaType";
        try{
            Map<String,Object> params = new HashMap<>();
            params.put("typeId",strIds);
            String _rus = HttpClientUtil.doGet(comUrl+url,params,username,password);
            if(StringUtils.isEmpty(_rus)){
                result.setSuccessFlg(false);
                result.setErrorMsg("cda����ȡʧ��");
            }else{
                result.setSuccessFlg(true);
                result.setObj(_rus);
            }
        }catch (Exception ex){
            LogService.getLogger(CdaTypeController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;

       /* Result result = new Result();
        result.setSuccessFlg(false);
        try {
            List<XCDAType> listType = xcdaTypeManager.getCdatypeInfoByIds(strIds);
            if (listType.size() > 0) {
                result.setSuccessFlg(true);
                List<CDATypeForInterface> listInfo = getTypeForInterface(listType);
                result.setObj(listInfo.get(0));
            }
        } catch (Exception ex) {
            LogService.getLogger(CdaController.class).error(ex.getMessage());
            result.setErrorMsg("ϵͳ��������ϵ����Ա!");
        }
        return result;*/
    }

    @RequestMapping("delteCdaTypeInfo")
    @ResponseBody
    /**
     * ɾ��CDA�����������������𣬽�һ��ɾ�������
     * �ȸ��ݵ�ǰ�����ID��ȡȫ�������ID���ٽ���ɾ��
     * @param  ids  cdaType Id
     *  @return result �������
     */
    public Object delteCdaTypeInfo(String ids) {
        Envelop result = new Envelop();
        String url = "/cdaType/cdaType";
        if (StringUtils.isEmpty(ids)){
            result.setErrorMsg("��ѡ��Ҫɾ��������");
            result.setSuccessFlg(false);
            return result;
        }
        try{
            Map<String,Object> params = new HashMap<>();
            params.put("typeId",ids);
            String _msg = HttpClientUtil.doDelete(comUrl + url, params, username, password);
            if(Boolean.parseBoolean(_msg)){
                result.setSuccessFlg(true);
            }else{
                result.setSuccessFlg(false);
                result.setErrorMsg("cda���ɾ��ʧ��");
            }
        }catch (Exception ex){
            LogService.getLogger(CdaTypeController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;


       /* try {
            String strErrorMsg = "";
            if (ids == null || ids == "") {
                strErrorMsg += "����ѡ��Ҫɾ�������ݣ�";
            }
            if (strErrorMsg != "") {
                result.setSuccessFlg(false);
                result.setErrorMsg(strErrorMsg);
            }
            List<XCDAType> listParentType = xcdaTypeManager.getCdatypeInfoByIds(ids);
            String childrenIds = getCdaTypeChildId(listParentType, "");
            if(childrenIds.length()>0) {
                childrenIds = childrenIds.substring(0, childrenIds.length() - 1);
            }
            boolean reault = xcdaTypeManager.deleteCdaType(childrenIds);
            if (reault) {
                result.setSuccessFlg(true);
            } else {
                result.setSuccessFlg(false);
                result.setErrorMsg("ɾ��ʧ��!");
            }
        } catch (Exception ex) {
            LogService.getLogger(CdaController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg("ϵͳ��������ϵ����Ա!");
        }
        return result;*/
    }

    @RequestMapping("SaveCdaType")
    @ResponseBody
    public Object SaveCdaType(String info) {
        Envelop result = new Envelop();
        String url = "/cdaType/SaveCdaType";
        //todo��ǰ̨js�жϲ���Ϊ��
//        if(StringUtil.isEmpty(info.getCode())){
//            strErrorMsg += "���벻��Ϊ��! ";
//        }
//        if(StringUtil.isEmpty(info.getParentName())){
//            strErrorMsg += "���Ʋ���Ϊ�գ�";
//        }
//        if(!StringUtil.isEmpty(strErrorMsg)){
//            result.setSuccessFlg(false);
//            result.setErrorMsg(strErrorMsg);
//            return result.toJson();
//        }
        try {
            //TODO �ṩcodeΨһ����֤api
            Map<String, Object> params = new HashMap<>();
            params.put("typeJson", info);
            String _rus = HttpClientUtil.doPost(comUrl+url,params,username,password);
            if(StringUtils.isEmpty(_rus)){
                result.setSuccessFlg(false);
                result.setErrorMsg("cadtype����ʧ��");
            }else{
                result.setSuccessFlg(true);
            }
        } catch (Exception ex){
            LogService.getLogger(CdaTypeController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return result;


       /* try {
            String strErrorMsg = "";
            if (info.getCode() == null || info.getCode() == "") {
                strErrorMsg += "���벻��Ϊ��!";
            }
            if (info.getName() == null || info.getName() == "") {
                strErrorMsg += "���Ʋ���Ϊ��!";
            }
            XCDAType xcdaType = new CDAType();
            //id ��Ϊ�����Ȼ�ȡ���ݣ��ٽ����޸�
            if (!StringUtil.isEmpty(info.getId())) {
                List<XCDAType> listType = xcdaTypeManager.getCdatypeInfoByIds(info.getId());
                if (listType.size() > 0) {
                    xcdaType = listType.get(0);
                }
                if(!info.getCode().equals(xcdaType.getCode()) && xcdaTypeManager.isCodeExist(info.getCode())){
                    strErrorMsg = "�����Ѵ���!";
                }
                xcdaType.setUpdateUser(info.getUserId());
                xcdaType.setUpdateDate(new Date());
            } else {
                if(xcdaTypeManager.isCodeExist(info.getCode())){
                    strErrorMsg = "�����Ѵ���!";
                }
                xcdaType.setCreateUser(info.getUserId());
                xcdaType.setCreateDate(new Date());
            }
            xcdaType.setCode(info.getCode());
            xcdaType.setName(info.getName());
            xcdaType.setParentId(info.getParentId());
            xcdaType.setDescription(info.getDescription());
            if (!strErrorMsg.equals("")) {
                result.setSuccessFlg(false);
                result.setErrorMsg(strErrorMsg);
                return result;
            }
            boolean iResult = false;
            if (info.getId() == "") {
                XEnvironmentOption environmentOption = ServiceFactory.getService(Services.EnvironmentOption);
                Object objectID = new ObjectId(Short.parseShort(environmentOption.getOption(EnvironmentOptions.AdminRegion)), BizObject.StdArchive);
                xcdaType.setId(objectID.toString());
                iResult = xcdaTypeManager.insertCdaType(xcdaType);
            } else {
                iResult = xcdaTypeManager.updateCdaType(xcdaType);
            }
            if (iResult) {
                result.setSuccessFlg(true);
            } else {
                result.setSuccessFlg(false);
                result.setErrorMsg("����ʧ��!");
            }
        } catch (Exception ex) {
            LogService.getLogger(CdaController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg("ϵͳ��������ϵ����Ա!");
        }
        return result;*/
    }

    @RequestMapping("getParentType")
    @ResponseBody
    public String getParentType(String strId, String key) {
        String url = "/cdaType/getParentType";
        String _strResult = "";
        try{
            Map<String,Object> params = new HashMap<>();
            params.put("typeId",strId);
            params.put("code",key);
            params.put("name",key);
            _strResult = HttpClientUtil.doGet(comUrl+url,params,username,password);
        }catch(Exception ex){
            LogService.getLogger(CdaTypeController.class).error(ex.getMessage());
        }
        return _strResult;

       /*String strResult = "";
        try {
            List<XCDAType> listParentType = xcdaTypeManager.getCdatypeInfoByIds(strId);
            String childrenIds = getCdaTypeChildId(listParentType,"");
            if(childrenIds.length()>0) {
                childrenIds = childrenIds.substring(0, childrenIds.length() - 1);
            }
            List<XCDAType> listType = xcdaTypeManager.getParentType(childrenIds,key);
            if (listType != null) {
                List<CDATypeForInterface> listInfo = getTypeForInterface(listType);
                ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
                strResult = objectMapper.writeValueAsString(listInfo);
            }
        } catch (Exception ex) {
            LogService.getLogger(CdaTypeController_w.class).error(ex.getMessage());
        }
        return strResult;*/
    }

    @RequestMapping("getCDATypeListByParentId")
    @ResponseBody
    public String getCDATypeListByParentId(String ids) {
        String url = "/cdaType/getCDATypeListByParentId";
        String _strResult = "";
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("typeId", ids);
            _strResult = HttpClientUtil.doGet(comUrl + url, params, username, password);
        } catch (Exception ex) {
            LogService.getLogger(CdaTypeController.class).error(ex.getMessage());
        }
        return _strResult;
    }

/*        String strResult = "";
        try {
            List<XCDAType> listType = xcdaTypeManager.getCDATypeListByParentId(ids);
            List<CDATypeForInterface> cdaTypeModels = getTypeForInterface(listType);
            if (listType != null) {
                ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
                strResult = objectMapper.writeValueAsString(cdaTypeModels);
            }
        } catch (Exception ex) {
                LogService.getLogger(CdaController.class).error(ex.getMessage());
        }
        return strResult;
    }*/


//    public List<CDATypeForInterface> getTypeForInterface(List<XCDAType> listType) {
//        if (listType == null) {
//            return null;
//        }
//        List<CDATypeForInterface> listInfo = new ArrayList<>();
//        for (int i = 0; i < listType.size(); i++) {
//            CDAType cdaType = (CDAType) listType.get(i);
//            CDATypeForInterface info = new CDATypeForInterface();
//            info.setId(cdaType.getId());
//            info.setCode(cdaType.getCode());
//            info.setName(cdaType.getName());
//            info.setParentId(cdaType.getParentId());
//            String strParentName = "";
//            if (cdaType.getParentId() != null && !cdaType.getParentId().equals("")) {
//                strParentName = cdaType.getParentCdaType()!=null?cdaType.getParentCdaType().getName():"";
//            }
//            info.setParentName(strParentName);
//            info.setDescription(cdaType.getDescription());
//            listInfo.add(info);
//        }
//        return listInfo;
//    }
}
