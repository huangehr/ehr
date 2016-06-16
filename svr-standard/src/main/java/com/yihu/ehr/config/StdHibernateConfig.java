package com.yihu.ehr.config;


import com.yihu.ehr.standard.datasets.service.BaseDataSet;
import com.yihu.ehr.util.ClassPoolUtils;
import com.yihu.ehr.util.ExtJdbcTemplate;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate4.HibernateTransactionManager;

import javax.sql.DataSource;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author lincl
 * @version 1.0
 * @created 2016.2.19
 */
@Configuration
public class StdHibernateConfig {

    @Autowired
    DataSource dataSource;

    public static Map<String, String> vesionedEntitys = new HashMap<>();
    static{
        vesionedEntitys.put("com.yihu.ehr.standard.datasets.service.DataSet", "std_data_set_");
        vesionedEntitys.put("com.yihu.ehr.standard.datasets.service.MetaData", "std_meta_data_");
        vesionedEntitys.put("com.yihu.ehr.standard.dict.service.Dict", "std_dictionary_");
        vesionedEntitys.put("com.yihu.ehr.standard.dict.service.DictEntry", "std_dictionary_entry_");
        vesionedEntitys.put("com.yihu.ehr.standard.document.service.CDADocument", "std_cda_document_");
        vesionedEntitys.put("com.yihu.ehr.standard.document.service.CDADataSetRelationship", "std_cda_data_set_relationship_");
    }

    private static void addPath(File f) throws Exception {
        URL u = f.toURI().toURL();
        URLClassLoader urlClassLoader = (URLClassLoader) BaseDataSet.class.getClassLoader();
        Class urlClass = URLClassLoader.class;
        Method method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
        method.setAccessible(true);
        method.invoke(urlClassLoader, new Object[]{u});
    }

    @Bean
    public StdSessionFactoryBean sessionFactory() throws Exception {

        List<Class> tableClass = createEntity(dataSource);
        StdSessionFactoryBean sessionFactory = new StdSessionFactoryBean();
        //bind entity to session
        sessionFactory.setAnnotatedClasses(tableClass.toArray(new Class[tableClass.size()]));
        sessionFactory.setDataSource(dataSource);
        sessionFactory.getHibernateProperties().setProperty("hibernate.show_sql", "true");
        sessionFactory.getHibernateProperties().setProperty("hibernate.format_sql", "true");

        String splitMark = System.getProperty("file.separator");
        String path = System.getProperty("user.home") +splitMark+ "ehr" + splitMark + "std" + splitMark;
        addPath(new File(path));
        return sessionFactory;
    }

    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory){
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory);
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }


    @Bean
    public ExtJdbcTemplate jdbcTemplate(){

        return  new ExtJdbcTemplate(dataSource);
    }

    //创建基于版本的实体类
    private List<Class> createEntity(DataSource dataSource) throws Exception{
        List<Class> tableClass = new ArrayList<>();
        Connection c = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try {
            c = dataSource.getConnection();
            preparedStatement = c.prepareStatement("SELECT version FROM std_cda_versions");
            rs = preparedStatement.executeQuery();
            String version = "";
            while (rs.next()){
                version = rs.getString(1);
                for (String vesionedEntity : vesionedEntitys.keySet()){
                    tableClass.add(
                            ClassPoolUtils.tableMapping(
                                    vesionedEntity,
                                    vesionedEntitys.get(vesionedEntity) + version,
                                    vesionedEntity + version));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("create entity failure!");
        } finally {
            rs.close();
            preparedStatement.close();
            c.close();
        }
        return tableClass;
    }
}
