package com.yihu.ehr.analyze.controller.qc;

import com.yihu.ehr.analyze.service.qc.QcRuleCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Airhead
 * @created 2018-01-19
 */
@RestController
@RequestMapping(value = "/qc/check")
public class QcRuleCheckEndpoint {
    @Autowired
    QcRuleCheckService service;

    @RequestMapping(value = "/empty", produces = "application/json;charset=UTF-8", method = RequestMethod.GET)
    public void emptyCheck(
            @RequestBody String message) {
        service.emptyCheck(message);
    }

    @RequestMapping(value = "/type", produces = "application/json;charset=UTF-8", method = RequestMethod.GET)
    public void typeCheck(
            @RequestBody String message) {
        service.typeCheck(message);
    }

    @RequestMapping(value = "/format", produces = "application/json;charset=UTF-8", method = RequestMethod.GET)
    public void formatCheck(
            @RequestBody String message) {
        service.formatCheck(message);
    }

    @RequestMapping(value = "/value", produces = "application/json;charset=UTF-8", method = RequestMethod.GET)
    public void valueCheck(
            @RequestBody String message) {
        service.valueCheck(message);
    }
}
