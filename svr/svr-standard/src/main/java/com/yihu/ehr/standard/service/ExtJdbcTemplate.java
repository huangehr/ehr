package com.yihu.ehr.standard.service;

import com.yihu.ehr.query.ReturnIdPstCreator;
import com.yihu.ehr.query.UpdatePstCallback;
import javafx.util.Pair;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/5/6
 */
public class ExtJdbcTemplate extends JdbcTemplate {

    public ExtJdbcTemplate(DataSource dataSource){
        super(dataSource);
    }

    public Object insert(Object obj) throws InvocationTargetException, IllegalAccessException, SQLException, NoSuchFieldException, NoSuchMethodException {
        Table table = obj.getClass().getAnnotation(Table.class);
        if(table == null)
            throw new  IllegalStateException("NO BIND TABLE!");

        List<Pair<Type, Object>> values = new ArrayList<>();
        StringBuilder sb = new StringBuilder("INSERT INTO " + table.name() + "(");
        StringBuilder valToken = new StringBuilder();

        Method[] methods = obj.getClass().getMethods();
        Method idMtd = null;
        for(Method method : methods){
            if(method.getAnnotation(Id.class) != null){
                idMtd = method;
                GenericGenerator genericGenerator = method.getAnnotation(GenericGenerator.class);
                if(genericGenerator != null && "increment".equals(genericGenerator.strategy())){
                    continue;
                }
            }
            Column column = method.getAnnotation(Column.class);
            if(column != null){
                sb.append(column.name() + ",");
                valToken.append("?,");
                values.add(new Pair<>(method.getReturnType(), method.invoke(obj)));
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(") VALUES("+ valToken.deleteCharAt(valToken.length()-1) +")");

        int id = execute(
                    new ReturnIdPstCreator(sb.toString()),
                    new UpdatePstCallback(values));

//        getDataSource().getConnection().close();
        return setId(obj, idMtd, id);
    }

    private Object setId(Object obj, Method idMtd, int id) throws InvocationTargetException, IllegalAccessException, NoSuchFieldException, NoSuchMethodException {
        Method idSetMtd = obj.getClass().getMethod(idMtd.getName().replace("get", "set"), idMtd.getReturnType());
//        Field idfile = obj.getClass().getField(idMtd.getName().replace("get", "").toLowerCase());
        if(idMtd.getReturnType().equals(Long.class) || idMtd.getReturnType().equals(Long.TYPE))
            idSetMtd.invoke(obj, (long) id);
        else
            idSetMtd.invoke(obj, id);
        return obj;
    }
}
