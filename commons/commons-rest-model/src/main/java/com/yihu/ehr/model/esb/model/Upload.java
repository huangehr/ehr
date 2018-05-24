package com.yihu.ehr.model.esb.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * ElasticSearch:archive_relation
 * Created by progr1mmer on 2018/4/11.
 */
public class Upload implements Serializable {

    //--------------------es必定不为空的字段-----------------------------------------
    private String _id; //重传或者补传为profile_id,否则为uuid
    private Integer origin;//0为重传,即reuploadFlag为true    1为补传(事件时间晚于当前时间2天)    2查询索引失败  3查询主表失败     4查询细表失败
    private Date create_date;
    private String schema_version;//适配省平台版本号
    private Integer failed_count;//失败次数
    private String failed_message;//失败信息
    private Date repeat_date ;//重传时间

    //--------------------es可能为空的字段-----------------------------------------
    private Date start_date;//开始时间
    private Date end_date;//结束时间
    private Date event_date;//事件时间
    private String row_key;
    private String table;//平台数据集code
    private String upload_table;//待上传的数据集code


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Integer getOrigin() {
        return origin;
    }

    public void setOrigin(Integer origin) {
        this.origin = origin;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }

    public String getSchema_version() {
        return schema_version;
    }

    public void setSchema_version(String schema_version) {
        this.schema_version = schema_version;
    }

    public Integer getFailed_count() {
        return failed_count;
    }

    public void setFailed_count(Integer failed_count) {
        this.failed_count = failed_count;
    }

    public String getFailed_message() {
        return failed_message;
    }

    public void setFailed_message(String failed_message) {
        this.failed_message = failed_message;
    }

    @JsonFormat(pattern="yyyy-MM-dd", timezone = "GMT+8")
    public Date getRepeat_date() {
        return repeat_date;
    }

    public void setRepeat_date(Date repeat_date) {
        this.repeat_date = repeat_date;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public String getRow_key() {
        return row_key;
    }

    public void setRow_key(String row_key) {
        this.row_key = row_key;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getEvent_date() {
        return event_date;
    }

    public void setEvent_date(Date event_date) {
        this.event_date = event_date;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getUpload_table() {
        return upload_table;
    }

    public void setUpload_table(String upload_table) {
        this.upload_table = upload_table;
    }
}
