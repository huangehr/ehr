package com.yihu.ehr.std.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.standard.cdatype.CdaTypeDetailModel;
import com.yihu.ehr.agModel.standard.cdatype.CdaTypeModel;
import com.yihu.ehr.agModel.user.UserDetailModel;
import com.yihu.ehr.constants.ErrorCode;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by AndyCai on 2015/12/14.
 */
@RequestMapping("/cdatype")
@Controller
@SessionAttributes(SessionAttributeKeys.CurrentUser)
public class CdaTypeController extends BaseUIController{
    @Value("${service-gateway.username}")
    private String username;
    @Value("${service-gateway.password}")
    private String password;
    @Value("${service-gateway.cdatypeurl}")
    private String comUrl;

    @Autowired
    ObjectMapper objectMapper;

    @RequestMapping("index")
    public String cdaTypeInitial(Model model) {
        model.addAttribute("contentPage", "std/cdaType/index");
        return "pageView";
    }

    @RequestMapping("typeupdate")
    public String typeupdate(Model model,HttpServletRequest request) {
        UserDetailModel user = (UserDetailModel)request.getSession().getAttribute(SessionAttributeKeys.CurrentUser);
        model.addAttribute("UserId", user.getId());
        model.addAttribute("contentPage", "std/cdaType/CdaTypeDetail");
        return "generalView";
    }

    @RequestMapping("getTreeGridData")
    @ResponseBody
    //获取TreeData 用于初始页面显示嵌套model
    public Object getTreeGridData(String codeName) {
        Envelop envelop = new Envelop();
        String url = "/cda_types/cda_types_tree";
        String strResult = "";
        if (StringUtils.isEmpty(codeName)){
            codeName = "";
        }
        try{
            Map<String,Object> params = new HashMap<>();
            params.put("code_name",codeName);
            strResult = HttpClientUtil.doGet(comUrl+url,params,username,password);
            return strResult;
        }catch(Exception ex){
            LogService.getLogger(CdaTypeController.class).error(ex.getMessage());
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
            return envelop;
        }
    }

    @RequestMapping("GetCdaTypeList")
    @ResponseBody
    public Object GetCdaTypeList(String strKey, Integer page, Integer rows) {
        Envelop envelop = new Envelop();
        String url = "/cda_types/no_paging";
        String filters = "";
        if(!StringUtils.isEmpty(strKey)){
            filters = "code?"+strKey+" g1;name?"+strKey+" g1;";
        }
        try{
            Map<String,Object> params = new HashMap<>();
            params.put("filters",filters);
            String _rus = HttpClientUtil.doGet(comUrl+url,params,username,password);
            return _rus;
        }catch (Exception ex){
            LogService.getLogger(CdaTypeController.class).error(ex.getMessage());
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return envelop;
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
    }

    @RequestMapping("SaveCdaType")
    @ResponseBody
    //新增、修改的保存合二为一
    public Object SaveCdaType(String dataJson,HttpServletRequest request) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        String url = "/cda_types";
        UserDetailModel userDetailModel = (UserDetailModel)request.getSession().getAttribute(SessionAttributeKeys.CurrentUser);
        String createUserId = userDetailModel.getId();
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
            String cdaTypeId = detailModel.getId();
            // 新增cda类别
            String envelopStr = "";
            if(StringUtils.isEmpty(cdaTypeId)){
                detailModel.setCreateUser(createUserId);
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
            modelForUpdate.setUpdateUser(createUserId);
            String typeJson = objectMapper.writeValueAsString(modelForUpdate);
            params.put("jsonData", typeJson);

            String envelopStrUpdate = HttpClientUtil.doPut(comUrl + url, params, username, password);
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
     * @param strId
     * @param codeName
     * @return
     */
    @RequestMapping("getCdaTypeExcludeSelfAndChildren")
    @ResponseBody
    public Object getCdaTypeExcludeSelfAndChildren(String strId, String codeName) {
        //页面新增修改访问的是同个接口
        Envelop envelop = new Envelop();
        try{
            //新增cda类别是获取的是所有类别
            if(StringUtils.isEmpty(strId)){
                String urlGetAll = "/cda_types/no_paging";
                String filters = "";
                if(!StringUtils.isEmpty(codeName)){
                    filters = "code?"+codeName+" g1;name?"+codeName+" g1;";
                }
                Map<String,Object> params = new HashMap<>();
                params.put("filters",filters);
                String envelopStr = HttpClientUtil.doGet(comUrl+urlGetAll,params,username,password);
                return envelopStr;
            }
            //修改时获取自身及其子类。。以外的cda类别
            String urlGetOthers = "/types/parent";
            Map<String,Object> args = new HashMap<>();
            args.put("id",strId);
            String envelopStrOthers = HttpClientUtil.doGet(comUrl+urlGetOthers,args,username,password);
            return envelopStrOthers;
        }catch (Exception ex){
            LogService.getLogger(CdaTypeController.class).error(ex.getMessage());
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
            return envelop;
        }

    }

    @RequestMapping("getCDATypeListByParentId")
    @ResponseBody
    //根据父级ID获取下一级cda类别（不含子类的子类。。）
    public Object getCDATypeListByParentId(String ids) {
        Envelop envelop = new Envelop();
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> params = new HashMap<>();
        params.put("parent_id",ids);
        String url = "/children_cda_types";

        try {
            String envelopStr = HttpClientUtil.doGet(comUrl + url,params,username, password);
            envelop = mapper.readValue(envelopStr,Envelop.class);

            return envelop.getDetailModelList();
        } catch (Exception ex) {
            LogService.getLogger(CdaTypeController.class).error(ex.getMessage());
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return envelop;
    }


    /**
     * 删除cda类别前先判断是否有关联的cda文档
     * @param ids
     * @return
     */
    @RequestMapping("isExitRelativeCDA")
    @ResponseBody
    public Object isExitRelativeCDA(String ids){
        Envelop envelop = new Envelop();
        String url = "/isExitRelativeCDA";
        try{
            Map<String,Object> params = new HashMap<>();
            params.put("ids",ids);
            String envelopStr = HttpClientUtil.doGet(comUrl+url,params,username,password);
            return envelopStr;
        }catch (Exception ex){
            LogService.getLogger(CdaTypeController.class).error(ex.getMessage());
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
            return envelop;
        }
    }
}
