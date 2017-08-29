package com.yihu.ehr.analysis.model;

import org.springframework.data.annotation.Id;

/**
 * Created by Administrator on 2017/2/9.
 * 数据对象的公工接口
 * // 业务日志
 * 0 consult // 咨询
 * 1 guidance  // 指导
 * 2 article  // 健康教育
 * 3 followup  // 随访
 * 4 appointment // 预约
 * 5 label // 标签
 * 6 register  // 注册
 * 7 archive // 健康档案
 {
      time:"" 时间
     ,logType:2 日志类型
     ,caller:"" 调用者
     ,data:{
         ,businessType:""  业务类型
         ,patient:"" 居民
         ,data:{} 业务数据
     } 数据
 }

 // 接口调用日志
 {
  time:"" 时间
 ,logType:1 日志类型
 ,caller:"" 调用者
 ,data:{
      responseTime:"" 响应时间
     ,url:"" 接口URL
     ,params:{} 参数
    } 数据
 }
 */
public class DataModel {
    @Id
    protected String id;
    protected String time;//时间
    protected String caller; //调用者
    protected String logType;//日志类型 1接口 2业务

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }
}
