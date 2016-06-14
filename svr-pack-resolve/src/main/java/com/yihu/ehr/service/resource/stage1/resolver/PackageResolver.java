package com.yihu.ehr.service.resource.stage1.resolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.service.resource.stage1.StandardPackage;
import com.yihu.ehr.service.resource.stage1.extractor.ExtractorChain;
import com.yihu.ehr.service.resource.stage1.DataSetParserWithTranslator;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

/**
 * 包解析器。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.04.13 16:14
 */
public abstract class PackageResolver {
    @Autowired
    protected DataSetParserWithTranslator dataSetResolverWithTranslator;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected ExtractorChain extractorChain;

    public abstract void resolve(StandardPackage profile, File root) throws Exception;
}
