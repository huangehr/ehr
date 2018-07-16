package com.yihu.ehr.event.classloader;


import com.yihu.ehr.entity.event.EventProcessor;
import com.yihu.ehr.event.service.EventProcessorService;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by progr1mmer on 2018/7/4.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class EventClassLoader extends ClassLoader {

    @Value("${fast-dfs.public-server:http://127.0.0.1:8080}")
    private String publicServer;

    @Autowired
    private EventProcessorService eventProcessorService;
    @Autowired
    private FastDFSUtil fastDFSUtil;

    @Override
    protected Class findClass(String name) throws ClassNotFoundException {
        Class clazz = findLoadedClass(name);
        if (clazz != null) {
            return clazz;
        }
        EventProcessor packEventProcessor = eventProcessorService.findByName(name);
        if (null == packEventProcessor || StringUtils.isEmpty(packEventProcessor.getRemotePath())) {
            throw new ClassNotFoundException("java.lang.ClassNotFoundException: " + name);
        }
        String remote_path = packEventProcessor.getRemotePath();
        String extension = remote_path.substring(remote_path.lastIndexOf(".") + 1);
        switch (extension) {
            case "class":
                byte [] _clazz;
                try {
                    _clazz = fastDFSUtil.download(remote_path.split(":")[0], remote_path.split(":")[1]);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new ClassNotFoundException(name);
                }
                clazz = defineClass(name, _clazz, 0, _clazz.length);
                return clazz;
            case "jar":
                try {
                    //URLClassLoader loader = new URLClassLoader(new URL[]{new URL("file:///D:/event.jar")}); //此方式需要该路径底下包含完整的包名文件夹
                    URLClassLoader loader = new URLClassLoader(new URL[]{new URL(publicServer  + "/" + remote_path.replaceAll(":", "/"))}); //从方式需要该路径底下包含完整的包名文件夹
                    clazz = loader.loadClass(name);
                    return clazz;
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new ClassNotFoundException(name);
                }
            default:
                throw new ClassNotFoundException(name);
        }
    }
}
