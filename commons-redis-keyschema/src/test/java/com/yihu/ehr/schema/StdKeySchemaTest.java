package com.yihu.ehr.org;

import com.yihu.ehr.schema.StdKeySchema;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.25 11:57
 */
public class StdKeySchemaTest {
    static final String Version = "000000000000";

    StdKeySchema schema;

    //@Test
    public void versionKey() throws Exception {
        assertEquals("std_cda_versions:000000000000:name", schema.versionName(Version));
        assertNotEquals("std_cda_versions:000000000000:version", schema.versionName(Version));
    }

    //@Test
    public void dataSetKey() throws Exception {
        assertEquals("std_data_set_000000000000:HDSA01:code", schema.dataSetCode(Version, "HDSA01"));
        assertNotEquals("std_data_set_000000000000:HDSA01:name", schema.dataSetCode(Version, "HDSA01"));
    }

    //@Test
    public void metaDataKey() throws Exception {
        assertEquals("std_meta_data_000000000000:HDSA01.HDSA01_001:dict_id", schema.metaDataDict(Version, "HDSA01", "HDSA01_001"));
        assertNotEquals("std_meta_data_000000000000:HDSA01.HDSA01_001:code", schema.metaDataDict(Version, "HDSA01", "HDSA01_001"));
    }

    //@Test
    public void dictEntryKey() throws Exception {
        assertEquals("std_dictionary_entry_000000000000:4.01:value", schema.dictEntryValue(Version, "4", "01"));
        assertNotEquals("std_dictionary_entry_000000000000:401:value", schema.dictEntryValue(Version, "4", "01"));
    }
}