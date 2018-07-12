package com.yihu.ehr.profile.controller.profile;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.profile.service.ProfileMedicationService;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * EndPoint - 用药记录
 * Created by progr1mmer on 2018/3/13.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "ProfileMedicationEndPoint", description = "用药记录", tags = {"档案影像服务 - 用药记录"})
public class ProfileMedicationEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private ProfileMedicationService profileMedicationService;

    @ApiOperation("用药排行")
    @RequestMapping(value = ServiceApi.Profiles.MedicationRanking, method = RequestMethod.GET)
    public Map<String, Integer> medicationRanking(
            @ApiParam(name = "demographic_id", value = "身份证号", required = true, defaultValue = "362301195002141528")
            @RequestParam(value = "demographic_id") String demographic_id,
            @ApiParam(name = "filter", value = "过滤条件org_code=xxxxx")
            @RequestParam(value = "filter", required = false) String filter,
            @ApiParam(name = "hp_code", value = "健康问题代码，不传默认查找所有（接口改造后此参数可在filter中体现）")
            @RequestParam(value = "hp_code", required = false) String hp_code,
            @ApiParam(name = "date", value = "某个时间段内（不传默认查找所有）")
            @RequestParam(value = "date", required = false) String date) throws Exception {
        return profileMedicationService.medicationRanking(demographic_id, filter, hp_code, date);
    }

    @ApiOperation("用药记录 - 上饶PP")
    @RequestMapping(value = ServiceApi.Profiles.MedicationRecords, method = RequestMethod.GET)
    public List<Map<String, Object>> medicationRecords(
            @ApiParam(name = "demographic_id", value = "身份证号", required = true, defaultValue = "362301195002141528")
            @RequestParam(value = "demographic_id") String demographic_id,
            @ApiParam(name = "filter", value = "健康问题health_problem?hpCode（不传默认查找所有）")
            @RequestParam(value = "filter", required = false) String filter,
            @ApiParam(name = "date", value = "时间")
            @RequestParam(value = "date", required = false) String date,
            @ApiParam(name = "key_word", value = "关键字（药品）")
            @RequestParam(value = "key_word", required = false) String key_word) throws Exception {
        return profileMedicationService.medicationRecords(demographic_id, filter, date, key_word);
    }

    @ApiOperation("用药记录 - 档案浏览器")
    @RequestMapping(value = ServiceApi.Profiles.MedicationRecordsPage, method = RequestMethod.GET)
    public Envelop medicationRecordsPage (
            @ApiParam(name = "demographic_id", value = "身份证号", required = true, defaultValue = "362301195002141528")
            @RequestParam(value = "demographic_id") String demographic_id,
            @ApiParam(name = "filter", value = "健康问题health_problem?hpCode（不传默认查找所有）")
            @RequestParam(value = "filter", required = false) String filter,
            @ApiParam(name = "date", value = "时间")
            @RequestParam(value = "date", required = false) String date,
            @ApiParam(name = "key_word", value = "关键字（药品）")
            @RequestParam(value = "key_word", required = false) String key_word,
            @ApiParam(name = "page", value = "页码", required = true)
            @RequestParam(value = "page") Integer page,
            @ApiParam(name = "size", value = "分页大小", required = true)
            @RequestParam(value = "size") Integer size) throws Exception {
        List<Map<String, Object>> list = profileMedicationService.medicationRecords(demographic_id, filter, date, key_word);
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        envelop.setCurrPage(page);
        envelop.setPageSize(size);
        envelop.setTotalPage(list.size() % size > 0 ? list.size() / size + 1 : list.size() / size);
        envelop.setTotalCount(list.size());
        List result = new ArrayList();
        for (int i = (page - 1) * size; i < page * size; i ++) {
            if (i > list.size() - 1) {
                break;
            }
            result.add(list.get(i));
        }
        envelop.setDetailModelList(result);
        return envelop;
    }

    @ApiOperation("最近的处方清单 - 档案浏览器")
    @RequestMapping(value = ServiceApi.Profiles.RecentMedicationSub, method = RequestMethod.GET)
    public Envelop recentMedicationSub(
            @ApiParam(name = "demographic_id", value = "主表事件索引", required = true, defaultValue = "362301195002141528")
            @RequestParam(value = "demographic_id") String demographic_id,
            @ApiParam(name = "page", value = "页码", required = true)
            @RequestParam(value = "page") Integer page,
            @ApiParam(name = "size", value = "分页大小", required = true)
            @RequestParam(value = "size") Integer size) throws Exception {
        return profileMedicationService.recentMedicationSub(demographic_id, null, page, size);
    }

}
