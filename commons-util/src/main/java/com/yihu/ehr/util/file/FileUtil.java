package com.yihu.ehr.util.file;

import com.yihu.ehr.util.encode.Base64;

import java.io.*;

/**
 * @author Air
 * @version 1.0
 * @created 2015.06.25 14:14
 */
public class FileUtil {
    public static boolean writeFile(String filePath, String data, String encoding) throws IOException {
        File file = new File(filePath);
//        if (file.exists()) {
//            return false;
//        }

        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                return false;
            }
        }

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, encoding);
        byte[] bytes = Base64.decode(data);
        byte[] bbuf = new byte[1024];
        InputStream fis = new ByteArrayInputStream(bytes);

        int hasRead = 0;
        while ((hasRead = fis.read(bbuf)) > 0) {
            //每读取一次，即写入文件输出流，读了多少，就写多少
            fileOutputStream.write(bbuf, 0, hasRead);
        }

        outputStreamWriter.flush();
        fileOutputStream.close();
        outputStreamWriter.close();

        return true;
    }


    /**
     * 将文件的String类型二进制流转成文件
     *
     * @param filePath
     * @param data
     * @param encoding
     * @return
     * @throws IOException
     */
    public static boolean writeFileByString(String filePath, String data, String encoding) throws IOException {
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                return false;
            }
        }

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        byte[] bytes = Base64.decode(data);
        byte[] bbuf = new byte[1024];
        InputStream fis = new ByteArrayInputStream(bytes);
        int hasRead = 0;
        //循环从输入流中取出数据
        while ((hasRead = fis.read(bbuf)) > 0) {
            //每读取一次，即写入文件输出流，读了多少，就写多少
            fileOutputStream.write(bbuf, 0, hasRead);
        }
        fileOutputStream.close();
        return true;
    }

    /**
     * InputStream 转 byte[]
     *
     * @param input
     * @return
     * @throws IOException
     */
    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }


    /**
     * 通过文件途径，将文件转成byte[] 流
     *
     * @param filepath 文件路径
     * @return
     * @throws IOException
     */
    public static byte[] toByteArray(String filepath) throws IOException {
        File file = new File(filepath);
        InputStream input = new FileInputStream(file);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }


    /**
     * 通过文件途径，将文件转成String二进制流  流
     *
     * @param filepath 文件路径
     * @return
     * @throws IOException
     */
    public static String file2Base64String(String filepath) throws IOException {
        File file = new File(filepath);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return Base64.encode(buffer);
    }

    /**
     * file转string
     * @param fileName 文件名
     * @return string类型流
     */
    public static String convertFileToString(String fileName) {

        File file = new File(fileName);
        StringBuilder sb = new StringBuilder();
        if(file.isFile()&&file.exists()) {
            InputStreamReader read = null;
            try {
                read = new InputStreamReader(new FileInputStream(file), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            BufferedReader reader = new BufferedReader(read);
            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    /**
     * 复制一个指定文件夹下的指定文件 到另一个指定文件夹下
     * @param path 复制文件路径
     * @param toPath 文件路径
     */
    public static void copyAllFileByPath(String path, String toPath) throws IOException {

        File file = new File(path);
        File toFile = new File(toPath);
        byte[] b = new byte[(int) file.length()];
        if(file.isFile()){
            try {
                FileInputStream is= new FileInputStream(file);
                FileOutputStream ps= new FileOutputStream(toFile);
                is.read(b);
                ps.write(b);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(file.isDirectory()){
            if(!file.exists()) {
                file.mkdir();
            }
            if(!toFile.exists()) {
                toFile.mkdir();
            }
            String[] list = file.list();
            for(int i=0;i<list.length;i++){
                copyAllFileByPath(path + "/" + list[i], toPath + "/" + list[i]);
            }
        }
    }
}
