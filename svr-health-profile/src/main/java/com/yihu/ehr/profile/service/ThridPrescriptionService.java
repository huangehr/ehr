package com.yihu.ehr.profile.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.config.FastDFSConfig;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.profile.model.Template;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.log.LogService;
import freemarker.template.Configuration;
import freemarker.template.SimpleDate;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.fit.cssbox.demo.ImageRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.*;

import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Created by Administrator on 2016/6/14.
 */
@Service
public class ThridPrescriptionService extends BaseJpaService<Template, XTemplateRepository> {
    @Autowired
    private FastDFSConfig FastDFSConfig;

    @Autowired
    ProfileCDAService cdaService;

    @Autowired
    TemplateService tempService;

    @Autowired
    ProfileCDAService profileCDAService;

    Map<String, String> usageMap;

    public ThridPrescriptionService() {
        usageMap = new HashMap<>();

        usageMap.put("bid", "每日两次");
        usageMap.put("biw", "每周两次");
        usageMap.put("Hs", "临睡前");
        usageMap.put("q12 h", "每12小时一次");
        usageMap.put("q1 h", "每1小时一次");
        usageMap.put("q3 h", "每3小时一次");
        usageMap.put("q6 h", "每6小时一次");
        usageMap.put("q8 h", "每8小时一次");
        usageMap.put("qd", "每日一次");
        usageMap.put("qid", "每日四次");
        usageMap.put("qod", "每隔天一次");
        usageMap.put("qw", "每周一次");
        usageMap.put("St", "马上");
    }

    /**
     * 把html转图片存fastdfs上并且返回fdfs的路径
     *
     * @param eventNo
     * @param orgCode
     * @return
     * @throws Exception
     */
    public String prescriptioToImage(String eventNo, String orgCode, String cdaType, String version, int width, int height) throws Exception {
        String filePath = "";
        //查找处方笺判断处方签书是否存在 如果不存在查找处方签主表数据，新增处方签细表，存在的话判断处方签是否有图片

        //InputStream in = new FileInputStream(new File("E:/1.png"));
        //FastDFSUtil fdfs= FastDFSConfig.fastDFSUtil();
        //ObjectNode jsonResult = fdfs.upload(in, "png", "");
        //查找处方主表得到主表信息判断中药或者西药

        //根据主表信息调用中药或者西药

        //判断之前图片是否有生成 有的话直接返回路径
        // filePath=CDAToImage(eventNo,orgCode,cdaType,version,width,height);

        Map<String, Object> params = new HashMap<String, Object>();//ProfileDataSetSerializer
        params.put("status", "200");
        List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();

        for (int i = 0; i < 5; i++) {
            Map<String, Object> paramss = new HashMap<String, Object>();//ProfileDataSetSerializer
            paramss.put("type", "png");
            paramss.put("photo", "http://172.19.103.54/group1/M00/00/25/rBFuWldjdF-ABzPUAADQ9Y6xFp8579.png");
            lists.add(paramss);
        }
        params.put("photos", lists);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(params);
    }

    /**
     * 转换图片并保存到fastdfs
     *
     * @param profileId  rowkey
     * @param orgCode    机构代码
     * @param cdaVersion cda版本号
     * @param cdaCode    cda代码
     * @param width      宽度
     * @param height     高度
     * @return
     * @throws Exception
     */
    public String transformImage(String profileId, String orgCode, String cdaVersion, String cdaCode, String type, int width, int height) throws Exception {
        //获取CDA模板信息
        Template temp = tempService.getPresriptionTemplate(orgCode, cdaVersion, cdaCode);

        if (temp == null) {
            LogService.getLogger("prescription").error("CDA template not existed");
            throw new Exception("找不到对应CDA模板信息");
        }

        //获取CDA数据
        Map<String, Object> model = profileCDAService.getCDAData(profileId, temp.getCdaDocumentId());

        return CDAToImage(model, type, width, height);
    }


