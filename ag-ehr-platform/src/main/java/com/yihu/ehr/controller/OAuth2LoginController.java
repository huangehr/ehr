package com.yihu.ehr.controller;

import org.springframework.stereotype.Controller;
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
    public ModelAndView confirmAccess(){
        ModelAndView modelAndView = new ModelAndView("templates/login/oauth/confirm_access.jsp");

        return modelAndView;
    }

    @RequestMapping("/oauth/error")
    public ModelAndView error(){
        ModelAndView modelAndView = new ModelAndView("login/oauth/access_confirmation.jsp");

        return modelAndView;
    }
}
