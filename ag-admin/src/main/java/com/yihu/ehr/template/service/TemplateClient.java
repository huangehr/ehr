package com.yihu.ehr.template.service;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.profile.MTemplate;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;

/**
 *
 * @author lincl
 * @version 1.0
 * @created 2016.4.15
 */
@FeignClient(name=MicroServices.HealthProfile)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface TemplateClient {


    @ApiOperation(value = "创建模板")
    @RequestMapping(value = ServiceApi.ProfileTemplate.Templates, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.POST)
    void saveTemplate(
            @RequestParam(value = "model") String model);

    @ApiOperation(value = "获取模板列表", response = MTemplate.class, responseContainer = "List")
    @RequestMapping(value = ServiceApi.ProfileTemplate.Templates, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
    ResponseEntity<Collection<MTemplate>> getTemplates(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", defaultValue = "15", required = false) int size,
            @RequestParam(value = "page", defaultValue = "1", required = false) int page) ;

    @ApiOperation(value = "获取模板")
    @RequestMapping(value = ServiceApi.ProfileTemplate.Template, method = RequestMethod.GET)
    MTemplate getTemplate(
            @PathVariable(value = "id") int id);

    @ApiOperation(value = "更新模板属性")
    @RequestMapping(value = ServiceApi.ProfileTemplate.Template, method = RequestMethod.PUT)
    void updateTemplate(
            @PathVariable(value = "id") int id,
            @RequestParam("model") String model) ;

    @ApiOperation(value = "下载模板展示文件")
    @RequestMapping(value = ServiceApi.ProfileTemplate.TemplateCtn, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, method = RequestMethod.GET)
    void getTemplateContent(
            @PathVariable(value = "id") int id,
            @RequestParam(value = "pc", defaultValue = "true") boolean pc);

    @ApiOperation(value = "更新模板展示文件")
    @RequestMapping(value = ServiceApi.ProfileTemplate.TemplateCtn, method = RequestMethod.POST)
    void setTemplateContent(
            @PathVariable(value = "id") int id,
            @RequestParam(value = "pc", defaultValue = "true") boolean pc,
            @RequestPart() MultipartFile file);

    @ApiOperation(value = "删除模板")
    @RequestMapping(value = ServiceApi.ProfileTemplate.Template, method = RequestMethod.DELETE)
    void deleteTemplate(
            @PathVariable(value = "id") int id) ;

    @ApiOperation(value = "打包下载模板", response = MTemplate.class, responseContainer = "List")
    @RequestMapping(value = ServiceApi.ProfileTemplate.TemplatesDownloads, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, method = RequestMethod.GET)
    void downloadTemplates(
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", defaultValue = "+id", required = false) String sorts,
            @RequestParam(value = "size", defaultValue = "15", required = false) int size,
            @RequestParam(value = "page", defaultValue = "1", required = false) int page);

    @ApiOperation(value = "获取模板")
    @RequestMapping(value = ServiceApi.ProfileTemplate.TemplateTitleExistence, method = RequestMethod.GET)
    boolean isNameExist(
            @RequestParam(value = "version") String version,
            @RequestParam(value = "title") String title);
}
