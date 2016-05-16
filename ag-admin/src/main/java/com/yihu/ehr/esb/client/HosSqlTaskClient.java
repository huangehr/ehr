package com.yihu.ehr.esb.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.esb.MHosSqlTask;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
    List<MHosSqlTask> searchHosSqlTasks(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);



    @RequestMapping(value = "/createHosSqlTask", method = RequestMethod.POST)
    MHosSqlTask createHosSqlTask(
            @RequestParam(value = "json_data", required = true) String jsonData);

    @RequestMapping(value = "/updateHosSqlTask", method = RequestMethod.PUT)
    MHosSqlTask updateHosSqlTask(
            @RequestParam(value = "json_data") String jsonData);


    @RequestMapping(value = "/deleteHosSqlTask/{id}", method = RequestMethod.DELETE)
    boolean deleteHosSqlTask(
            @PathVariable(value = "id") String id);

}
