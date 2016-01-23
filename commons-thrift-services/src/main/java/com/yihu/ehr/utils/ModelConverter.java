package com.yihu.ehr.utils;

import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * Thrift data model and business model converter.
 *
 * @author Sand
 * @version 1.0
 * @created 2016.01.23 9:01
 */
public class ModelConverter {
    /**
     * Convert business object to thrift model object.
     *
     * @param bizObj
     * @param modelClz
     * @param <T>
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public <T> T toThriftModel(Object bizObj, Class<T> modelClz) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        T model = modelClz.newInstance();

        BeanUtils.copyProperties(model, bizObj);

        return model;
    }

    /**
     * Convert thrift object to business object, which is used internal only.
     * 
     * @param model
     * @param bizClz
     * @param <T>
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public <T> T toBusinessObject(Object model, Class<T> bizClz) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        T bizObject = bizClz.newInstance();

        BeanUtils.copyProperties(bizClz, model);

        return bizObject;
    }
}
