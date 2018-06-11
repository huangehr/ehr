package com.yihu.ehr.resolve;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.profile.extractor.ExtractorChain;
import com.yihu.ehr.profile.util.DataSetParserUtil;
import com.yihu.ehr.resolve.model.stage1.OriginalPackage;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

/**
 * Created by progr1mmer on 2018/6/8.
 */
public abstract class PackageResolver {

    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected DataSetParserUtil dataSetParser;
    @Autowired
    protected ExtractorChain extractorChain;

    public abstract void resolve(OriginalPackage originalPackage, File root) throws Exception;
}
