package com.yihu.ehr.profile.feign;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.standard.MCDADocument;
import com.yihu.ehr.model.standard.MCdaDataSetRelationship;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.04.11 9:03
 */
@ApiIgnore
@FeignClient(MicroServices.Standard)
public interface XCDADocumentClient {
    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.Standards.Documents, method = RequestMethod.GET)
    List<MCDADocument> getCDADocuments(@RequestParam(value = "fields") String fields,
                                       @RequestParam(value = "filters") String filters,
                                       @RequestParam(value = "sorts") String sorts,
                                       @RequestParam(value = "size") int size,
                                       @RequestParam(value = "page") int page,
                                       @RequestParam(value = "version") String version);

    @RequestMapping(value = ApiVersion.Version1_0 + "/std/cda_data_set_relationships/cda_id", method = RequestMethod.GET)
    List<MCdaDataSetRelationship> getCDADataSetRelationshipByCDAId(
            @RequestParam(value = "version") String version,
            @RequestParam(value = "document_Id") String cdaDocumentId);
}
