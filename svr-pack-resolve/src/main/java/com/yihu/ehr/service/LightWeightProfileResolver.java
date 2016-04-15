package com.yihu.ehr.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.yihu.ehr.profile.core.Profile;

import java.io.File;
import java.io.IOException;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.04.13 16:33
 */
public class LightWeightProfileResolver implements ProfileResolver {
    @Override
    public void resolve(Profile profile, File root) {
        /*File file = files[0];
        JsonNode jsonNode = objectMapper.readTree(file);
        if (jsonNode.isNull()) {
            throw new IOException("Invalid json file when generate data set");
        }

        //设置数据集
        dataSetResolver.parseLightJsonDataSet(profile, jsonNode);
        file.delete();*/
    }
}
