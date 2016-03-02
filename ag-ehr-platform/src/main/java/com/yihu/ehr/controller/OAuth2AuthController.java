package com.yihu.ehr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.01 20:06
 */
@Controller
@RequestMapping("/login/oauth")
public class OAuth2AuthController {
    @RequestMapping("/confirm_access")
    public void confirmAccess(ModelAndView model) {
        model.addObject("name", "Sand");
        model.addObject("clientName", "爱康弹性福利平台");

        model.setViewName("confirm_access.html");
    }

    @RequestMapping("/error")
    public void error(ModelAndView model) {
        model.setViewName("error.html");
    }
}
