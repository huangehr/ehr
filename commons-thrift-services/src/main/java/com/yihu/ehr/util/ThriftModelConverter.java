package com.yihu.ehr.util;

import org.apache.commons.beanutils.*;

import java.lang.reflect.InvocationTargetException;

/**
 * thrift 对象与实体对象之间的转换工具。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.01.14 16:48
 */
public class ThriftModelConverter {
    /**
     * 将实体中的属性复制到Thrift模型中。
     *
     * @param modelCls
     * @param entity
     * @param <T>
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     */
    public <T> T toModel(Object entity, Class<T> modelCls) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        T model = modelCls.newInstance();

        BeanUtils.copyProperties(model, entity);

        return model;
    }

    /**
     * 将Thrift模型中的属性复制到实体中。
     *
     * @param model
     * @param entityCls
     * @param <T>
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     */
    public <T> T toEntity(Object model, Class<T> entityCls) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        T entity = entityCls.newInstance();

        BeanUtils.copyProperties(entity, model);

        return entity;
    }
}
