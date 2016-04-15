package com.yihu.ehr.service;

import com.yihu.ehr.profile.core.Profile;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

/**
 * 包解析器。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.04.13 16:14
 */
public interface ProfileResolver {
    void resolve(Profile profile, File root) throws IOException, ParseException;
}
