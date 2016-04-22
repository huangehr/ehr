package com.yihu.ehr.security.controller;

import com.yihu.ehr.adapter.controller.ExtendController;
import com.yihu.ehr.security.service.SecurityService;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.log.LogService;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/12.
 */
@RequestMapping("/security")
@Controller
public class SecurityController extends ExtendController<SecurityService>{


    @RequestMapping("/getUserPublicKey")
    @ResponseBody
    public Object getUserPublicKey(String loginCode) {

        try{
            Envelop result = new Envelop();
            if(StringUtils.isEmpty(loginCode)){
                result.setSuccessFlg(false);
                result.setErrorMsg("登陆名不允行为空");
                return result;
            }

            Map<String, Object> params = new HashMap<>();
            params.put("login_code",loginCode);

            String resultStr = service.search("/securities/user/" + loginCode, params);
            result = getEnvelop(resultStr);

            if(result.isSuccessFlg()){
                Map<String, String> userSecurityModel = (Map)result.getObj();
                result.setObj(userSecurityModel.get("publicKey"));
            }
            return result;

        }catch (Exception ex){
            LogService.getLogger(SecurityController.class).error(ex.getMessage());
            return systemError();
        }
    }

    @RequestMapping("/getOrgPublicKey")
    @ResponseBody
    public Object getOrgPublicKey(String orgCode){

        try{
            Envelop result = new Envelop();

            if(StringUtils.isEmpty(orgCode)){
                result.setSuccessFlg(false);
                result.setErrorMsg("机构代码不允行为空");
                return result;
            }

            Map<String, Object> params = new HashMap<>();
            params.put("login_code",orgCode);

            String resultStr = service.search("/securities/org/" + orgCode, params);

            result = getEnvelop(resultStr);
            if(result.isSuccessFlg()){
                Map<String, String> userSecurityModel = (Map)result.getObj();
                result.setObj(userSecurityModel.get("publicKey"));
            }
            return result;

        }catch (Exception ex){
            LogService.getLogger(SecurityController.class).error(ex.getMessage());
            return systemError();
        }
    }

}
