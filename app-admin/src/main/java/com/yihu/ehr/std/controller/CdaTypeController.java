package com.yihu.ehr.std.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.standard.cdatype.CdaTypeDetailModel;
import com.yihu.ehr.agModel.standard.cdatype.CdaTypeModel;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constants.RestAPI;
import com.yihu.ehr.constants.SessionAttributeKeys;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.HttpClientUtil;
import com.yihu.ehr.util.controller.BaseUIController;
import com.yihu.ehr.util.log.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by AndyCai on 2015/12/14.
 */
@RequestMapping("/cdatype")
@Controller(RestAPI.StandardCDATypeController)
@SessionAttributes(SessionAttributeKeys.CurrentUser)
public class CdaTypeController extends BaseUIController{
    @Value("${service-gateway.username}")
    private String username;
    @Value("${service-gateway.password}")
    private String password;
//    @Value("${service-gateway.url}")
//    private String comUrl;

    //TODO 访问路径，一般有aimin而标准部分网关没有admin
    String comUrl = "http://localhost:10000/api/v1.0/cdatype";

    @Autowired
    ObjectMapper objectMapper;

    @RequestMapping("index")
    public String cdaTypeInitial(Model model) {
        model.addAttribute("contentPage", "std/cdaType/index");
        return "pageView";
    }

    @RequestMapping("typeupdate")
    public String typeupdate(Model model,String userId) {
        //TODO 临时测试数据
        userId = "wwcs";
        model.addAttribute("UserId", userId);
        model.addAttribute("contentPage", "std/cdaType/CdaTypeDetail");
        return "generalView";
    }

    @RequestMapping("getTreeGridData")
    @ResponseBody
    //获取TreeData 用于初始页面显示
    public Object getTreeGridData() {
        //TODO 待网关提供接口
        Envelop envelop = new Envelop();
        String url = "/cda_types/cda_types_tree";
        String strResult = "";
        try{
            strResult = HttpClientUtil.doGet(comUrl+url,username,password);
            return strResult;
        }catch(Exception ex){
            LogService.getLogger(CdaTypeController.class).error(ex.getMessage());
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
            return envelop;
        }
    }


