package com.yihu.ehr.web;

import com.yihu.ehr.constants.ArchiveStatus;

import java.beans.PropertyEditorSupport;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.09.22 12:40
 */
public class ArchiveStatusConverter extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        ArchiveStatus archiveStatus = ArchiveStatus.valueOf(text);
        setValue(archiveStatus);
    }
}
