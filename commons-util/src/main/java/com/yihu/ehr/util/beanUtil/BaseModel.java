package com.yihu.ehr.util.beanUtil;
import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * Model的基础类，model类必须继承这个类和Vo也可以继承这个类
 * Created by Administrator on 2016/1/14.
 */
public class BaseModel implements Serializable {
    public static final String	FUNCTION_COMPILE	= "compile";

    @Override
    public String toString() {
        Field[] field = this.getClass().getDeclaredFields();
        String s = "";
        try {
            for (Field f : field) {
                f.setAccessible(true);
                s += ","
                        + f.getName()
                        + "="
                        + (f.get(this) == null ? "null" : f.get(this)
                        .toString());
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return s;
    }

}
