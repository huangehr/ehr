package com.yihu.ehr.esb.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.esb.MHosLog;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author linaz
 * @created 2016.05.13 14:15
 */
@FeignClient(name=MicroServices.ESB)
@RequestMapping(value = ApiVersion.Version1_0 + "/esb")
@ApiIgnore
public interface HosLogClient {

    @RequestMapping(value = "/searchHosLogs", method = RequestMethod.GET)
    ResponseEntity<List<MHosLog>> searchHosLogs(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);



    @RequestMapping(value = "/deleteHosLogs/{id}", method = RequestMethod.DELETE)
    boolean deleteHosLogs(
            @PathVariable(value = "id") String id);

}
