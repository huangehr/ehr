package com.yihu.ehr.util.file;

import java.io.*;

/**
 * @author Air
 * @version 1.0
 * @created 2015.06.25 14:14
 */
public class FileUtil {

    public static boolean writeFile(String filePath, String data, String encoding) throws IOException {
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                return false;
            }
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, encoding);
        outputStreamWriter.write(data);
        outputStreamWriter.flush();
        outputStreamWriter.close();
        return true;
    }

    public static boolean writeFile(String filePath, byte[] bytes, String encoding) throws IOException {
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                return false;
            }
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        byte[] bbuf = new byte[1024];
        InputStream fis = new ByteArrayInputStream(bytes);
        int hasRead = 0;
        //循环从输入流中取出数据
        while ((hasRead = fis.read(bbuf)) > 0) {
            fileOutputStream.write(bbuf, 0, hasRead);
        }
        fileOutputStream.close();

        return true;
    }

    public static boolean writeFile(String filePath,InputStream fis, String encoding) throws IOException {
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                return false;
            }
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        byte[] bbuf = new byte[1024];
        int hasRead = 0;
        //循环从输入流中取出数据
        while ((hasRead = fis.read(bbuf)) > 0) {
            fileOutputStream.write(bbuf, 0, hasRead);
        }
        fileOutputStream.close();

        return true;
    }


    /**
     * 删除整个目录
     *
     * @param dir 目录
     * @return boolean
     * @created Airhead
     */
    public static boolean deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDirectory(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    /**
     * file转string
     * @param file 文件
     * @return string类型流
     */
    public static String convertFileToString(File file) {

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

    public static String streamToString(InputStream inputStream) {

        StringBuilder sb = new StringBuilder();
        InputStreamReader read = null;
        try {
            read = new InputStreamReader(inputStream, "UTF-8");
        } catch (UnsupportedEncodingException e) {
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
        return sb.toString();
    }


    /**
     * 清空文件内容
     * @param filePath 文件路径
     * @param content 写入内容
     */
    public static void clearInfoForFile(String filePath,String content) {
        File file =new File(filePath);
        try {
            if(!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter =new FileWriter(file);
            fileWriter.write(content);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    /**
     * 记录文件最后读取的位置
     * @param readPath  被读取文件路径
     * @param savePath  保存被读取文件的最后位置 的文件路径
     */
    public static Integer  saveFilePos(String  readPath,  String  savePath)  {
        boolean result=false;
        File readFile=new File(readPath);
        Integer lenth=null;
        try  {
            if(readFile.exists()){
                InputStream inputStream = new FileInputStream(readPath);
                lenth = inputStream.available();
                clearInfoForFile(savePath,"");//清空内容
                writeFile(savePath,lenth.toString(),"UTF-8");//重新写入标识
            }
        }
        catch  (Exception  e)  {
            e.printStackTrace();
        }
        return lenth;
    }

    public static Integer getFileSize(String path) {
        Integer size=0;
        InputStream inputStream=null;
        File file=new File(path);
        if (file.exists()){
            try {
                inputStream = new FileInputStream(path);
                size = inputStream.available();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return size;
    }

    /**
     * 读取文本文件内容
     * @param file  文件路径
     * @return
     */
    public static String readFileText(File file) {
        StringBuilder stringBuilder = new StringBuilder();
        InputStream in = null;
        BufferedReader br = null;
        try {
            in = new FileInputStream(file);
            br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                }
            }
        }
        return stringBuilder.toString();
    }



    public static  byte[] getBytesByStream(InputStream inputStream){
        byte[] buffer = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1024];
            int n;
            while ((n = inputStream.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * 获得指定文件的byte数组
     */
    public static  byte[] getBytes(String filePath){
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    // 将byte数组转换成InputStream
    public static InputStream byteTOInputStream(byte[] in) throws Exception {
        ByteArrayInputStream is = new ByteArrayInputStream(in);
        return is;

    }

    /**
     * 获取文件后缀名
     * @param file
     * @return
     */
    public static String getSuffix(File file){
        String suffix = file.getName().substring(file.getName().lastIndexOf(".") + 1);
        return suffix;
    }

}
