package com.yihu.ehr.resource.dao.intf;

import com.yihu.ehr.resource.model.ResourceDefaultParam;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author linaz
 * @created 2016.05.17 16:33
 */
public interface ResourceDefaultParamDao extends PagingAndSortingRepository<ResourceDefaultParam,Long> {

    List<ResourceDefaultParam> findByResourcesCode(String resourcesCode);

    @Query("from ResourceDefaultParam where resourcesId = ?1 or resourcesCode = ?2 ")
    List<ResourceDefaultParam> findByResourcesIdOrResourcesCode(String resourcesId,String resourceCode);

    @Query("from ResourceDefaultParam where ( resourcesId = ?1 or resourcesCode = ?2 ) and paramKey = ?3 ")
    List<ResourceDefaultParam> findByResourcesIdOrResourcesCodeWithParamKey(String resourceId,String resourceCode ,String ParamKey);

    @Query("select rdf from ResourceDefaultParam rdf where rdf.id = :id")
    ResourceDefaultParam findById(@Param("id") String id);
}