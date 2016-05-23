package com.yihu.ehr.resource.service.intf;

import com.yihu.ehr.resource.model.RsDimension;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by lyr on 2016/4/25.
 */
public interface IDimensionService {
    RsDimension saveDimension(RsDimension dimension);

    void deleteDimension(String id);

    long getCount(String filters) throws java.text.ParseException;

    List search(String fields, String filters, String sorts, Integer page, Integer size) throws java.text.ParseException;

    Page<RsDimension> getDimensions(String sorts, int page, int size);

}
