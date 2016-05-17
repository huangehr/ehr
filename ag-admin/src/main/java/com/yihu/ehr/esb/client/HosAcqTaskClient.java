package com.yihu.ehr.esb.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.esb.MHosAcqTask;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @author linaz
 * @created 2016.05.13 14:15
 */
@FeignClient(name=MicroServices.ESB)
@RequestMapping(value = ApiVersion.Version1_0 + "/esb")
@ApiIgnore
public interface HosAcqTaskClient {

    @RequestMapping(value = "/searchHosAcqTasks", method = RequestMethod.GET)
    ResponseEntity<List<MHosAcqTask>> searchHosAcqTasks(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = "/createHosAcqTask", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    MHosAcqTask createHosAcqTask(
            @RequestBody String jsonData);

    @RequestMapping(value = "/updateHosAcqTask", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    MHosAcqTask updateHosAcqTask(
            @RequestBody String jsonData);

    @RequestMapping(value = "/deleteHosAcqTask/{id}", method = RequestMethod.DELETE)
    boolean deleteHosAcqTask(
            @PathVariable(value = "id") String id);

}
