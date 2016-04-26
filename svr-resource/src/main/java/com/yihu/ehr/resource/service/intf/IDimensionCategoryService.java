package com.yihu.ehr.resource.service.intf;

import com.yihu.ehr.resource.model.RsDimension;
import com.yihu.ehr.resource.model.RsDimensionCategory;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by lyr on 2016/4/25.
 */
public interface IDimensionCategoryService {
    RsDimensionCategory createDimensionCategory(RsDimensionCategory dimensionCategory);

    void updateDimensionCategory(RsDimensionCategory dimensionCategory);

    void deleteDimensionCategory(String id) throws Exception;

    long getCount(String filters) throws java.text.ParseException;

    List search(String fields, String filters, String sorts, Integer page, Integer size) throws java.text.ParseException;

    Page<RsDimensionCategory> getDimensionCategories(String sorts, int page, int size);
}
