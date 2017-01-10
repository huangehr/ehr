package com.yihu.ehr.schema;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Sand
 * @created 2016.05.19 13:54
 */
@Component
public class StdVersionKeySchema extends KeySchema {
    @Value("${ehr.redis-key-schema.std.version-table-prefix}")
    private String VersionTable = "std_cda_versions";

    @Value("${ehr.redis-key-schema.std.version-name}")
    private String VersionNameColumn = "name";

    public String versionName(String version){
        return makeKey(VersionTable, version, VersionNameColumn);
    }
}
