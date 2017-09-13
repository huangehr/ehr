package com.yihu.ehr.resource.dao;

import com.yihu.ehr.resource.model.RsAdapterDictionary;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author linaz
 * @created 2016.05.17 16:33
 */
public interface RsAdapterDictionaryDao extends PagingAndSortingRepository<RsAdapterDictionary, String> {

    @Modifying
    @Query("delete from RsAdapterDictionary where schemeId = ?1")
    void deleteBySchemaId(String schemeId);
    List<RsAdapterDictionary> findBySchemeId(String schemeId);
}