    @RequestMapping("GetCdaTypeListByKey")
    @ResponseBody
    public Object GetCdaTypeListByKey(String strKey, Integer page, Integer rows) {
        // TODO  未使用
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
                result.setErrorMsg("cda类别获取失败");
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
                result.setErrorMsg("数据获取失败!");
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
            result.setErrorMsg("系统错误，请联系管理员!");
        }
        return result;*/
    }

    @RequestMapping("getCdaTypeById")
    @ResponseBody
    public Object getCdaTypeById(String strIds) {
        Envelop envelop = new Envelop();
        String url = "/cda_types/id/"+strIds;
        try{
            String envelopStr = HttpClientUtil.doGet(comUrl+url,username,password);
            return envelopStr;
        }catch (Exception ex){
            LogService.getLogger(CdaTypeController.class).error(ex.getMessage());
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return envelop;

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
            result.setErrorMsg("系统错误，请联系管理员!");
        }
        return result;*/
    }

    @RequestMapping("delteCdaTypeInfo")
    @ResponseBody
    /**
     * 删除CDA类别，若该类别存在子类别，将一并删除子类别
     * 先根据当前的类别ID获取全部子类别ID，再进行删除
     * @param  ids  cdaType Id
     *  @return result 操作结果
     */
    public Object delteCdaTypeInfo(String ids) {
        Envelop result = new Envelop();
        String url = "/cda_types/"+ids;
        if (StringUtils.isEmpty(ids)){
            result.setErrorMsg("请选择要删除的数据");
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
                result.setErrorMsg("cda类别删除失败");
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
                strErrorMsg += "请先选择将要删除的数据";
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
                result.setErrorMsg("删除失败!");
            }
        } catch (Exception ex) {
            LogService.getLogger(CdaController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg("系统错误，请联系管理员!");
        }
        return result;*/
    }

    @RequestMapping("SaveCdaType")
    @ResponseBody
    //新增、修改的保存合二为一
    public Object SaveCdaType(String dataJson) {
        //TODO 用户code  (待网关的获取下拉列表的接口）
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        String url = "/cda_types";
        //临时测试数据（用户code）
        String createUser = "wwcs";
        try {
            CdaTypeDetailModel detailModel = objectMapper.readValue(dataJson,CdaTypeDetailModel.class);
            if(StringUtils.isEmpty(detailModel.getCode())){
                envelop.setErrorMsg("cda类别编码不能为空！");
                return envelop;
            }
            if(StringUtils.isEmpty(detailModel.getName())){
                envelop.setErrorMsg("cda类别名称不能为空！");
                return envelop;
            }
            Map<String,Object> params = new HashMap<>();
            //TODO 暂时根据id是否空来判断新增、修改--（或改成通过传递参数 new/modify）

            String cdaTypeId = detailModel.getId();
            // 新增cda类别
            String envelopStr = "";
            if(StringUtils.isEmpty(cdaTypeId)){
                detailModel.setCreateUser(createUser);
                String jsonData = objectMapper.writeValueAsString(detailModel);
                params.put("jsonData",jsonData);
                envelopStr = HttpClientUtil.doPost(comUrl+url,params,username,password);
                return envelopStr;
            }
            // 修改cda类别（先获取，再赋值）
            String urlGet = "/cda_types/id/"+cdaTypeId;
            String envelopStrGet = HttpClientUtil.doGet(comUrl+urlGet,username,password);
            Envelop envelopGet = objectMapper.readValue(envelopStrGet,Envelop.class);
            if (!envelopGet.isSuccessFlg()){
                return envelopStrGet;
            }
            CdaTypeDetailModel modelForUpdate = getEnvelopModel(envelopGet.getObj(),CdaTypeDetailModel.class);
            modelForUpdate.setCode(detailModel.getCode());
            modelForUpdate.setDescription(detailModel.getDescription());
            modelForUpdate.setName(detailModel.getName());
            modelForUpdate.setParentId(detailModel.getParentId());
            modelForUpdate.setUpdateUser(createUser);
            String typeJson = objectMapper.writeValueAsString(modelForUpdate);
            params.put("typeJson", typeJson);

            String envelopStrUpdate = HttpClientUtil.doPost(comUrl+url,params,username,password);
            return envelopStrUpdate;
        } catch (Exception ex){
            LogService.getLogger(CdaTypeController.class).error(ex.getMessage());
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return envelop;
    }

    /**
     * 获取可以作为父类别的cda类别列表
     * （不含该类别及子类别、子类别的子类类别。。所剩下的类别）
     * @param strId
     * @param key
     * @return
     */
    @RequestMapping("getParentType")
    @ResponseBody
    public String getParentType(String strId, String key) {
        //TODO 待网关提供接口
        String url = "/cda_types/patient_ids/key";
        String strResult = "";
        try{
            Map<String,Object> params = new HashMap<>();
            params.put("patient_ids",strId);
            params.put("key",key);
            String envelopStr = HttpClientUtil.doGet(comUrl+url,params,username,password);
            Envelop envelop = objectMapper.readValue(envelopStr,Envelop.class);
            if (envelop.isSuccessFlg()) {
                List<CdaTypeModel> cdaModelList = (List<CdaTypeModel>) getEnvelopList(envelop.getDetailModelList(), new ArrayList<CdaTypeModel>(), CdaTypeModel.class);
                strResult = objectMapper.writeValueAsString(cdaModelList);
            }
        }catch(Exception ex){
            LogService.getLogger(CdaTypeController.class).error(ex.getMessage());
        }
        return strResult;
    }

    @RequestMapping("getCDATypeListByParentId")
    @ResponseBody
    //根据父级ID获取下一级cda类别（不含子类的子类。。）
    public Object getCDATypeListByParentId(String ids) {
        Envelop envelop = new Envelop();
        String url = "/children_cda_types/"+ids;
        if (StringUtils.isEmpty(ids)){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("id号不能为空！");
            return envelop;
        }
        try {
            String envelopStr = HttpClientUtil.doGet(comUrl + url, username, password);
            return envelopStr;
        } catch (Exception ex) {
            LogService.getLogger(CdaTypeController.class).error(ex.getMessage());
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return envelop;
    }
}
