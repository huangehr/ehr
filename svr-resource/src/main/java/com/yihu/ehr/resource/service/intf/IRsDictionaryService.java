package com.yihu.ehr.resource.service.intf;

import com.yihu.ehr.resource.model.RsDictionary;

/**
 * @author linaz
 * @created 2016.05.17 16:33
 */
public interface IRsDictionaryService {

    RsDictionary findById(String id);
}
