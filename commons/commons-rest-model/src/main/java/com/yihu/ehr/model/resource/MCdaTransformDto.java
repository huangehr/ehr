package com.yihu.ehr.model.resource;

import java.util.List;
import java.util.Map;

/**
 * @author hzp
 * @created 2016.07.21 17:11
 */
public class MCdaTransformDto {
    private Map<String, Object> masterJson;
    private Map<String, List<String>> masterDatasetCodeList;
    private Map<String, List<String>> multiDatasetCodeList;

    public Map<String, Object> getMasterJson() {
        return masterJson;
    }

    public void setMasterJson(Map<String, Object> masterJson) {
        this.masterJson = masterJson;
    }

    public Map<String, List<String>> getMasterDatasetCodeList() {
        return masterDatasetCodeList;
    }

    public void setMasterDatasetCodeList(Map<String, List<String>> masterDatasetCodeList) {
        this.masterDatasetCodeList = masterDatasetCodeList;
    }

    public Map<String, List<String>> getMultiDatasetCodeList() {
        return multiDatasetCodeList;
    }

    public void setMultiDatasetCodeList(Map<String, List<String>> multiDatasetCodeList) {
        this.multiDatasetCodeList = multiDatasetCodeList;
    }
}