package com.yihu.ehr.profile.controller.template;

import com.google.common.io.Files;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.profile.model.ArchiveTemplate;
import com.yihu.ehr.profile.service.template.ArchiveTemplateService;
import com.yihu.ehr.util.compress.Zipper;
import com.yihu.ehr.util.rest.Envelop;
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

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.04.08 9:45
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "健康档案模板服务", description = "维护/获取健康档案模板", tags = {"档案影像服务 - CDA模板"})
public class TemplateEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private ArchiveTemplateService templateService;

    @ApiOperation(value = "创建模板")
    @RequestMapping(value = ServiceApi.ProfileTemplate.Templates,  method = RequestMethod.POST)
    public Envelop saveTemplate(
            @ApiParam(name = "model", value = "健康档案模板")
            @RequestParam(value = "model") String model) throws Exception {
        ArchiveTemplate template = toEntity(model, ArchiveTemplate.class);
        String filters = "cdaVersion=" + template.getCdaVersion() + ";cdaDocumentId=" + template.getCdaDocumentId();
        if (templateService.search(filters).size() > 0) {
            return failed("该类型模版已存在！");
        }
        templateService.save(template);
        return success(true);
    }

    @ApiOperation(value = "删除模板")
    @RequestMapping(value = ServiceApi.ProfileTemplate.Template, method = RequestMethod.DELETE)
    public void deleteTemplate (
            @ApiParam(name = "id", value = "模板ID")
            @PathVariable(value = "id") int id) throws Exception {
        templateService.delete(id);
    }

    @ApiOperation(value = "更新模板属性")
    @RequestMapping(value = ServiceApi.ProfileTemplate.Template, method = RequestMethod.PUT)
    public Envelop getTemplate(
            @ApiParam(name = "id", value = "模板ID")
            @PathVariable(value = "id") int id,
            @ApiParam(name = "model", value = "模板JSON")
            @RequestParam(value = "model") String model,
            @ApiParam(name = "mode", value = "模式：copy（拷贝），update（更新）")
            @RequestParam(value = "mode") String mode) throws Exception {
        ArchiveTemplate tpl = templateService.getTemplate(id);
        if (null == tpl) {
            return failed("更新模板不存在！");
        }
        ArchiveTemplate _tpl = toEntity(model, ArchiveTemplate.class);
        if (!tpl.getCdaVersion().equals(_tpl.getCdaVersion()) || !tpl.getCdaDocumentId().equals(_tpl.getCdaDocumentId())) {
            String filters = "cdaVersion=" + _tpl.getCdaVersion() + ";cdaDocumentId=" + _tpl.getCdaDocumentId();
            if (templateService.search(filters).size() > 0) {
                return failed("该类型模版已存在！");
            }
        }
        if ("copy".equals(mode)) {
            _tpl.setId(0);
            templateService.save(_tpl);
            return success(true);
        }
        tpl.setTitle(_tpl.getTitle());
        tpl.setCdaVersion(_tpl.getCdaVersion());
        tpl.setCdaDocumentId(_tpl.getCdaDocumentId());
        tpl.setCdaDocumentName(_tpl.getCdaDocumentName());
        tpl.setType(_tpl.getType());
        templateService.save(tpl);
        return success(true);
    }

    @ApiOperation(value = "更新模板展示文件")
    @RequestMapping(value = ServiceApi.ProfileTemplate.TemplateCtn, method = RequestMethod.POST)
    public boolean setTemplateContent(
            @ApiParam(name = "id", value = "模板ID")
            @PathVariable(value = "id") int id,
            @ApiParam(name = "pc", value = "true表示PC端，false表示移动端")
            @RequestParam(value = "pc", defaultValue = "true") boolean pc,
            @ApiParam(value = "展示文件")
            @RequestPart() MultipartFile file) throws Exception {
        ArchiveTemplate template = templateService.getTemplate(id);
        if (template == null) {
            return false;
        }
        InputStream stream = file.getInputStream();
        template.setContent(pc, stream);
        templateService.save(template);
        return true;
    }

    @ApiOperation(value = "获取模板列表")
    @RequestMapping(value = ServiceApi.ProfileTemplate.Templates, method = RequestMethod.GET)
    public Envelop getTemplates(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小")
            @RequestParam(value = "size", defaultValue = "15", required = false) int size,
            @ApiParam(name = "page", value = "页码")
            @RequestParam(value = "page", defaultValue = "1", required = false) int page) throws ParseException {
        List<ArchiveTemplate> templateList = templateService.search(fields, filters, sorts, page, size);
        int count = (int)templateService.getCount(filters);
        Envelop envelop = getPageResult(templateList, count, page, size);
        return envelop;
    }

    @ApiOperation(value = "获取模板")
    @RequestMapping(value = ServiceApi.ProfileTemplate.Template, method = RequestMethod.GET)
    public ArchiveTemplate getTemplate(
            @ApiParam(name = "id", value = "模板ID")
            @PathVariable(value = "id") int id) {
        ArchiveTemplate template = templateService.getTemplate(id);
        return template;
    }

    @ApiOperation(value = "判断模版名称是否已存在")
    @RequestMapping(value = ServiceApi.ProfileTemplate.TemplateTitleExistence, method = RequestMethod.GET)
    public boolean isNameExist(
            @ApiParam(name = "title", value = "标题")
            @RequestParam(value = "title") String title,
            @ApiParam(name = "version", value = "版本")
            @RequestParam(value = "version") String version) {
        return templateService.isExistName(title, version);
    }

    @ApiOperation(value = "下载模板展示文件")
    @RequestMapping(value = ServiceApi.ProfileTemplate.TemplateCtn, method = RequestMethod.GET)
    public void getTemplateContent(
            @ApiParam(name = "id", value = "模板ID")
            @PathVariable(value = "id") int id,
            @ApiParam(value = "true表示PC端，false表示移动端")
            @RequestParam(value = "pc", defaultValue = "true") boolean pc,
            HttpServletResponse response) throws Exception {
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        ArchiveTemplate template = templateService.getTemplate(id);
        if (template == null) {
            throw new ApiException(HttpStatus.NO_CONTENT, ErrorCode.NO_CONTENT, "Template not found");
        }
        if (pc && StringUtils.isEmpty(template.getPcUrl())) {
            throw new ApiException(HttpStatus.NO_CONTENT, ErrorCode.NO_CONTENT, "Template content is empty.");
        }
        if (!pc && StringUtils.isEmpty(template.getMobileUrl())) {
            throw new ApiException(HttpStatus.NO_CONTENT, ErrorCode.NO_CONTENT, "Template content is empty.");
        }
        IOUtils.copy(new ByteArrayInputStream(template.getContent(pc)), response.getOutputStream());
        String extension = ".file";
        if (pc) {
            if (template.getPcUrl().split("\\.").length == 2) {
                extension = template.getPcUrl().split("\\.")[1];
            }
        } else {
            if (template.getMobileUrl().split("\\.").length == 2) {
                extension = template.getMobileUrl().split("\\.")[1];
            }
        }
        response.setHeader("Content-Disposition", "attachment; filename=" + template.getTitle() + "." + extension);
        response.flushBuffer();
    }

    @ApiOperation(value = "打包下载模板")
    @RequestMapping(value = ServiceApi.ProfileTemplate.TemplatesDownloads, method = RequestMethod.GET)
    public void downloadTemplates(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "organizationCode=41872607-9")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档")
            @RequestParam(value = "sorts", defaultValue = "+id", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小")
            @RequestParam(value = "size", defaultValue = "15", required = false) int size,
            @ApiParam(name = "page", value = "页码")
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            HttpServletResponse response) throws Exception {
        File tempFile = Files.createTempDir();
        String zipName = tempFile.getAbsolutePath() + ".zip";
        File zipFile = null;
        try {
            // download and zip templates
            List<ArchiveTemplate> templateList = templateService.search("", filters, sorts, reducePage(page), size);
            for (ArchiveTemplate template : templateList) {
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
        } finally {
            FileUtils.deleteQuietly(tempFile);
            FileUtils.deleteQuietly(zipFile);
        }
    }

    private void writeTplContent(File location, ArchiveTemplate template, String suffix, byte content[]) throws IOException {
        if (content != null) {
            String fileName = location.getAbsolutePath() + File.separator + template.getId() + suffix;
            FileUtils.writeByteArrayToFile(new File(fileName), content);
        }
    }
}
