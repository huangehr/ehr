package com.yihu.ehr.service.resource.stage1.resolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.service.resource.stage1.DataSetParserWithTranslator;
import com.yihu.ehr.service.resource.stage1.StandardPackage;
import com.yihu.ehr.service.resource.stage1.extractor.ExtractorChain;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.List;

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

    /**
     *  非档案类型包解析
     * @param root
     * @throws Exception
     */
    public abstract List<StandardPackage> resolveDataSets(File root, String clientId) throws Exception;
    
    public abstract void resolve(StandardPackage profile, File root) throws Exception;


}
