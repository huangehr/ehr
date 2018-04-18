package com.yihu.ehr.basic.util;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by 卓 on 2017/3/14.
 */
public class ClazzReflect {
    public Object formatToClazz(Object clazz, JSONObject jsonObject){
        Field[] field = clazz.getClass().getDeclaredFields(); // 获取实体类的所有属性，返回Field数组
            try {
            for (int j = 0; j < field.length; j++) { // 遍历所有属性
                String name = field[j].getName(); // 获取属性的名字
                String key = field[j].getName();
                if(jsonObject.isNull(name))continue;
                name = name.substring(0, 1).toUpperCase() + name.substring(1); // 将属性的首字符大写，方便构造get，set方法
                String type = field[j].getGenericType().toString(); // 获取属性的类型
                Method m = null;
                if (type.equals("class java.lang.String")) { // 如果type是类类型，则前面包含"class "，后面跟类名
                    String value = jsonObject.getString(key);
                    m = clazz.getClass().getMethod("set"+name,String.class);
                    m.invoke(clazz, value);
                }
                if (type.equals("class java.lang.Integer")) {
                    Integer value =  jsonObject.getInt(key);
                    m = clazz.getClass().getMethod("set"+name,Integer.class);
                    m.invoke(clazz, value);
                }
                if (type.equals("class java.lang.Boolean")) {
                    Boolean value =  jsonObject.getBoolean(key);
                    m = clazz.getClass().getMethod("set"+name,Boolean.class);
                    m.invoke(clazz, value);
                }
                if (type.equals("class java.lang.Long")) {
                    Long value =  getJsonLong(jsonObject,key);
                    m = clazz.getClass().getMethod("set"+name,Long.class);
                    m.invoke(clazz, value);
                }
                if (type.equals("class java.util.Date")) {
                    String valueString = jsonObject.getString(key);
                    Date value =  DateTimeUtil.simpleDateParse(valueString);
                    m = clazz.getClass().getMethod("set"+name,Date.class);
                    m.invoke(clazz, value);
                }// 如果有需要,可以仿照上面继续进行扩充,再增加对其它类型的判断
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }catch (ParseException e){
              e.printStackTrace();
        }
        return clazz;
    }

    private Long getJsonLong(JSONObject jsonObject, String key){
            Object object = jsonObject.get(key);
            if(object==null||"".equals(object)){
                return null;
            }else{
                return jsonObject.getLong(key);
            }
    }


}
