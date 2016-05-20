package com.yihu.ehr.util.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.AgAdminConstants;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.util.DateTimeUtils;
import com.yihu.ehr.util.Envelop;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by AndyCai on 2016/2/23.
 */
public class BaseController extends AbstractController {

    private static String ERR_SYSREM_DES = "系统错误,请联系管理员!";

    private static String X_Total_Count = "X-Total-Count";

    @Autowired
    public ObjectMapper objectMapper;

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        return null;
    }

    /**
     * 将实体转换为模型。
     *
     * @param source
     * @param targetCls
     * @param properties
     * @param <T>
     * @return
     */
    public <T> T convertToModel(Object source, Class<T> targetCls, String... properties) {
        if (source == null) {
            return null;
        }
        T target = BeanUtils.instantiate(targetCls);
        BeanUtils.copyProperties(source, target, propertyDiffer(properties, targetCls));

        return target;
    }

    /**
     * 将实体集合转换为模型集合。
     *
     * @param sources
     * @param targets
     * @param properties
     * @param <T>
     * @return
     */
    public <T> Collection<T> convertToModels(Collection sources, Collection<T> targets, Class<T> targetCls, String properties) {
        if (sources == null) {
            return null;
        }
        Iterator iterator = sources.iterator();
        while (iterator.hasNext()) {
            Object source = iterator.next();

            T target = (T) BeanUtils.instantiate(targetCls);
            BeanUtils.copyProperties(source, target, propertyDiffer(properties == null ? null : properties.split(","), targetCls));
            targets.add(target);
        }

        return targets;
    }

    public <T> T toEntity(String json, Class<T> entityCls) {
        try {
            T entity = objectMapper.readValue(json, entityCls);
            return entity;
        } catch (IOException ex) {
            throw new ApiException(ErrorCode.SystemError, "无法转换json, " + ex.getMessage());
        }
    }

    public String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return null;
        }
    }


    /**
     * 计算不在类中的属性。
     *
     * @return
     */
    protected String[] propertyDiffer(String[] properties, Class targetCls) {
        if (properties == null || properties.length == 0) return null;

        PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(targetCls);
        List<String> propertiesList = Arrays.asList(properties);
        List<String> arrayList = new ArrayList<>();

        for (PropertyDescriptor targetPd : targetPds) {
            Method writeMethod = targetPd.getWriteMethod();

            if (writeMethod != null && !propertiesList.contains(targetPd.getName())) {
                arrayList.add(targetPd.getName());
            }
        }

        return arrayList.toArray(new String[arrayList.size()]);
    }

    public Envelop getResult(List detaiModelList, int totalCount, int currPage, int rows) {

        Envelop result = new Envelop();
        result.setSuccessFlg(true);
        result.setDetailModelList(detaiModelList);
        result.setTotalCount(totalCount);
        result.setCurrPage(currPage);
        result.setPageSize(rows);
        if (result.getTotalCount() % result.getPageSize() > 0) {
            result.setTotalPage((result.getTotalCount() / result.getPageSize()) + 1);
        } else {
            result.setTotalPage(result.getTotalCount() / result.getPageSize());
        }

        return result;
    }

    public String trimEnd(String param, String trimChars) {
        if (param.endsWith(trimChars)) {
            param = param.substring(0, param.length() - trimChars.length());
        }
        return param;
    }

    public String trimStart(String param, String trimChars) {
        if (param.startsWith(trimChars)) {
            param = param.substring(trimChars.length(), param.length());
        }
        return param;
    }

    public Envelop failed(String errMsg) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        envelop.setErrorMsg(errMsg);
        return envelop;
    }

    public Envelop success(Object object) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        envelop.setObj(object);
        return envelop;
    }

    protected Envelop failedSystem() {
        return failed(ERR_SYSREM_DES);
    }

    protected int getTotalCount(ResponseEntity responseEntity) {
        return Integer.parseInt(responseEntity.getHeaders().get(X_Total_Count).get(0));
    }

    public <T> T getEnvelopModel(String jsonData, Class<T> targetCls) {
        try {
            Envelop envelop = objectMapper.readValue(jsonData, Envelop.class);
            String objJsonData = objectMapper.writeValueAsString(envelop.getObj());
            T model = objectMapper.readValue(objJsonData, targetCls);

            return model;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public <T> Collection<T> getEnvelopList(String jsonData, Collection<T> targets, Class<T> targetCls) {
        try {
            Envelop envelop = objectMapper.readValue(jsonData, Envelop.class);
            for (int i = 0; i < envelop.getDetailModelList().size(); i++) {
                String objJsonData = objectMapper.writeValueAsString(envelop.getDetailModelList().get(i));
                T model = objectMapper.readValue(objJsonData, targetCls);

                targets.add(model);
            }
            return targets;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }

    /**
     * 将字符串转为Data
     *
     * @param dateTime   日期字符串
     * @param formatRule 转换格式
     * @return 时间格式的日期
     */
    public Date StringToDate(String dateTime, String formatRule) {
        try {
            return dateTime == null ? null : DateTimeUtils.utcDateTimeParse(dateTime);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 将日期转为字符串
     *
     * @param dateTime   日期
     * @param formatRule 转换格式
     * @return 日期字符串
     */
    public String DateToString(Date dateTime, String formatRule) {
        try {
            return dateTime == null ? null : DateTimeUtils.utcDateTimeFormat(dateTime);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 计算出生日期
     *
     * @param birthday
     * @return
     */
    public static int getAgeByBirthday(Date birthday) {

        if (birthday == null)
            return 0;

        Calendar cal = Calendar.getInstance();

        if (cal.before(birthday)) {
            return 0;
        }

        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH) + 1;
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

        cal.setTime(birthday);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH) + 1;
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirth;

        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                // monthNow==monthBirth
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--;
                }
            } else {
                // monthNow>monthBirth
                age--;
            }
        }
        return age;
    }


    /**
     * 网关前端对象转微服务对象
     *
     * @param source
     * @param targetCls
     * @param properties
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T convertToMModel(Object source, Class<T> targetCls, String... properties) throws Exception {
        if (source == null) {
            return null;
        }
        T target = BeanUtils.instantiate(targetCls);
        BeanUtils.copyProperties(source, target, propertyDiffer(properties, targetCls));
        Field[] targetFields = target.getClass().getDeclaredFields();    // 获取target的所有属性，返回Field数组
        for (Field targetField : targetFields) {
            String name = targetField.getName();
            Object type = targetField.getGenericType();
            targetField.setAccessible(true);
            if (type == java.util.Date.class) {
                Field sourceField = source.getClass().getDeclaredField(name);
                sourceField.setAccessible(true);
                targetField.set(target, StringToDate(sourceField.get(source).toString(), AgAdminConstants.DateTimeFormat));
            }
        }
        return target;
    }

    /**
     * 网关微服务对象转前端对象
     *
     * @param source
     * @param targetCls
     * @param properties
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T convertToModelDetail(Object source, Class<T> targetCls, String... properties) throws Exception {
        if (source == null) {
            return null;
        }
        T target = BeanUtils.instantiate(targetCls);
        BeanUtils.copyProperties(source, target, propertyDiffer(properties, targetCls));
        Field[] sourceFields = source.getClass().getDeclaredFields();    // 获取source的所有属性，返回Field数组
        for (Field sourceField : sourceFields) {
            String name = sourceField.getName();
            Object type = sourceField.getGenericType();
            sourceField.setAccessible(true);
            if (type == java.util.Date.class) {
                Field targetField = target.getClass().getDeclaredField(name);
                targetField.setAccessible(true);
                targetField.set(target, DateToString((Date) sourceField.get(source), AgAdminConstants.DateTimeFormat));
            }
        }
        return target;
    }
}
