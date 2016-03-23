package com.yihu.ehr.config;

import com.mysql.jdbc.Driver;
import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;

import java.sql.SQLException;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.11.27 14:34
 */
@Configuration
public class HibernateConfig {
    @Value("${data-source.url}")
    String url;

    @Value("${data-source.user-name}")
    String userName;

    @Value("${data-source.password}")
    String password;

    @Value("${data-source.initialize-size}")
    int initialSize;

    @Value("${data-source.max-total}")
    int maxTotal;

    @Value("${data-source.max-idle}")
    int maxIdle;

    @Value("${data-source.min-idle}")
    int minIdle;

    @Value("${data-source.validation-query}")
    String validationQuery;

    @Value("${data-source.test-on-borrow}")
    boolean testOnBorrow;

    @Value("${data-source.remove-abandoned-timeout}")
    int removeAbandonedTimeout;

    @Bean
    public BasicDataSource dataSource() throws SQLException {
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setDriver(new Driver());
        basicDataSource.setUrl(url);
        basicDataSource.setUsername(userName);
        basicDataSource.setPassword(password);
        basicDataSource.setInitialSize(initialSize);
        basicDataSource.setMaxTotal(maxTotal);
        basicDataSource.setMaxIdle(maxIdle);
        basicDataSource.setMinIdle(minIdle);
        basicDataSource.setValidationQuery(validationQuery);
        basicDataSource.setTestOnBorrow(testOnBorrow);
        basicDataSource.setRemoveAbandonedTimeout(removeAbandonedTimeout);

        return basicDataSource;
    }

}
