package com.yihu.ehr.analyze.service.pack;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.profile.extractor.ExtractorChain;
import com.yihu.ehr.profile.util.DataSetParserUtil;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 包解析器。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.04.13 16:14
 */
public abstract class PackageAnalyzer {
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected ExtractorChain extractorChain;
    @Autowired
    protected DataSetParserUtil dataSetParser;

    public abstract void analyze(ZipPackage profile) throws Exception;
}
