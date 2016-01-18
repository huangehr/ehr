package com.yihu.ehr.util.beanUtil;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * vo和model相互转换
 * Created by Administrator on 2016/1/14.
 */
public class BeanUtils {

    private static final Set<Class<?>>	SIMPLE_TYPE_SET	= new HashSet<Class<?>>();

    static {
        SIMPLE_TYPE_SET.add(Character.class);
        SIMPLE_TYPE_SET.add(String.class);
        SIMPLE_TYPE_SET.add(Integer.class);
        SIMPLE_TYPE_SET.add(Long.class);
        SIMPLE_TYPE_SET.add(Float.class);
        SIMPLE_TYPE_SET.add(Double.class);
        SIMPLE_TYPE_SET.add(Boolean.class);
        SIMPLE_TYPE_SET.add(java.util.Date.class);
        SIMPLE_TYPE_SET.add(java.sql.Date.class);
        SIMPLE_TYPE_SET.add(java.sql.Timestamp.class);
        SIMPLE_TYPE_SET.add(BigDecimal.class);
    }

    /**
     * model转换为vo
     * @param vo
     * @param model
     * @param <T>
     * @return
     */
    public static <T extends Object> T copyModelToVo(Class<T> vo, BaseModel model) {
        T v = null;
        try {
            v = vo.newInstance();
            copy(model, v);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return v;
    }

    /**
     * model转换为vo的list
     * @param vo
     * @param models
     * @param <T>
     * @return
     */
    public static <T extends Object> List<T> copyModelToVo(Class<T> vo,
                                                           List<? extends BaseModel> models) {
        List<T> list = new ArrayList<T>();
        if (models != null) {
            for (BaseModel model : models) {
                list.add(copyModelToVo(vo, model));
            }
        }
        return list;
    }

    /**
     * vo转换为model
     * @param model
     * @param vo
     * @param <T>
     * @return
     */
    public static <T extends BaseModel> T copyVoToModel(Class<T> model,
                                                        Object vo) {
        T m = null;
        try {
            m = model.newInstance();
            copy(model, m);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return m;
    }

    /**
     * vo转换为model list
     * @param model
     * @param vos
     * @param <T>
     * @return
     */
    public static <T extends BaseModel> List<T> copyVoToModel(Class<T> model,
                                                              List<? extends Object> vos) {
        List<T> list = new ArrayList<T>();
        if (vos != null) {
            for (Object vo : vos) {
                list.add(copyVoToModel(model, vo));
            }
        }
        return list;
    }

    private static Object copy(Object source, Object target) {
        if (target == null || source == null)
            return null;
        // 获得源文件的class
        Class<?> sourceClass = source.getClass();
        // 获得要转换后的目标文件的class
        Class<?> targetClass = target.getClass();
        // 获得源文件的的fields
        Field[] sourceFields = sourceClass.getDeclaredFields();

        for (int i = 0; i < sourceFields.length; i++) {
            try {
                // 获得目标文件的Field
                Field temp = targetClass.getDeclaredField(sourceFields[i]
                        .getName());
                // 根据field获得属性名称
                String fieldName = temp.getName();
                // 获得属性的类型
                Class<?> fieldType = temp.getType();
                // 获得属性的get,set方法
                String getMethodName = "get"
                        + fieldName.substring(0, 1).toUpperCase()
                        + fieldName.substring(1);
                String setMethodName = "set"
                        + fieldName.substring(0, 1).toUpperCase()
                        + fieldName.substring(1);
                Method getMethod = sourceClass.getMethod(getMethodName);
                Method setMethod = targetClass.getMethod(setMethodName,
                        new Class[] { fieldType });
                Object value = getMethod.invoke(source);
                // 判断value的类型
                if (value != null) {
                    if (SIMPLE_TYPE_SET.contains(value.getClass())) {
                        if (value instanceof String) {
                            String s = (String) value;
                            value = s != null ? s.trim() : null;
                        }
                        if (value instanceof String
                                && !fieldType.equals(String.class)) {
                            value = stringToObj(fieldType, (String) value);
                        }
                        if (!(value instanceof String)
                                && fieldType.equals(String.class)) {
                            value = objToString(value);
                        }
                        setMethod.invoke(target, new Object[] { value });
                    } else if (value instanceof Collection) {
                    } else {
                        Object obj = copy(value, fieldType.newInstance());
                        setMethod.invoke(target, new Object[] { obj });
                    }
                } else {
                    setMethod.invoke(target, new Object[] { null });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return target;
    }

    private static Object stringToObj(Class<?> clazz, String value) {
        Object obj = null;
        if (value == null)
            return null;
        if (clazz.equals(Integer.class)) {
            obj = Integer.valueOf(value);
        } else if (clazz.equals(Long.class)) {
            obj = Long.valueOf(value);
        } else if (clazz.equals(Float.class)) {
            obj = Float.valueOf(value);
        } else if (clazz.equals(Double.class)) {
            obj = Double.valueOf(value);
        } else if (clazz.equals(Boolean.class)) {
            obj = Boolean.valueOf(value);
        } else if (clazz.equals(BigDecimal.class)) {
            obj = new BigDecimal(value);
        } else if (clazz.equals(java.util.Date.class)) {
            try {
                if (value.length() == 19) {
                    java.text.DateFormat df = new java.text.SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss");
                    obj = df.parse(value);
                } else if (value.length() == 16) {
                    java.text.DateFormat df = new java.text.SimpleDateFormat(
                            "yyyy-MM-dd HH:mm");
                    obj = df.parse(value);
                } else {
                    java.text.DateFormat df = new java.text.SimpleDateFormat(
                            "yyyy-MM-dd");
                    obj = df.parse(value);
                }
            } catch (Exception e) {
            }
        } else if (clazz.equals(Character.class)) {
            obj = value.charAt(0);
        }

        return obj;
    }

    private static String objToString(Object obj) {
        String value = null;
        if (obj == null)
            return null;
        if (obj instanceof java.util.Date) {
            java.text.DateFormat df = new java.text.SimpleDateFormat(
                    "yyyy-MM-dd");
            value = df.format((java.util.Date) obj);
        } else {
            value = obj.toString();
        }
        return value;
    }

    public static void main(String[] args) {

        VoUsers resUser = new VoUsers();
        ResUser user = new ResUser();
        user.setUSER_ID("12");
        user.setUSER_NAME("hh");
        user.setUSER_PASSWORD("123");
        // 将model转换为vo
        resUser = BeanUtils.copyModelToVo(resUser.getClass(), user);
        // 输出vo的值
        System.err.println(resUser.toString());
    }
}
