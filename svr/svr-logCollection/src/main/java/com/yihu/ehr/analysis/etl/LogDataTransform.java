package com.yihu.ehr.analysis.etl;

import com.yihu.ehr.analysis.entity.UserPortrait;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 日志信息分析提取
 * <p>
 * Created by lyr-pc on 2017/2/17.
 */
public class LogDataTransform {

    private static Logger logger = LoggerFactory.getLogger(LogDataTransform.class);

    // 日志提取分析类集合
    private Map<String, ILogTransform> transforms = new HashMap<>();

    private static LogDataTransform instance;

    private static Object obj = new Object();

    /**
     * 初始化
     */
    private LogDataTransform() {
        init();
    }

    /**
     * 获取日志清洗实例
     *
     * @return
     */
    public static LogDataTransform getLogTransform() {
        if (instance == null) {
            synchronized (obj) {
                if (instance == null) {
                    instance = new LogDataTransform();
                }
            }
        }
        return instance;
    }

    /**
     * 日志信息分析提取
     *
     * @param log
     */
    public List<UserPortrait> transform(JSONObject log) {
        try {
            String logType = String.valueOf(log.get("logType"));
            if (logType.equals("2")) {
                String businessType = String.valueOf(log.getJSONObject("data").get("businessType"));
                ILogTransform transform = transforms.get(businessType);

                if (transform == null) {
                    logger.error("logType:" + log.getString("logType") + " transform can not find");
                } else {
                    return transform.transform(log);
                }
            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return null;
        }
    }

    /**
     * 初始化加载日志提取分析类
     */
    public void init() {
        try {
            logger.info("log transform begin init");
            transforms.clear();
            // 当前文件路径
            String path = LogDataTransform.class.getResource(".").getPath();
            // 当前应用类路径
            String rootPath = LogDataTransform.class.getResource("/").getPath();
            File root = new File(rootPath);
            // 日志清洗类路径
            File fileEtl = new File(path, "transform");
            // 过滤日志清洗类文件
            File[] files = fileEtl.listFiles((pathname) -> {
                return pathname.getName().endsWith("Transform.class");
            });
            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            for (File file : files) {
                // 获取类名
                String className = file.getAbsolutePath()
                        .replace(root.getAbsolutePath() + "\\", "")
                        .replace("\\", ".")
                        .replace(".class", "");
                Class logClass = classLoader.loadClass(className);
                if (logClass != null) {
                    Object object = logClass.newInstance();
                    if (object instanceof ILogTransform) {
                        ILogTransform logTransform = (ILogTransform) object;
                        transforms.put(String.valueOf(logTransform.getLogType()), logTransform);
                    }
                }
            }
            logger.info("log transform init success");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("log transform init failed:" + e.getMessage());
        }
    }

    public static void main(String[] args) {
        LogDataTransform.getLogTransform();
    }
}
