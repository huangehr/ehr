//package com.yihu.ehr.util.ParmUtils;
//
//import net.sf.json.JSONArray;
//import net.sf.json.JSONObject;
//import net.sf.json.util.JSONUtils;
//
//import java.lang.reflect.Field;
//import java.lang.reflect.Method;
//import java.lang.reflect.ParameterizedType;
//import java.lang.reflect.Type;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by lincl                    on 2016/1/28.
// */
//public class JSONUtil {
//    public static Object jsonToObj(String json, Class clz){
//        JSONObject jsonObject = JSONObject.fromObject(json);
//        return jsonObjectToObj(jsonObject,clz);
//    }
//
//    public static Object jsonObjectToObj(JSONObject jsonObject, Class clz){
//        Object model = null;
//        try {
//            model = clz.newInstance();
//            Object obj;
//            Field f;
//            Method m;
//            String key;
//            for(Object k:jsonObject.keySet()){
//                key = (String)k;
//                obj = jsonObject.get(k);
//                try {
//                    String methodStr = "set"+firstLetterToUpper(key);
//                    f = clz.getDeclaredField(key);
//
//                    if(JSONUtils.isArray(obj)){
//                        Type[] params = ((ParameterizedType) f.getGenericType()).getActualTypeArguments();
//                        JSONArray arr = (JSONArray) obj;
//                        List rs = new ArrayList<>();
//                        for(Object o:arr){
//                            if(isNotObject(o))
//                                rs.add(o);
//                            else
//                                rs.add(jsonObjectToObj(((JSONObject) o), ((Class) params[0])));
//                        }
//                        obj = rs;
//                    }
//                    else if(JSONUtils.isObject(obj)){
//                        if(f.getType().equals(Map.class) ){
//                            Type[] params = ((ParameterizedType) f.getGenericType()).getActualTypeArguments();
//                            JSONObject tmp = (JSONObject) obj;
//                            Map map = new HashMap<>();
//                            for(Object i:tmp.keySet()){
//                                if(isNotObject(tmp.get(i)))
//                                    map.put(i, tmp.get(i));
//                                else
//                                    map.put(i, jsonObjectToObj(tmp.getJSONObject(((String) i)), (Class) params[1]));
//                            }
//                            obj = map;
//                        }
//                        else {
//                            obj = jsonObjectToObj((JSONObject) obj, f.getType());
//                        }
//                    }
//                    m = clz.getDeclaredMethod(methodStr, f.getType());
//                    m.invoke(model, obj);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return model;
//    }
//
//    public static boolean isNotObject(Object o){
//        if(JSONUtils.isObject(o) || JSONUtils.isArray(o))
//            return false;
//        return true;
//    }
//    public static String firstLetterToUpper(String str){
//        if(str==null || "".equals(str.trim())){
//            return "";
//        }
//        return str.replaceFirst((""+str.charAt(0)), (""+str.charAt(0)).toUpperCase());
//    }
//
//    public static void main(String[] args){
//        String json = "{page:1, rows:10,filters:{code:{col:'code', logic:'like',  val:[1,2]}}}";
//        jsonToObj(json, ParmModel.class);
//    }
//}
