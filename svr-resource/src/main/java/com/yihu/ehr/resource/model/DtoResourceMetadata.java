package com.yihu.ehr.resource.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by hzp on 2016/5/14.
 */
public class DtoResourceMetadata extends RsResourceMetadata {

    private String statsType; //统计类型
    private String dimensionValue; //维度范围


}
