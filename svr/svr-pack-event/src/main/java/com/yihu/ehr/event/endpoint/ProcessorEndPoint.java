package com.yihu.ehr.event.endpoint;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.event.EventProcessor;
import com.yihu.ehr.event.chain.EventDealChain;
import com.yihu.ehr.event.processor.Processor;
import com.yihu.ehr.event.service.EventProcessorService;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Created by progr1mmer on 2018/7/4.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "ProcessorEndPoint", description = "事件处理链", tags = {"事件服务-事件处理链"})
public class ProcessorEndPoint extends EnvelopRestEndPoint{

    @Autowired
    private EventDealChain dealChain;
    @Autowired
    private EventProcessorService eventProcessorService;
    @Autowired
    private FastDFSUtil fastDFSUtil;

    @ApiOperation(value = "添加事件处理器")
    @RequestMapping(value = "/event/processor", method = RequestMethod.POST)
    public boolean addProcessor (
            @ApiParam(name = "processor", value = "事件处理器名称", required = true)
            @RequestParam(value = "processor") String processor,
            @ApiParam(name = "processType", value = "事件处理器类型", required = true)
            @RequestParam(value = "processType") String processType,
            @ApiParam(name = "file", value = "文件")
            @RequestPart(value = "file", required = false) MultipartFile file) throws Exception {
        if (eventProcessorService.findByName(processor) != null) {
            throw new ApiException("processor " + processor + " already exists");
        }
        EventProcessor eventProcessor = new EventProcessor();
        eventProcessor.setName(processor);
        eventProcessor.setProcessType(processType);
        eventProcessor.setActive(true);
        eventProcessor.setCreator("Progr1mmer");
        if (file != null) {
            String fileName = file.getOriginalFilename();
            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            ObjectNode objectNode = fastDFSUtil.upload(file.getInputStream(), fileExtension, "svr-event");
            String groupName = objectNode.get(FastDFSUtil.GROUP_NAME).toString();
            String remoteFileName = objectNode.get(FastDFSUtil.REMOTE_FILE_NAME).toString();
            String path = groupName.substring(1, groupName.length() - 1) + ":" + remoteFileName.substring(1, remoteFileName.length() - 1);
            eventProcessor.setRemote_path(path);
        }
        eventProcessor = eventProcessorService.save(eventProcessor);
        try {
            dealChain.addProcessor(processor, processType);
        } catch (Exception e) {
            eventProcessorService.delete(eventProcessor.getId());
            throw e;
        }
        return true;
    }

    @ApiOperation(value = "移除事件处理器")
    @RequestMapping(value = "/event/processor", method = RequestMethod.DELETE)
    public boolean removeProcessor (
            @ApiParam(name = "processor", value = "事件处理器名称", required = true)
            @RequestParam(value = "processor") String processor) throws Exception {
        dealChain.removeProcessor(processor);
        EventProcessor eventProcessor = eventProcessorService.findByName(processor);
        if (eventProcessor != null) {
            String remote_path = eventProcessor.getRemote_path();
            if (!StringUtils.isEmpty(remote_path)) {
                fastDFSUtil.delete(remote_path.split(":")[0], remote_path.split(":")[1]);
            }
            eventProcessorService.delete(eventProcessor.getId());
        }
        return true;
    }

    @ApiOperation(value = "获取事件处理链")
    @RequestMapping(value = "/event/processor", method = RequestMethod.GET)
    public Map<String, Processor> getProcessors (){
        return dealChain.getProcessors();
    }

    @ApiOperation(value = "更新事件处理器状态， 返回更新后状态")
    @RequestMapping(value = "/event/processor", method = RequestMethod.PUT)
    public boolean switchProcessorStatus (
            @ApiParam(name = "processor", value = "事件处理器名称", required = true)
            @RequestParam(value = "processor") String processor,
            @ApiParam(name = "active", value = "活跃状态", required = true)
            @RequestParam(value = "active") boolean active) throws Exception {
        EventProcessor eventProcessor = eventProcessorService.findByName(processor);
        if (null == eventProcessor) {
            throw new ApiException("processor " + processor + " no exists");
        }
        eventProcessor.setActive(active);
        dealChain.switchProcessorStatus(processor, active);
        eventProcessorService.save(eventProcessor);
        return active;
    }

}
