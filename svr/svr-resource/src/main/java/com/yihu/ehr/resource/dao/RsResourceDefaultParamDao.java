package com.yihu.ehr.resource.dao;

import com.yihu.ehr.resource.model.RsResourceDefaultParam;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author linaz
 * @created 2016.05.17 16:33
 */
public interface RsResourceDefaultParamDao extends PagingAndSortingRepository<RsResourceDefaultParam, Long> {

    List<RsResourceDefaultParam> findByResourcesCode(String resourcesCode);

    @Query("from ResourceDefaultParam where resourcesId = ?1 or resourcesCode = ?2 ")
    List<RsResourceDefaultParam> findByResourcesIdOrResourcesCode(String resourcesId, String resourceCode);

    @Query("from ResourceDefaultParam where ( resourcesId = ?1 or resourcesCode = ?2 ) and paramKey = ?3 ")
    List<RsResourceDefaultParam> findByResourcesIdOrResourcesCodeWithParamKey(String resourceId, String resourceCode , String ParamKey);

    @Query("select rdf from RsResourceDefaultParam rdf where rdf.id = :id")
    RsResourceDefaultParam findById(@Param("id") String id);
}