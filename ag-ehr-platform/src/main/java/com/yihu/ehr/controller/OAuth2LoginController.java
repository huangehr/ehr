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
@RequestMapping("/login")
public class OAuth2LoginController {
    @RequestMapping("/oauth/confirm_access")
    public String confirmAccess(Model model) {
        model.addAttribute("name", "Sand");
        model.addAttribute("clientName", "爱康弹性福利平台");

        return "login/oauth/confirm_access.html";
    }

    @RequestMapping("/oauth/error")
    public String error() {
        return "login/oauth/error";
    }
}
