package com.yihu.ehr.adapter.controller;

import com.yihu.ehr.adapter.service.ExtendService;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseUIController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/3/19
 */
public class ExtendController<T extends ExtendService> extends BaseUIController {

    @Autowired
    public T service;

    public String listUrl = "";
    public String modifyUrl = "";
    public String addUrl = "";

    public Map<String, String> comboKv = null;


    public void init(String listUrl, String modifyUrl){
        this.init(listUrl, modifyUrl, modifyUrl);
    }

    public void init(String listUrl, String modifyUrl, String addUrl){
        this.listUrl = listUrl;
        this.addUrl = addUrl;
        this.modifyUrl = modifyUrl;
    }

    @RequestMapping("/initial")
    public String gotoList(Model model, String dataModel){
        model.addAttribute("dataModel",dataModel);
        model.addAttribute("contentPage", this.listUrl);
        return "pageView";
    }


    @RequestMapping("/gotoModify")
    public Object gotoModify(Model model, String id, String mode, String extParms){
        try {
            Envelop envelop = new Envelop();
            if (!StringUtils.isEmpty(id)){
                Map<String, Object> params = new HashMap<>();
                params.put("id",id);

                params = putAll(extParms, params);
                params = beforeGotoModify(params);

                envelop = getEnvelop(service.getModel(params));
            }
            Object plan;
            if(envelop.getObj()==null)
                plan = service.newModel();
            else
                plan = envelop.getObj();

            plan = afterGotoModify(plan);

            model.addAttribute("model",toJson(plan));
            model.addAttribute("mode",mode);
            model.addAttribute("contentPage", getModeUrl(mode));
            return "simpleView";
        } catch (Exception e) {
            e.printStackTrace();
            return systemError();
        }
    }

    @RequestMapping("/model")
    @ResponseBody
    public Object getModel(String id, String extParms){
        try {
            Envelop envelop = new Envelop();
            if (!StringUtils.isEmpty(id)){
                Map<String, Object> params = new HashMap<>();
                params.put("id",id);

                params = putAll(extParms, params);
                params = beforeGotoModify(params);

                envelop = getEnvelop(service.getModel(params));
            }
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            return systemError();
        }
    }

    @RequestMapping("/list")
    @ResponseBody
    public Object search(String fields, String filters, String sorts, int page, int rows, String extParms){

        try{
            Map<String, Object> params = new HashMap<>();
            params.put("fields", nullToSpace(fields));
            params.put("filters",nullToSpace(filters));
            params.put("sorts",nullToSpace(sorts));
            params.put("page",page);
            params.put("size",rows);
            putAll(extParms, params);
            String url = beforeSearch(service.searchUrl, params);
            String resultStr = service.search(url, params);
            resultStr = afterSearch(resultStr);
            return resultStr;
        } catch (Exception e) {
            e.printStackTrace();
            return systemError();
        }
    }


    @RequestMapping("/combo")
    @ResponseBody
    public Object comboSearch(String fields, String filters, String sorts, int page, int rows){

        try{
            Map<String, Object> params = new HashMap<>();
            params.put("fields", nullToSpace(fields));
            params.put("filters",nullToSpace(filters));
            params.put("sorts",nullToSpace(sorts));
            params.put("page",page);
            params.put("size",rows);

            String resultStr = service.search(params);
            resultStr = afterComboSearch(resultStr);
            return resultStr;
        } catch (Exception e) {
            e.printStackTrace();
            return systemError();
        }
    }

    public String afterComboSearch(String resultStr) {
        Envelop envelop = getEnvelop(resultStr);
        List ls = envelop.getDetailModelList();
        Map<String, String> map;
        List rs = new ArrayList<>();
        for(int i=0; i<ls.size(); i++){
            map = ((Map) ls.get(i));
            if(comboKv!=null){
                for(String key : comboKv.keySet()){
                    map.put(key, map.get(comboKv.get(key)));
                }
            }
            rs.add(map);
        }
        envelop.setDetailModelList(rs);
        return toJson(envelop);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public Object delete(String ids, String extParms){

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("ids", nullToSpace(ids));
            params = putAll(extParms, params);

            params = beforeDel(params);
            String rs = service.delete(params);
            rs = afterDel(rs);
            return rs;
        } catch (Exception e) {
            return systemError();
        }
    }


    @RequestMapping("/update")
    @ResponseBody
    public Object update(String id, String model, String extParms){

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("id",id);
            params.put("model",model);

            params =  putAll(extParms, params);

            params = beforeUpdate(params);
            String resultStr = "";
            if(StringUtils.isEmpty(id))
                resultStr = service.add(params);
            else
                resultStr = service.update(params);

            resultStr = afterUpdate(resultStr);
            return resultStr;
        } catch (Exception e) {
            return systemError();
        }
    }

    public String beforeSearch(String searchUrl, Map<String, Object> params) {

        return searchUrl;
    }

    public String afterSearch(String rs){

        return rs;
    }

    public Object afterGotoModify(Object rs){

        return rs;
    }

    public Map beforeGotoModify(Map params){

        return params;
    }

    public String afterDel(String rs){

        return rs;
    }

    public Map beforeDel(Map params){

        return params;
    }

    public String afterUpdate(String rs){

        return rs;
    }

    public Map beforeUpdate(Map params){

        return params;
    }

    public Envelop systemError(){

        return faild(ErrorCode.SystemError.toString());
    }

    public Envelop pramsError(){

        return faild("参数出错！");
    }

    public Envelop faild(String msg){
        Envelop result = new Envelop();
        result.setSuccessFlg(false);
        result.setErrorMsg(msg);
        return result;
    }

    public String getModeUrl(String mode){
        if("add".equals(mode))
            return this.addUrl;
        return this.modifyUrl;
    }

    public String nullToSpace(String str){
        if(str == null)
            return "";
        return str;
    }

    public Map putAll(String sourceJson, Map target){
        if(!StringUtils.isEmpty(sourceJson)){
            Map<String, String> source = new HashMap<>();
            try {
                source = objectMapper.readValue(sourceJson, Map.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            for(Object key : source.keySet()){
                target.put(key, source.get(key));
            }
        }
        return target;
    }

    public Envelop formatComboData(String resultStr, String idField, String nameField){
        Envelop rs = getEnvelop(resultStr);
        List ls = new ArrayList<>();
        if(rs.getDetailModelList()!=null && rs.getDetailModelList().size()>0){
            Map<String, String> tem, map;
            for(Object obj: rs.getDetailModelList()){
                map = ((Map) obj);
                tem = new HashMap<>();
                tem.put("id", map.get(idField));
                tem.put("name", map.get(nameField));
                ls.add(tem);
            }
        }
        rs.setDetailModelList(ls);
        return rs;
    }
}
