package com.yihu.ehr.agModel.tj;

import java.util.Date;

/**
 * Created by Administrator on 2017/6/13.
 */
public class TjQuotaModel {
    private Long id;
    private String code;
    private String name;
    private String cron;
    private String execType;
    private Date execTime;
    private String jobClazz;
    private Date createTime;
    private String createUser;
    private String createUserName;
    private Date updateTime;
    private String updateUser;
    private String updateUserName;
    private String status;
    private Integer dataLevel;
    private String remark;
    private TjQuotaDataSourceModel tjQuotaDataSourceModel;
    private TjQuotaDataSaveModel tjQuotaDataSaveModel;
}
