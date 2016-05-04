package com.yihu.ehr.profile.persist.repo;

import com.yihu.ehr.data.hbase.ResultUtil;
import com.yihu.ehr.profile.annotation.Column;
import com.yihu.ehr.profile.annotation.Table;
import com.yihu.ehr.util.DateTimeUtils;
import org.apache.commons.lang3.EnumUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Date;

/**
 * @author Sand
 * @created 2016.05.03 15:25
 */
public class HBaseEntityUtil {
    /**
     * 根据注解，为实体装配指定的字段。
     *
     * @param result
     * @param entity
     * @param <T>
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws ParseException
     */
    public static <T> T assembleEntity(Object result, T entity)
            throws IllegalAccessException,
            InstantiationException,
            NoSuchMethodException,
            InvocationTargetException, ParseException {
        Class<T> cls = (Class<T>) entity.getClass();

        Table table = cls.getAnnotation(Table.class);
        if (table == null) return null;

        ResultUtil record = new ResultUtil(result);

        Method[] methods = cls.getMethods();
        for (Method method : methods){
            Column column = method.getAnnotation(Column.class);
            if (column == null) continue;

            Class returnType = method.getReturnType();
            String setterName = method.getName().replace("get", "set");
            Method setter = cls.getMethod(setterName, returnType);

            String value = record.getCellValue(column.family(), column.value(), null);
            Object targetValue = null;
            if (returnType == Enum.class){
                targetValue = returnType.getEnumConstants()[Integer.parseInt(value)];
            } else if (returnType == Date.class){
                targetValue = DateTimeUtils.utcDateTimeParse(value);
            } else {
                targetValue = value;
            }

            setter.invoke(entity, returnType.cast(targetValue));
        }

        return entity;
    }
}
