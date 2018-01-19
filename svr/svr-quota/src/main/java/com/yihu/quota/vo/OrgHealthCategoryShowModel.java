package com.yihu.quota.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.searchbox.annotations.JestId;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

/**
 * Created by janseny on 2017/12/28.
 */
public class OrgHealthCategoryShowModel {

    private String orgHealthCategoryCode;     //卫生机构类型代码
    private String orgHealthCategoryName;   //卫生机构类型名字
    private String orgHealthCategoryId;     //卫生机构类型ID
    private String orgHealthCategoryPid;    //卫生机构类型父节点
    private String cloumnKey1;              //表格第1列
    private String cloumnKey2;              //表格第2列
    private String cloumnKey3;
    private String cloumnKey4;
    private String cloumnKey5;
    private String cloumnKey6;
    private String cloumnKey7;
    private String cloumnKey8;
    private String cloumnKey9;

    public String getOrgHealthCategoryCode() {
        return orgHealthCategoryCode;
    }

    public void setOrgHealthCategoryCode(String orgHealthCategoryCode) {
        this.orgHealthCategoryCode = orgHealthCategoryCode;
    }

    public String getOrgHealthCategoryName() {
        return orgHealthCategoryName;
    }

    public void setOrgHealthCategoryName(String orgHealthCategoryName) {
        this.orgHealthCategoryName = orgHealthCategoryName;
    }

    public String getOrgHealthCategoryId() {
        return orgHealthCategoryId;
    }

    public void setOrgHealthCategoryId(String orgHealthCategoryId) {
        this.orgHealthCategoryId = orgHealthCategoryId;
    }

    public String getOrgHealthCategoryPid() {
        return orgHealthCategoryPid;
    }

    public void setOrgHealthCategoryPid(String orgHealthCategoryPid) {
        this.orgHealthCategoryPid = orgHealthCategoryPid;
    }

    public String getCloumnKey1() {
        return cloumnKey1;
    }

    public void setCloumnKey1(String cloumnKey1) {
        this.cloumnKey1 = cloumnKey1;
    }

    public String getCloumnKey2() {
        return cloumnKey2;
    }

    public void setCloumnKey2(String cloumnKey2) {
        this.cloumnKey2 = cloumnKey2;
    }

    public String getCloumnKey3() {
        return cloumnKey3;
    }

    public void setCloumnKey3(String cloumnKey3) {
        this.cloumnKey3 = cloumnKey3;
    }

    public String getCloumnKey4() {
        return cloumnKey4;
    }

    public void setCloumnKey4(String cloumnKey4) {
        this.cloumnKey4 = cloumnKey4;
    }

    public String getCloumnKey5() {
        return cloumnKey5;
    }

    public void setCloumnKey5(String cloumnKey5) {
        this.cloumnKey5 = cloumnKey5;
    }

    public String getCloumnKey6() {
        return cloumnKey6;
    }

    public void setCloumnKey6(String cloumnKey6) {
        this.cloumnKey6 = cloumnKey6;
    }

    public String getCloumnKey7() {
        return cloumnKey7;
    }

    public void setCloumnKey7(String cloumnKey7) {
        this.cloumnKey7 = cloumnKey7;
    }

    public String getCloumnKey8() {
        return cloumnKey8;
    }

    public void setCloumnKey8(String cloumnKey8) {
        this.cloumnKey8 = cloumnKey8;
    }

    public String getCloumnKey9() {
        return cloumnKey9;
    }

    public void setCloumnKey9(String cloumnKey9) {
        this.cloumnKey9 = cloumnKey9;
    }
}
