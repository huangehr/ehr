package com.yihu.ehr.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.profile.core.StructedProfile;
import com.yihu.ehr.profile.core.extractor.ExtractorChain;
import com.yihu.ehr.profile.persist.DataSetResolverWithTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

/**
 * 包解析器。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.04.13 16:14
 */
public abstract class PackageResolver {
    @Autowired
    protected ApplicationContext context;

    @Autowired
    protected DataSetResolverWithTranslator dataSetResolverWithTranslator;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected ExtractorChain extractorChain;

    private final static char PathSep = File.separatorChar;
    private final static String LocalTempPath = System.getProperty("java.io.tmpdir");

    public abstract void resolve(StructedProfile profile, File root) throws IOException, ParseException;
}
