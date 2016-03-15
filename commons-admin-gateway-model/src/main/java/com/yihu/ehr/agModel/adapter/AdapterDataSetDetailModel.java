package com.yihu.ehr.agModel.adapter;

import com.yihu.ehr.util.validate.Required;
import com.yihu.ehr.util.validate.Valid;

/**
 * Created by AndyCai on 2016/3/15.
 */
@Valid
public class AdapterDataSetDetailModel {

    private Long id;

    private Long adapterPlanId;

    private Long dataSetId;

    private Long metaDataId;

    private String dataType;

    private Long orgDataSetSeq;

    private Long orgMetaDataSeq;

    private String description;

    private Long stdDict;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Required(filedName = "适配方案")
    public Long getAdapterPlanId() {
        return adapterPlanId;
    }

    public void setAdapterPlanId(Long adapterPlanId) {
        this.adapterPlanId = adapterPlanId;
    }

    @Required(filedName = "标准数据集")
    public Long getDataSetId() {
        return dataSetId;
    }

    public void setDataSetId(Long dataSetId) {
        this.dataSetId = dataSetId;
    }

    @Required(filedName = "标准数据元")
    public Long getMetaDataId() {
        return metaDataId;
    }

    public void setMetaDataId(Long metaDataId) {
        this.metaDataId = metaDataId;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Long getOrgDataSetSeq() {
        return orgDataSetSeq;
    }

    public void setOrgDataSetSeq(Long orgDataSetSeq) {
        this.orgDataSetSeq = orgDataSetSeq;
    }

    public Long getOrgMetaDataSeq() {
        return orgMetaDataSeq;
    }

    public void setOrgMetaDataSeq(Long orgMetaDataSeq) {
        this.orgMetaDataSeq = orgMetaDataSeq;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getStdDict() {
        return stdDict;
    }

    public void setStdDict(Long stdDict) {
        this.stdDict = stdDict;
    }
}
