//package com.yihu.ehr.standard.document.service;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import com.yihu.ehr.constants.ErrorCode;
//import com.yihu.ehr.exception.ApiException;
//import com.yihu.ehr.fastdfs.FastDFSUtil;
//import com.yihu.ehr.query.BaseHbmService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import javax.transaction.Transactional;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
///**
// * 版本管理实现类。
// *
// * @author Sand
// * @version 1.0
// * @updated 02-7月-2015 14:40:20
// */
//@Service
//@Transactional
//public class CDAManager extends BaseHbmService<ICDADataSetRelationship> {
//
//    @Autowired
//    private CDADataSetRelationshipManager cdaDataSetRelationshipManager;
//
//    @Autowired
//    private CDADocumentManager cdaDocumentManager;
//
//    @Autowired
//    private FastDFSUtil fastDFSUtil;
//
//    public Class getServiceEntity(String version){
//        try {
//            return Class.forName("com.yihu.ehr.standard.document.service.CDADocument" + version);
//        } catch (ClassNotFoundException e) {
//            throw new ApiException(ErrorCode.NotFoundEntity, "CDA文档", version);
//        }
//    }
//
//
//
//
//
//}