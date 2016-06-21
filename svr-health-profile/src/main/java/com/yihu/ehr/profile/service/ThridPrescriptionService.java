package com.yihu.ehr.profile.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.config.FastDFSConfig;
import com.yihu.ehr.data.hbase.HBaseDao;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.query.BaseJpaService;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.cache.WebappTemplateLoader;
import freemarker.template.Configuration;
import org.apache.commons.io.FileUtils;
import org.fit.cssbox.demo.ImageRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ExceptionDepthComparator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Created by Administrator on 2016/6/14.
 */
@Service
@Transactional
public class ThridPrescriptionService extends BaseJpaService<Template, XTemplateRepository> {
    @Autowired
    private FastDFSConfig FastDFSConfig;
    //返回图片的ip地址
    @Value("${returnurl}")
    private String returnUrl;



    /**
     * 把html转图片存fastdfs上并且返回fdfs的路径
     * @param eventNo
     * @param orgCode
     * @return
     * @throws Exception
     */
    public String prescriptioToImage(String eventNo, String orgCode,String cdaType,String version,int width,int height) throws Exception {
        String filePath="";
        //查找处方笺判断处方签书是否存在 如果不存在查找处方签主表数据，新增处方签细表，存在的话判断处方签是否有图片

        //InputStream in = new FileInputStream(new File("E:/1.png"));
        //FastDFSUtil fdfs= FastDFSConfig.fastDFSUtil();
        //ObjectNode jsonResult = fdfs.upload(in, "png", "");
            //查找处方主表得到主表信息判断中药或者西药

            //根据主表信息调用中药或者西药

            //判断之前图片是否有生成 有的话直接返回路径
       // filePath=CDAToImage(eventNo,orgCode,cdaType,version,width,height);

        Map<String,Object> params=new HashMap<String,Object>();//ProfileDataSetSerializer
        params.put("status","200");
        List<Map<String,Object>> lists=new ArrayList<Map<String,Object>>();

        for (int i=0;i<5;i++){
            Map<String,Object> paramss=new HashMap<String,Object>();//ProfileDataSetSerializer
            paramss.put("type","png");
            paramss.put("photo","http://172.19.103.54/group1/M00/00/25/rBFuWldjdF-ABzPUAADQ9Y6xFp8579.png");
            lists.add(paramss);
        }
        params.put("photos",lists);
        ObjectMapper objectMapper=new ObjectMapper();
        return objectMapper.writeValueAsString(params);
    }

    /**
     * 根据eventNo找到CDA数据 然后根据orgCode,cdaType,version得到模板，把数据填充模板转成图片上传到fdfs返回fdfs上的路径,
     * 此方法是公共方法
     * @param eventNo 事件号
     * @param orgCode 机构code
     * @param cdaType cda类别
     * @param version 模板版本
     * @param width 生成的图片宽度
     * @param height 生成图片高度
     * @return
     * @throws Exception
     */
    private String CDAToImage(String eventNo, String orgCode,String cdaType,String version,int width,int height) throws Exception {
        String filePath="";
        try{
            //根据机构ID,CDA类别，版本得到模板
            String fileString="";
            //根据事件号和机构ID得到处方数据,数据格式是CDA格式
            Object model="";
            //把数据和模板结合
            String html=fillTemplate(fileString,model);
            //网页转图片 并且保存到fastdfs
            filePath = htmlToImage(html,width,height);
            return filePath;
        }catch (Exception e){
            throw new Exception("html转图片失败");
        }
    }
    /**
     * html转图片
     * @return
     * @throws Exception
     */
    private String htmlToImage(String html,Integer width,Integer height) throws Exception {
        try{
            //把模板保存成文件
            String fileTempName= UUID.randomUUID().toString();
            String url =ThridPrescriptionService.class.getResource("/").getPath()+fileTempName+".html";
            File fileTmep=new File(url);//临时文件保存模板文件
            fileTmep.createNewFile();
            FileUtils.writeStringToFile(fileTmep,html);

            //随机生成图片名字ID
            String fileName= UUID.randomUUID().toString();
            File file=new File(fileName+".png");//临时文件保存图片
            file.createNewFile();
            FileUtils.writeStringToFile(file,html);
            ImageRenderer render = new ImageRenderer();
            FileOutputStream out = new FileOutputStream(file);
            Dimension d = new Dimension();
            d.setSize(width,height);//设置图片大小
            render.setWindowSize(d,false);//false 超出图片设置的大小是自适应
            render.renderURL("file://"+url, out, ImageRenderer.Type.PNG);
            //保存到fastdfs
            InputStream in = new FileInputStream(file);
            FastDFSUtil fdfs= FastDFSConfig.fastDFSUtil();
            ObjectNode jsonResult = fdfs.upload(in, "zip", "");
            String filePath = jsonResult.get("fid").textValue();

            out.close();
            in.close();
            //删除图片文件
            file.delete();
            //删除临时html文件
            fileTmep.delete();
            return filePath;
        }catch (Exception e){
            throw new Exception("html转图片失败");
        }
    }

    /**
     * 填充模板
     * @param fileString
     * @param model
     * @return
     * @throws Exception
     */
    private String fillTemplate(String fileString,Object model) throws Exception{
        try{
            Configuration cfg = new Configuration();
            // 在哪个文件夹下找ftl模板文件 // 在哪个文件夹下找ftl模板文件
            StringTemplateLoader t=new StringTemplateLoader();
            t.putTemplate("test",fileString);
            cfg.setTemplateLoader(t);
            // 加载ftl文件
            freemarker.template.Template temp = cfg.getTemplate("test");
            cfg.setTemplateLoader(t);
            // 加载ftl文件
            Writer out = new StringWriter(2048);
            temp.process(model, out);
            return out.toString();
        }catch (Exception e){
            throw new Exception("填充模板失败");
        }
    }

}
