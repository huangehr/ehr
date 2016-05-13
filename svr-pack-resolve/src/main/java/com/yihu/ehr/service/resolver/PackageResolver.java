package com.yihu.ehr.service.resolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.profile.core.StdProfile;
import com.yihu.ehr.profile.extractor.ExtractorChain;
import com.yihu.ehr.profile.persist.DataSetResolverWithTranslator;
import org.springframework.beans.factory.annotation.Autowired;

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
    protected DataSetResolverWithTranslator dataSetResolverWithTranslator;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected ExtractorChain extractorChain;

    public abstract void resolve(StdProfile profile, File root) throws Exception;
}