    /**
     * 图片生成
     *
     * @return
     * @throws Exception
     */
    public String CDAToImage(Map<String, Object> model, String type, int width, int height) throws Exception {
        try {
            //返回文件路径
            String filePath = "";
            //模板路径
            String fileString = ThridPrescriptionService.class.getResource("/").getPath();

            if (type.equals("1")) {
                //模板路径
                fileString += "/templates/westerntemplate.ftl";
            } else {
                //模板路径
                fileString += "/templates/chinesetemplate.ftl";
            }

            if (model.containsKey("data_sets") && ((Map<String, Object>) model.get("data_sets")).containsKey("HDSC01_09")) {
                List list = (List) ((Map<String, Object>) model.get("data_sets")).get("HDSC01_09");

                for (int i = 0; i < list.size(); i++) {
                    Map<String, Object> HDSC01_09 = (Map<String, Object>) list.get(i);

                    if (HDSC01_09.containsKey("HDSD00_04_006") && !StringUtils.isBlank(HDSC01_09.get("HDSD00_04_006").toString())) {
                        HDSC01_09.put("HDSD00_04_006", HDSC01_09.get("HDSD00_04_006").toString().substring(0, 10));
                    }
                }
            }

            if (model.containsKey("data_sets") && ((Map<String, Object>) model.get("data_sets")).containsKey("HDSA00_01")) {
                List list = (List) ((Map<String, Object>) model.get("data_sets")).get("HDSA00_01");

                for (int i = 0; i < list.size(); i++) {
                    Map<String, Object> HDSA00_01 = (Map<String, Object>) list.get(i);

                    if (HDSA00_01.containsKey("HDSA00_01_017") && !StringUtils.isBlank(HDSA00_01.get("HDSA00_01_017").toString())) {
                        Date current = new Date();
                        String year = HDSA00_01.get("HDSA00_01_017").toString().substring(6, 10);
                        String month = HDSA00_01.get("HDSA00_01_017").toString().substring(10, 12);
                        String day = HDSA00_01.get("HDSA00_01_017").toString().substring(12, 14);
                        Date birth = DateTimeUtil.simpleDateParse(year + "-" + month + "-" + day);
                        int age = current.getYear() - birth.getYear();
                        if (current.getMonth() < birth.getMonth()) {
                            age--;
                        }
                        HDSA00_01.put("HDSA00_01_012", age);
                    }
                }
            }

            if (model.containsKey("data_sets") && ((Map<String, Object>) model.get("data_sets")).containsKey("HDSC01_04")) {
                List list = (List) ((Map<String, Object>) model.get("data_sets")).get("HDSC01_04");

                for (int i = 0; i < list.size(); i++) {
                    Map<String, Object> HDSC01_04 = (Map<String, Object>) list.get(i);

                    if (HDSC01_04.containsKey("HDSD00_04_026_VALUE") && !StringUtils.isBlank(HDSC01_04.get("HDSD00_04_026_VALUE").toString())) {
                        if (!StringUtils.isBlank(usageMap.get(HDSC01_04.get("HDSD00_04_026_VALUE")))) {
                            HDSC01_04.put("HDSD00_04_026_VALUE", usageMap.get(HDSC01_04.get("HDSD00_04_026_VALUE")));
                        }
                    }
                }
            }

            //把数据和模板结合生成html
            String html = fillTemplate(fileString, model);
            //网页转图片 并且保存到fastdfs
            filePath = htmlToImage(html, width, height);

            return filePath;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * html转图片
     *
     * @return
     * @throws Exception
     */
    public String htmlToImage(String html, Integer width, Integer height) throws Exception {
        try {
            String rootPath = ThridPrescriptionService.class.getResource("/").getPath();
            rootPath = new File(new File(rootPath).getParent()).getParent();
            //把模板保存成文件
            String fileTempName = UUID.randomUUID().toString();
            String url = rootPath + fileTempName + ".html";
            File fileTmep = new File(url);//临时文件保存模板文件
            fileTmep.createNewFile();
            FileUtils.writeStringToFile(fileTmep, html);

            //随机生成图片名字ID
            String fileName = UUID.randomUUID().toString();
            File file = new File(rootPath + fileName + ".png");//临时文件保存图片
            file.createNewFile();
            FileUtils.writeStringToFile(file, html);
            ImageRenderer render = new ImageRenderer();
            FileOutputStream out = new FileOutputStream(file);
            if (width != 0 || height != 0) {
                Dimension d = new Dimension();
                d.setSize(width, height);//设置图片大小
                render.setWindowSize(d, false);//false 超出图片设置的大小是自适应
            }
            render.renderURL("file:///" + url, out, ImageRenderer.Type.PNG);
            //保存到fastdfs
            InputStream in = new FileInputStream(file);
            FastDFSUtil fdfs = FastDFSConfig.fastDFSUtil();
            ObjectNode jsonResult = fdfs.upload(in, "png", "");
            String filePath = jsonResult.get("fid").textValue();

            out.close();
            in.close();
            //删除图片文件
            file.delete();
            //删除临时html文件
            fileTmep.delete();
            return filePath;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 填充模板
     *
     * @param fileString
     * @param model
     * @return
     * @throws Exception
     */
    public String fillTemplate(String fileString, Object model) throws Exception {
        try {
            //模板文件
            File tempFile = new File(fileString);
            //模板加载配置
            Configuration cfg = new Configuration();
            //模板路径配置
            cfg.setDirectoryForTemplateLoading(new File(tempFile.getParent()));
            //加载模板文件
            freemarker.template.Template temp = cfg.getTemplate(tempFile.getName());
            //模板解析输出流
            Writer out = new StringWriter();
            //模板解析
            temp.process(model, out);
            //返回解析后HTML
            return out.toString();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

}
