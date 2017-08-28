package com.yihu.ehr.model.resource;

/**
 * Created by janseny on 2017/08/26
 * 指标统计预览上卷下钻
 */
public class MReportDimension {

    private String name;        //名称
    private String code;        //字段编码
    private String isCheck;     //是否选中
    private String isMain;      //是否是主维度

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(String isCheck) {
        this.isCheck = isCheck;
    }

    public String getIsMain() {
        return isMain;
    }

    public void setIsMain(String isMain) {
        this.isMain = isMain;
    }
}
