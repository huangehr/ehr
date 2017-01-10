package com.yihu.ehr.model.security;

import com.yihu.ehr.model.app.MApp;

import java.util.Date;
import java.util.Set;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.03.17 10:11
 */
public class MToken {
    long id;
    String url;
    Set<String> scopes;
    String token;
    String tokenLastEight;
    String tokenHash;
    String note;
    String noteUrl;
    Date updatedAt;
    Date createdAt;
    String fingerprint;

    MApp app;
}
