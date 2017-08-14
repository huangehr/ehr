package com.yihu.ehr.agModel.resource;

import java.io.Serializable;
import java.util.List;

/**
 * 资源报表分类 model
 *
 * @author 张进军
 * @date 2017/8/9 17:20
 */
public class RsReportCategoryModel implements Serializable {

    private Integer id; // 主键
    private Integer pid; // 父级ID
    private String code; // 编码
    private String name; // 名称
    private String remark; // 备注
    private List<RsReportCategoryModel> children;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<RsReportCategoryModel> getChildren() {
        return children;
    }

    public void setChildren(List<RsReportCategoryModel> children) {
        this.children = children;
    }
}
