package com.yihu.ehr.model.common;

import java.lang.reflect.Field;

/**
 * Created by hzp on 2017/03/07.
 * 基础对象
 */
public class Result {
    protected boolean successFlg = true;
    protected String message;
    protected int code;

    public boolean isSuccessFlg() {
        return successFlg;
    }

    public void setSuccessFlg(boolean successFlg) {
        this.successFlg = successFlg;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    /**
     * 错误消息
     */
    public static Result error(String message) {
        Result re= new Result();
        re.successFlg = false;
        re.message = message;
        re.code = -1;
        return re;
    }

    /**
     * 错误消息
     */
    public static Result error(int code,String message) {
        Result re= new Result();
        re.successFlg = false;
        re.message = message;
        re.code = code;
        return re;
    }

    /**
     * 成功消息
     */
    public static Result success(String message) {
        Result re= new Result();
        re.successFlg = true;
        re.message = message;
        re.code = 200;
        return re;
    }

    /**
     * 成功消息
     */
    public static ObjectResult success(String message,Object data) {
        ObjectResult re= new ObjectResult();
        re.successFlg = true;
        re.message = message;
        re.code = 200;
        re.setData(data);
        return re;
    }

    public void checkValue() {
        try {
            Field[] fields = this.getClass().getDeclaredFields();
            for (int j = 0; j < fields.length; j++) {
                fields[j].setAccessible(true);
                if (fields[j].getType().getName().equals(String.class.getTypeName())
                        && fields[j].get(this) == null){
                    fields[j].set(this, "");//设置为空字串
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
