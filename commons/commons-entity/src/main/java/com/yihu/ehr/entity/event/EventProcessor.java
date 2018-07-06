package com.yihu.ehr.entity.event;

import com.yihu.ehr.entity.BaseIdentityEntity;

import javax.persistence.*;

/**
 * 事件处理器
 * Created by progr1mmer on 2018/7/5.
 */
@Entity
@Table(name = "event_processor")
@Access(value = AccessType.PROPERTY)
public class EventProcessor extends BaseIdentityEntity {

    private String name; //类名，包含完整包名
    private String processType; //处理器类型
    private boolean active; //活跃状态
    private String remote_path; //文件系统.class存放的路径，当运行环境上下文加载不到此类的时候则尝试从此加载

    @Column(name = "name", nullable = false, unique = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "process_type", nullable = false)
    public String getProcessType() {
        return processType;
    }

    public void setProcessType(String processType) {
        this.processType = processType;
    }

    @Column(name = "active", nullable = false)
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Column(name = "remote_path")
    public String getRemote_path() {
        return remote_path;
    }

    public void setRemote_path(String remote_path) {
        this.remote_path = remote_path;
    }
}
