package com.yihu.ehr.model.packs;

import com.yihu.ehr.constants.ArchiveStatus;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.02.01 16:11
 */
public class MJsonPackage implements Serializable {
    public String id;
    public String pwd;
    public String remotePath;
    public String message;
    public Date receiveDate;
    public Date parseDate;
    public Date finishDate;
    public ArchiveStatus archiveStatus;
}
