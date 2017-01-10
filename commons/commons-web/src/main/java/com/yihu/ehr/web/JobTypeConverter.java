package com.yihu.ehr.web;

import java.beans.PropertyEditorSupport;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.09.22 12:40
 */
public class JobTypeConverter extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        //JobType jobType = JobType.valueOf(text);
        //setValue(jobType);
    }
}
