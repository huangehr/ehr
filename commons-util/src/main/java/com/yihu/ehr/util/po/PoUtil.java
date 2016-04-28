package com.yihu.ehr.util.po;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2016/2/23.
 */
public class PoUtil {



//    public static void main(String[] args) throws Exception {
//        User user = new User();
//        user.setId("123");
//        user.setName("testUser");
//        user.setAddress("beijing");
//        user.setPassword("123456");
//        String sql = getHql(user,"name","address");
//        System.out.printf(sql);
//    }

    /**
     * 通过Bean对象获取查询语句
     * @param obj 对象
     * @return Hql
     */
    public static String getHql(Object obj,String... fieldNames) throws Exception {
        List<String> properties = Arrays.asList(fieldNames);
        StringBuffer strBuffer = new StringBuffer(" from "+ obj.getClass().getSimpleName()+" where 1=1");
        Class<? extends Object> objClass = obj.getClass();
        Field fields[] = objClass.getDeclaredFields();
        for (Field field:fields) {
            String fieldValue = getFieldValue(obj, field);
            if(!properties.contains(field.getName())){
                if (fieldValue!=null ) {
                    strBuffer.append(" and "+field.getName()+"='"+fieldValue+"'");
                }else{
                    strBuffer.append(" and ("+field.getName()+" is null)");
                }
            }
        }
        return strBuffer.toString();
    }

    public static String getFieldValue(Object obj, Field field) throws Exception {
        String name = field.getName();
        String methodName = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
        Method method = obj.getClass().getMethod(methodName);
        Object methodValue = method.invoke(obj);
        if(methodValue!=null){
            return methodValue.toString();
        }
        return null;
    }

    static class User {
        String id;
        String name;
        String password;
        String address;

        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }
        public String getAddress() {
            return address;
        }
        public void setAddress(String address) {
            this.address = address;
        }
    }


}
