package com.yihu.ehr.entity;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Date;

/**
 * Listener - 创建日期、修改日期处理
 * Created by progr1mmer on 2017/11/17.
 */
public class AssignedEntityListener {

    /**
     * 保存前处理
     *
     * @param entity
     *            基类
     */
    @PrePersist
    public void prePersist(BaseAssignedEntity entity) {
        entity.setCreateDate(new Date());
        entity.setModifyDate(new Date());
    }

    /**
     * 更新前处理
     *
     * @param entity
     *            基类
     */
    @PreUpdate
    public void preUpdate(BaseAssignedEntity entity) {
        entity.setModifyDate(new Date());
    }

}
