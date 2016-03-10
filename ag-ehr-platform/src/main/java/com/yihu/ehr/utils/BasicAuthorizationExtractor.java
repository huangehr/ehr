package com.yihu.ehr.utils;

import javafx.util.Pair;

import java.security.InvalidParameterException;
import java.util.Base64;

/**
 * Created by Sand Wen on 2016.3.9.
 */
public class BasicAuthorizationExtractor {
    public static Pair<String, String> extract(String authorization){
        if(null == authorization) throw new InvalidParameterException("Need basic authentication.");

        String base64 = authorization.substring(authorization.indexOf(' ') + 1);

        String compose = new String(Base64.getDecoder().decode(base64));
        int index = compose.indexOf(':');

        return new Pair<>(compose.substring(0, index), compose.substring(index + 1));
    }
}
