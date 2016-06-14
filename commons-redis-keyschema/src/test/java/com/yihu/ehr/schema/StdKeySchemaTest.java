package com.yihu.ehr.schema;

import static org.junit.Assert.*;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.25 11:57
 */
public class StdKeySchemaTest {
    static final String Version = "000000000000";

    StdVersionKeySchema versionKeySchema;
    StdDataSetKeySchema dataSetKeySchema;
    StdMetaDataKeySchema metaDataKeySchema;

    //@Test
    public void versionKey() throws Exception {
        assertEquals("std_cda_versions:000000000000:name", versionKeySchema.versionName(Version));
        assertNotEquals("std_cda_versions:000000000000:version", versionKeySchema.versionName(Version));
    }

    //@Test
    public void dataSetKey() throws Exception {
        assertEquals("std_data_set_000000000000:1:code", dataSetKeySchema.dataSetName(Version, "1"));
        assertNotEquals("std_data_set_000000000000:1:name", dataSetKeySchema.dataSetName(Version, "2"));
    }

    //@Test
    public void metaDataKey() throws Exception {
        assertEquals("std_meta_data_000000000000:HDSA01.HDSA01_001:dict_id", metaDataKeySchema.metaDataDict(Version, "HDSA01", "HDSA01_001"));
        assertNotEquals("std_meta_data_000000000000:HDSA01.HDSA01_001:code", metaDataKeySchema.metaDataDict(Version, "HDSA01", "HDSA01_001"));
    }

    //@Test
    public void dictEntryKey() throws Exception {
        assertEquals("std_dictionary_entry_000000000000:4.01:value", metaDataKeySchema.dictEntryValue(Version, "4", "01"));
        assertNotEquals("std_dictionary_entry_000000000000:401:value", metaDataKeySchema.dictEntryValue(Version, "4", "01"));
    }
}