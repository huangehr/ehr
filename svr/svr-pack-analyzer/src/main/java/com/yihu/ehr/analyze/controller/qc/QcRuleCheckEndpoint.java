package com.yihu.ehr.analyze.controller.qc;

import com.yihu.ehr.analyze.service.qc.QcRuleCheckService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Airhead
 * @created 2018-01-19
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class QcRuleCheckEndpoint {
    @Autowired
    private QcRuleCheckService service;

    @RequestMapping(value = ServiceApi.PackageAnalyzer.QcEmpty, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public void emptyCheck(@RequestBody String message) throws Exception {
        //service.emptyCheck(message);
    }

    @RequestMapping(value = ServiceApi.PackageAnalyzer.QcType, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public void typeCheck(@RequestBody String message) throws Exception {
        //service.typeCheck(message);
    }

    @RequestMapping(value = ServiceApi.PackageAnalyzer.QcFormat, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public void formatCheck(@RequestBody String message) throws Exception {
        //service.formatCheck(message);
    }

    @RequestMapping(value = ServiceApi.PackageAnalyzer.QcValue, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public void valueCheck(@RequestBody String message) throws Exception {
        //service.valueCheck(message);
    }
}
