package com.yihu.ehr.util;

import com.yihu.ehr.util.log.LogService;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Property文件加载器.
 * @author Sand
 * @version 1.0
 * @created 2015.08.05 14:45
 */
public class PropertyLoader {

    public static Properties loadFile(final String fileName){
        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader = null;
        Properties properties = null;

        try {
            fileInputStream = new FileInputStream(fileName);
            inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");

            properties = new Properties();
            properties.load(inputStreamReader);

            return properties;
        } catch (IOException ex) {
            LogService.getLogger(PropertyLoader.class.getName()).error(ex.getMessage());
        } finally {
            try {
                if (inputStreamReader != null) inputStreamReader.close();
                if (fileInputStream != null) fileInputStream.close();
            } catch (Exception ex) {
                LogService.getLogger(PropertyLoader.class.getName()).error(ex.getMessage());
            }
        }

        return properties;
    }
}
