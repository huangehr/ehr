package com.yihu.ehr.analyze.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

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

    public abstract void analyze(ZipPackage profile) throws Exception;
}
