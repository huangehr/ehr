package com.yihu.ehr.esb.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.esb.MHosSqlTask;
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
public interface HosSqlTaskClient {

    @RequestMapping(value = "/searchHosSqlTasks", method = RequestMethod.GET)
    ResponseEntity<List<MHosSqlTask>> searchHosSqlTasks(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);


    @RequestMapping(value = "/hosSqlTask/{id}",method = RequestMethod.GET)
    MHosSqlTask getHosSqlTask(
            @PathVariable(value = "id") String id);

    @RequestMapping(value = "/createHosSqlTask", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    MHosSqlTask createHosSqlTask(
            @RequestBody String jsonData);

    @RequestMapping(value = "/updateHosSqlTask", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    MHosSqlTask updateHosSqlTask(
            @RequestBody String jsonData);


    @RequestMapping(value = "/deleteHosSqlTask/{id}", method = RequestMethod.DELETE)
    boolean deleteHosSqlTask(
            @PathVariable(value = "id") String id);

}
