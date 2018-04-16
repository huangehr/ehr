package com.yihu.ehr.basic.appointment.controller;

import com.yihu.ehr.basic.appointment.entity.Registration;
import com.yihu.ehr.basic.appointment.service.RegistrationService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.util.id.UuidUtil;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 挂号单 接口
 *
 * @author 张进军
 * @date 2018/4/16 19:18
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(description = "挂号单接口", tags = {"预约挂号--挂号单接口"})
public class RegistrationEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private RegistrationService registrationService;

    @ApiOperation("根据ID获取挂号单")
    @RequestMapping(value = ServiceApi.Registration.GetById, method = RequestMethod.GET)
    public Envelop getById(
            @ApiParam(name = "id", value = "主键", required = true)
            @PathVariable(value = "id") Integer id) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            Registration registration = registrationService.getById(id);
            envelop.setObj(registration);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功获取挂号单。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("获取挂号单发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation(value = "根据条件获取挂号单")
    @RequestMapping(value = ServiceApi.Registration.Search, method = RequestMethod.GET)
    public Envelop search(
            @ApiParam(name = "fields", value = "返回的字段，为空则返回全部字段")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "筛选条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            List<Registration> list = registrationService.search(fields, filters, sorts, page, size);
            int count = (int) registrationService.getCount(filters);
            envelop = getPageResult(list, count, page, size);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功获取挂号单列表。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("获取挂号单发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("新增挂号单")
    @RequestMapping(value = ServiceApi.Registration.Save, method = RequestMethod.POST)
    public Envelop add(
            @ApiParam(value = "挂号单JSON", required = true)
            @RequestBody String entityJson) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            Registration newEntity = objectMapper.readValue(entityJson, Registration.class);
            newEntity.setId(UuidUtil.randomUUID());
            newEntity = registrationService.save(newEntity);

            envelop.setObj(newEntity);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功新增挂号单。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("新增挂号单发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("更新挂号单")
    @RequestMapping(value = ServiceApi.Registration.Save, method = RequestMethod.PUT)
    public Envelop update(
            @ApiParam(value = "挂号单JSON", required = true)
            @RequestBody String entityJson) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            Registration updateEntity = objectMapper.readValue(entityJson, Registration.class);
            updateEntity = registrationService.save(updateEntity);

            envelop.setObj(updateEntity);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功更新挂号单。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("更新挂号单发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("删除挂号单")
    @RequestMapping(value = ServiceApi.Registration.Delete, method = RequestMethod.DELETE)
    public Envelop delete(
            @ApiParam(name = "id", value = "挂号单ID", required = true)
            @RequestParam(value = "id") Integer id) {
        Envelop envelop = new Envelop();
        registrationService.delete(id);
        envelop.setSuccessFlg(true);
        return envelop;
    }

}
