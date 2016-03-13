package com.yihu.ehr.login.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/index")
public class IndexController {

    @RequestMapping(value = "")
    public String init(Model model,HttpServletRequest request) {

        return "frameView";
    }
}