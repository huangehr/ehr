package com.yihu.ehr.profile.feign;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.profile.model.MCDADocument;
import com.yihu.hos.model.standard.MCdaDataSet;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.04.11 9:03
 */
@ApiIgnore
@RequestMapping(ApiVersion.Version1_0)
@FeignClient(MicroServices.Standard)
public interface CDADocumentClient {
    @RequestMapping(value = ServiceApi.Standards.Documents, method = RequestMethod.GET)
    List<MCDADocument> getCDADocumentByIds(@RequestParam(value = "fields") String fields,
                                           @RequestParam(value = "filters") String filters,
                                           @RequestParam(value = "sorts") String sorts,
                                           @RequestParam(value = "size") int size,
                                           @RequestParam(value = "page") int page,
                                           @RequestParam(value = "version") String version);

    @RequestMapping(value = ServiceApi.Standards.Document, method = RequestMethod.GET)
    MCDADocument getCDADocuments(@RequestParam(value = "version") String version,
                                           @RequestParam(value = "id") String id);

    @RequestMapping(value = ServiceApi.Standards.DocumentList, method = RequestMethod.GET)
    Map<String,MCDADocument> getCDADocumentsList(@RequestParam(value = "version") String version,
                                 @RequestParam(value = "idList") String[] id);

    @RequestMapping(value = ServiceApi.Standards.DocumentDataSet, method = RequestMethod.GET)
    List<MCdaDataSet> getCDADataSetByCDAId(
            @RequestParam(value = "version") String version,
            @RequestParam(value = "document_Id") String cdaDocumentId);

    @RequestMapping(value = ServiceApi.Standards.DocumentDataSetList, method = RequestMethod.GET)
    Map<String, List<MCdaDataSet>> getCDADataSetByCDAIdList(
            @RequestParam(value = "version") String version,
            @RequestParam(value = "document_Id") String[] cdaDocumentId);
}
