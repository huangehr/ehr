package com.yihu.ehr.profile.controller;

import com.google.common.io.Files;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.profile.MTemplate;
import com.yihu.ehr.profile.model.Template;
import com.yihu.ehr.profile.service.TemplateService;
import com.yihu.ehr.util.compress.Zipper;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.util.log.LogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.04.08 9:45
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "健康档案模板服务", description = "维护/获取健康档案模板")
public class TemplateEndPoint extends BaseRestEndPoint {
    @Autowired
    TemplateService templateService;

    @ApiOperation(value = "创建模板")
    @RequestMapping(value = ServiceApi.ProfileTemplate.Templates, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.POST)
    public void saveTemplate(@ApiParam(value = "健康档案模板")
                             @RequestBody String model) {
        Template template = toEntity(model, Template.class);
        template.setCreateTime(new Date());
        templateService.save(template);
    }

    @ApiOperation(value = "获取模板列表", response = MTemplate.class, responseContainer = "List")
    @RequestMapping(value = ServiceApi.ProfileTemplate.Templates, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
    public Collection<MTemplate> getTemplates(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,title,cdaVersion,cdaDocumentId,organizationCode,createTime,pcTplURL,mobileTplURL")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+organizationCode,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小")
            @RequestParam(value = "size", defaultValue = "15", required = false) int size,
            @ApiParam(name = "page", value = "页码")
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ParseException {
        List<Template> templateList = templateService.search(fields, filters, sorts, page, size);

        pagedResponse(request, response, templateService.getCount(filters), page, size);

        return convertToModels(templateList, new ArrayList<>(templateList.size()), MTemplate.class, fields);
    }

    @ApiOperation(value = "获取模板")
    @RequestMapping(value = ServiceApi.ProfileTemplate.Template, method = RequestMethod.GET)
    public MTemplate getTemplate(@ApiParam(value = "模板ID")
                                 @PathVariable(value = "id") int id) {
        Template template = templateService.getTemplate(id);
        if (null == template) throw new ApiException(HttpStatus.NOT_FOUND, "Template not found");

        return convertToModel(template, MTemplate.class, null);
    }

    @ApiOperation(value = "判断模版名称是否已存在")
    @RequestMapping(value = ServiceApi.ProfileTemplate.TemplateTitleExistence, method = RequestMethod.GET)
    public boolean isNameExist(
            @ApiParam(name = "version", value = "版本")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "title", value = "标题")
            @RequestParam(value = "title") String title,
            @ApiParam(name = "org_code", value = "机构代码")
            @RequestParam(value = "org_code") String orgCode) {

        return templateService.isExistName(title, version, orgCode);
    }

    @ApiOperation(value = "更新模板属性")
    @RequestMapping(value = ServiceApi.ProfileTemplate.Template, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void getTemplate(@ApiParam(value = "模板ID")
                            @PathVariable(value = "id") int id,
                            @ApiParam(value = "模板JSON")
                            @RequestBody String model) {
        Template tpl = templateService.getTemplate(id);
        if (null == tpl) throw new ApiException(HttpStatus.NOT_FOUND, "Template not found");

        Template template = toEntity(model, Template.class);
        template.setId(id);

        templateService.save(template);
    }

    @ApiOperation(value = "下载模板展示文件")
    @RequestMapping(value = ServiceApi.ProfileTemplate.TemplateCtn, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, method = RequestMethod.GET)
    public void getTemplateContent(@ApiParam(value = "模板ID")
                                   @PathVariable(value = "id") int id,
                                   @ApiParam(value = "true表示PC端，false表示移动端")
                                   @RequestParam(value = "pc", defaultValue = "true") boolean pc,
                                   HttpServletResponse response) throws Exception {
        Template template = templateService.getTemplate(id);
        if (template == null) throw new ApiException(HttpStatus.NOT_FOUND, "Template not found");
        if (StringUtils.isEmpty(template.getPcTplURL()))
            throw new ApiException(HttpStatus.NOT_FOUND, "Template content is empty.");

        IOUtils.copy(new ByteArrayInputStream(template.getContent(pc)), response.getOutputStream());

        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader("Content-Disposition", "attachment; filename=" + template.getTitle() + ".html");
        response.flushBuffer();
    }

    @ApiOperation(value = "更新模板展示文件")
    @RequestMapping(value = ServiceApi.ProfileTemplate.TemplateCtn, method = RequestMethod.POST)
    public void setTemplateContent(@ApiParam(value = "模板ID")
                                   @PathVariable(value = "id") int id,
                                   @ApiParam(value = "true表示PC端，false表示移动端")
                                   @RequestParam(value = "pc", defaultValue = "true") boolean pc,
                                   @ApiParam(value = "展示文件")
                                   @RequestPart() MultipartFile file) throws Exception {
        Template template = templateService.getTemplate(id);
        if (template == null) throw new ApiException(HttpStatus.NOT_FOUND, "Template not found");

        InputStream stream = file.getInputStream();
        template.setContent(pc, stream);
        templateService.updateTemplate(template);
    }

    @ApiOperation(value = "删除模板")
    @RequestMapping(value = ServiceApi.ProfileTemplate.Template, method = RequestMethod.DELETE)
    public void deleteTemplate(@ApiParam(value = "模板ID")
                               @PathVariable(value = "id") int id) {
        templateService.deleteTemplate(id);
    }

    @ApiOperation(value = "打包下载模板", response = MTemplate.class, responseContainer = "List")
    @RequestMapping(value = ServiceApi.ProfileTemplate.TemplatesDownloads, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, method = RequestMethod.GET)
    public void downloadTemplates(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "organizationCode=41872607-9")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档")
            @RequestParam(value = "sorts", defaultValue = "+id", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小")
            @RequestParam(value = "size", defaultValue = "15", required = false) int size,
            @ApiParam(name = "page", value = "页码")
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {
        File tempFile = Files.createTempDir();
        String zipName = tempFile.getAbsolutePath() + ".zip";
        File zipFile = null;

        try {
            // download and zip templates
            List<Template> templateList = templateService.search("", filters, sorts, reducePage(page), size);
            for (Template template : templateList) {
                byte pc[] = template.getContent(true);
                byte mobile[] = template.getContent(false);

                writeTplContent(tempFile, template, "-pc.html", pc);
                writeTplContent(tempFile, template, "-mobile.html", mobile);
            }

            zipFile = new Zipper().zipFile(tempFile, zipName);

            // send file
            IOUtils.copy(new ByteArrayInputStream(FileUtils.readFileToByteArray(zipFile)), response.getOutputStream());

            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader("Content-Disposition", "attachment; filename=" + tempFile.getName() + ".zip");
            response.flushBuffer();
        } catch (IOException e) {
            String message = "Unable to download profile template, " + e.getMessage();

            LogService.getLogger().error(message);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, message);
        } finally {
            FileUtils.deleteQuietly(tempFile);
            FileUtils.deleteQuietly(zipFile);
        }
    }

    private void writeTplContent(File location, Template template, String suffix, byte content[]) throws IOException {
        if (content != null) {
            String fileName = location.getAbsolutePath() + File.separator + template.getId() + suffix;
            FileUtils.writeByteArrayToFile(new File(fileName), content);
        }
    }
}
