package com.yihu.ehr.util.classpool;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;
import org.springframework.util.StringUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 对象池工具类
 *
 * 目前提供ORM动态映射解决方案并生成class  文件
 *
 * @author lincl
 * @version 1.0
 * @created 2016/2/15
 *
 */
public class ClassPoolUtils {


    /**
     * 运行时动态ORM表映射
     *
     *
     * @param entityClassName   待映射的实体全限定类名
     * @param tableName         待映射的表名
     * @param newClzName        新的实体全限定类名
     * @return                   映射后的类对象
     */
    public static Class<?> tableMapping(
            String entityClassName, String tableName, String newClzName)
            throws Exception {
        if(StringUtils.isEmpty(entityClassName) || StringUtils.isEmpty(tableName)){
            throw new IllegalArgumentException("The mapping parameter is invalid!");
        }
        ClassPool classPool = ClassPool.getDefault();
        classPool.appendClassPath(new ClassClassPath(ClassPoolUtils.class));

        classPool.importPackage("javax.persistence");
        CtClass clazz = classPool.get(entityClassName);
        clazz.defrost();
        ClassFile classFile = clazz.getClassFile();
        ConstPool constPool = classFile.getConstPool();

        // set annootation property
        AnnotationsAttribute attribute = (AnnotationsAttribute)classFile.getAttribute(AnnotationsAttribute.visibleTag);
        if(attribute==null)
            attribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        Annotation tableAnnotation = new Annotation("javax.persistence.Table", constPool);
        tableAnnotation.addMemberValue("name", new StringMemberValue(tableName, constPool));
        attribute.addAnnotation(tableAnnotation);

        tableAnnotation = new Annotation("javax.persistence.Entity", constPool);
        attribute.addAnnotation(tableAnnotation);

        classFile.addAttribute(attribute);
        classFile.setVersionToJava5();

        //rename class name
        clazz.replaceClassName(entityClassName, newClzName);

        //save class file
        DataOutputStream dataOutputStream =
                new DataOutputStream(
                        new FileOutputStream(createFile(newClzName)));
        classFile.write(dataOutputStream);
        dataOutputStream.flush();
        dataOutputStream.close();
        EntityClassLoader loader = new EntityClassLoader(ClassPoolUtils.class.getClassLoader());

        return clazz.toClass(loader , null);
    }

    public static File createFile(String clzName) throws IOException {
        String splitMark = System.getProperty("file.separator");
        String path = System.getProperty("user.home") +splitMark+ "ehr"  +splitMark+ "std" + splitMark;
        String clzPath = clzName.substring(0, clzName.lastIndexOf(".")).replace(".", splitMark);
        String filePath = path + clzPath;
        File pathFile = new File(filePath);
        if(!pathFile.exists())
            System.err.println(pathFile.mkdirs());
        String simpleName = clzName.substring(clzName.lastIndexOf(".") + 1 )+".class";
        File file = new File(filePath, simpleName);
        if(file.exists())
            file.delete();
        file.createNewFile();
        return file;
    }

    public static String classNameTofilePath(String clzName) {
        String clzPath = System.getProperty("user.home") + "/ehr/std/";
        File file = new File(clzPath);
        boolean rs = file.exists();
        System.err.println(rs);
        if(!rs)
            System.err.println(file.mkdir());

        String path = ClassPoolUtils.class.getClassLoader().getResource("").getPath();
        if(path.indexOf("test-classes")!=-1){
            path = path.replace("test-classes", "classes");
        }
        return clzPath
                + clzName.replace(".", "/") + ".class";
    }
}

