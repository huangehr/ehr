package com.yihu.ehr.resource.service.intf;


import com.yihu.ehr.resource.dao.ResourcesCategoryDao;
import com.yihu.ehr.resource.model.RsCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by hzp on 20160419.
 */
public interface IResourcesCategoryService {
    void deleteRsCategory(String id) throws Exception;

    RsCategory createOrUpdRsCategory(RsCategory rsCategory);

    long getCount(String filters) throws java.text.ParseException;

    List search(String fields, String filters, String sorts, Integer page, Integer size) throws java.text.ParseException;

    Page<RsCategory> getRsCategories(String sorts, int page, int size);

}
