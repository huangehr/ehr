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

import java.util.HashMap;
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


    @RequestMapping("modify")
    public Object gotoModify(Model model, String id, String mode){
        try {
            Envelop envelop = new Envelop();
            if (!StringUtils.isEmpty(id)){
                Map<String, Object> params = new HashMap<>();
                params.put("id",id);
                envelop = getEnvelop(service.getModel(params));
            }
            Object plan;
            if(envelop.getObj()==null)
                plan = service.newModel();
            else
                plan = envelop.getObj();

            model.addAttribute("model",toJson(plan));
            model.addAttribute("mode",mode);
            model.addAttribute("contentPage", getModeUrl(mode));
            return "simpleView";
        } catch (Exception e) {
            e.printStackTrace();
            return systemError();
        }
    }


    @RequestMapping("list")
    @ResponseBody
    public Object search(String fields, String filters, String sorts, int page, int rows){

        try{
            Map<String, Object> params = new HashMap<>();
            params.put("fields", nullToSpace(fields));
            params.put("filters",nullToSpace(filters));
            params.put("sorts",nullToSpace(sorts));
            params.put("page",page);
            params.put("size",rows);

            String resultStr = service.search(params);
            return resultStr;
        } catch (Exception e) {
            e.printStackTrace();
            return systemError();
        }
    }


    @RequestMapping("delete")
    @ResponseBody
    public Object delAdapterPlan(String ids){

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("ids", nullToSpace(ids));
            return service.delete(params);
        } catch (Exception e) {
            return systemError();
        }
    }

    public Envelop systemError(){

        return faild(ErrorCode.SystemError.toString());
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

}
