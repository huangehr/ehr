package com.yihu.ehr.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;

/**
 * ��־�����ࡣĿǰʹ�õ��� log4j2����2.X�汾��log4j,�����������汾����,��Ϊ���ǵ������ļ����ط�ʽ��һ��.
 *
 * @apiNote �����漰��Spring��ܵĳ�ʼ��, ��������log4j�ĳ�ʼ��. �����log4j����δ��ʼ��֮ǰ��Spring Bean��������
 * log4j����, ���ܻ���ɴ���. ����: Spring MVC ��Controller���ֿ��ܻỺ��һ��Logger����,����ʱlog4j��δ���õ�, ���Ի�����쳣.
 * ��ȷ��������Spring��Bean�ڲ�Ҫ�������Logger����,����lo4j��ʼ����������ʱȥ��ȡLogger����.
 *
 * @author Air
 * @author Sand
 * @version 1.0
 * @created 2015.06.18 16:59
 */
@Service("com.yihu.ehr.log.LogService")
public class LogService {
    static{
        try {
            String configPath = LogService.class.getResource("/").getPath() + "log4j/log4j2.xml";

            ConfigurationSource source;
            source = new ConfigurationSource(new FileInputStream(configPath));

            Configurator.initialize(null, source);
        } catch (Exception ex) {
            // �˴������ٵ���������Ϊ��־��¼����, ��Ϊ��ʱ��ʼ��ʧ����
            LogManager.getLogger(LogService.class).error(ex.getMessage());
        }
    }


    public static Logger getLogger(final Class<?> clazz){
        return LoggerFactory.getLogger(clazz);
    }

    public static Logger getLogger(final String name){
        return LoggerFactory.getLogger(name);
    }

    public static Logger getLogger(){
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        if(stackTraceElements == null && stackTraceElements.length > 3){
            return LoggerFactory.getLogger(LogService.class);
        } else{
            return LoggerFactory.getLogger(stackTraceElements[2].getClassName());
        }
    }
}
