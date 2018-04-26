package com.yihu.ehr.basic.appointment.controller;

import com.yihu.ehr.basic.appointment.entity.Registration;
import com.yihu.ehr.basic.appointment.service.RegistrationService;
import com.yihu.ehr.basic.fzopen.service.OpenService;
import com.yihu.ehr.basic.portal.model.ProtalMessageRemind;
import com.yihu.ehr.basic.portal.service.PortalMessageRemindService;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private OpenService fzOpenService;
    @Autowired
    private PortalMessageRemindService messageRemindService;

    @ApiOperation("根据ID获取挂号单")
    @RequestMapping(value = ServiceApi.Registration.GetById, method = RequestMethod.GET)
    public Envelop getById(
            @ApiParam(name = "id", value = "主键", required = true)
            @PathVariable(value = "id") String id) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            Registration registration = registrationService.getById(id);
            envelop.setObj(registration);
            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("根据orderId获取挂号单")
    @RequestMapping(value = ServiceApi.Registration.GetByOrderId, method = RequestMethod.GET)
    public Envelop getByOrderId(
            @ApiParam(name = "orderId", value = "福州总部挂号单Id", required = true)
            @PathVariable(value = "orderId") String orderId) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            Registration registration = registrationService.getByOrderId(orderId);
            envelop.setObj(registration);
            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg(e.getMessage());
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

            // 根据就诊日期判断，更新过期的还没结束的挂号单为已就诊状态。
            // 这是折中的办法，为了解决，成功预约、取消预约之外，没有触发点更新挂号单状态的问题。
            registrationService.updateStateByRegisterDate(list);

            envelop = getPageResult(list, count, page, size);
            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("新增挂号单")
    @RequestMapping(value = ServiceApi.Registration.Save, method = RequestMethod.POST)
    public Envelop add(
            @ApiParam(value = "挂号单JSON", required = true)
            @RequestParam String entityJson) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            Registration newEntity = objectMapper.readValue(entityJson, Registration.class);
            newEntity.setId(UuidUtil.randomUUID());
            if (1 == newEntity.getRegisterType()) {
                newEntity.setRegisterTypeDesc("预约挂号");
            }
            newEntity = registrationService.save(newEntity);

            envelop.setObj(newEntity);
            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("更新福州总部挂号单到医疗云对应挂号单")
    @RequestMapping(value = ServiceApi.Registration.Update, method = RequestMethod.POST)
    public Envelop update(
            @ApiParam(value = "医疗云挂号单ID", required = true)
            @RequestParam String id,
            @ApiParam(value = "医疗云患者ID", required = true)
            @RequestParam String userId) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            // 获取福州总部的挂号单详情
            String fzOrderInfoUrl = "TradeMgmt/Open/getRegOrderInfo";
            Map<String, Object> params = new HashMap<>();
            params.put("thirdPartyUserId", userId);
            params.put("thirdPartyOrderId", id);
            String fzOrderInfoStr = fzOpenService.callFzOpenApi(fzOrderInfoUrl, params);
            Map<String, Object> fzOrderInfoMap = objectMapper.readValue(fzOrderInfoStr, Map.class);

            if ("10000".equals(fzOrderInfoMap.get("Code").toString())) {
                Registration oldEntity = registrationService.getById(id);
                oldEntity.setOrderId(fzOrderInfoMap.get("orderId").toString());
                oldEntity.setOrderCreateTime(fzOrderInfoMap.get("orderCreateTime").toString());
                oldEntity.setState((Integer) fzOrderInfoMap.get("state"));
                oldEntity.setStateDesc(fzOrderInfoMap.get("stateDesc").toString());
                Object visitClinicResultObj = fzOrderInfoMap.get("visitClinicResult");
                if (visitClinicResultObj != null) {
                    Integer visitClinicResult = Integer.valueOf(visitClinicResultObj.toString());
                    oldEntity.setVisitClinicResult(visitClinicResult);
                    if (0 == visitClinicResult) {
                        oldEntity.setVisitClinicResultDesc("确认中");
                    } else if (1 == visitClinicResult) {
                        oldEntity.setVisitClinicResultDesc("已到诊");
                    } else if (-1 == visitClinicResult) {
                        oldEntity.setVisitClinicResultDesc("爽约");
                    }
                }
                Integer timeId = (Integer) fzOrderInfoMap.get("timeId");
                if (1 == timeId) {
                    oldEntity.setTimeIdDesc("上午");
                } else if (2 == timeId) {
                    oldEntity.setTimeIdDesc("下午");
                } else if (3 == timeId) {
                    oldEntity.setTimeIdDesc("晚上");
                }
                registrationService.save(oldEntity);

                envelop.setSuccessFlg(true);
            } else {
                envelop.setErrorMsg("更新时获取福州总部挂号单详情" + fzOrderInfoMap.get("Message"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg(e.getMessage());
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

    @ApiOperation("更新挂号单状态")
    @RequestMapping(value = ServiceApi.Registration.UpdateState, method = RequestMethod.POST)
    public Envelop updateState(
            @ApiParam(value = "挂号单ID", required = true)
            @RequestParam(value = "id") String id,
            @ApiParam(value = "订单状态", required = true)
            @RequestParam(value = "state") Integer state) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            Registration updateEntity = registrationService.getById(id);
            updateEntity.setState(state);
            if (state == 1) {
                updateEntity.setStateDesc("待付款");
            } else if (state == 2) {
                updateEntity.setStateDesc("待就诊");
            } else if (state == 11) {
                updateEntity.setStateDesc("预约中");
            } else if (state == 22) {
                updateEntity.setStateDesc("退款中");
            } else if (state == 99) {
                updateEntity.setStateDesc("已退号");
            } else if (state == -1) {
                updateEntity.setStateDesc("系统取消");
            } else if (state == 3) {
                updateEntity.setStateDesc("已就诊");
            }
            updateEntity = registrationService.save(updateEntity);

            envelop.setObj(updateEntity);
            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("判断挂号是否成功")
    @RequestMapping(value = ServiceApi.Registration.IsSuccessfullyRegister, method = RequestMethod.GET)
    public Envelop isSuccessfullyRegister(
            @ApiParam(value = "挂号单ID", required = true)
            @RequestParam(value = "id") String id) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            Registration registration = registrationService.getById(id);
            List<ProtalMessageRemind> messageRemindList = messageRemindService.getByOrderId(id);
            if (messageRemindList.size() > 0 ) {
                Map<String, Object> message = objectMapper.readValue(messageRemindList.get(0).getReceivedMessages(), Map.class);
                Map<String, Object> dataNode = (Map<String, Object>) message.get("data");
                if (registration.getState() == -1) {
                    // 系统取消状态，表示挂号失败。
                    envelop.setErrorMsg(dataNode.get("failMsg").toString());
                } else {
                    envelop.setErrorMsg(dataNode.get("smsContent").toString());
                    envelop.setSuccessFlg(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

}
