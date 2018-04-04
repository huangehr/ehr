package com.yihu.ehr.basic.apps.service;

import com.yihu.ehr.basic.apps.dao.AppApiErrorCodeDao;
import com.yihu.ehr.basic.apps.dao.AppApiParameterRepository;
import com.yihu.ehr.basic.apps.dao.AppApiRepository;
import com.yihu.ehr.basic.apps.dao.AppApiResponseRepository;
import com.yihu.ehr.basic.apps.model.AppApi;
import com.yihu.ehr.basic.apps.model.AppApiParameter;
import com.yihu.ehr.basic.apps.model.AppApiResponse;
import com.yihu.ehr.basic.user.dao.XRoleApiRelationRepository;
import com.yihu.ehr.basic.user.dao.XRoleAppRelationRepository;
import com.yihu.ehr.basic.user.dao.XRolesRepository;
import com.yihu.ehr.basic.user.entity.RoleApiRelation;
import com.yihu.ehr.basic.user.entity.RoleAppRelation;
import com.yihu.ehr.basic.user.entity.Roles;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.entity.api.AppApiErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.app.OpenAppApi;
import com.yihu.ehr.query.BaseJpaService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author linz
 * @version 1.0
 * @created 2016年7月7日21:05:04
 */
@Service
@Transactional
public class AppApiService extends BaseJpaService<AppApi, AppApiRepository> {

    private static final String ADD = "add";
    private static final String DELETE = "delete";
    private static final String UPDATE = "update";
    private static final String DATA_STATUS = "__status";
    private static final Integer NEW_DATA = 0;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private AppApiRepository appApiRepository;
    @Autowired
    private AppApiParameterRepository appApiParameterRepository;
    @Autowired
    private AppApiResponseRepository appApiResponseRepository;
    @Autowired
    private XRoleApiRelationRepository xRoleApiRelationRepository;
    @Autowired
    private XRoleAppRelationRepository xRoleAppRelationRepository;
    @Autowired
    private AppApiErrorCodeDao appApiErrorCodeDao;
    @Autowired
    private XRolesRepository xRolesRepository;

    public Page<AppApi> getAppApiList(String sorts, int page, int size){
        AppApiRepository repo = (AppApiRepository)getJpaRepository();
        Pageable pageable = new PageRequest(page, size, parseSorts(sorts));
        return repo.findAll(pageable);
    }

    public AppApi createAppApi(AppApi appApi) {
        if ("".equals(appApi.getMethodName())){
            appApi.setMethodName(null);
        }
        appApiRepository.save(appApi);
        return appApi;
    }

    public AppApi updateAppApi(AppApi appApi){
        appApiRepository.save(appApi);
        return appApi;
    }

    public void  deleteAppApi(Integer id){
        appApiRepository.delete(id);
    }

    public AppApi completeSave(String appApiStr, String apiParamStr, String apiResponseStr, String apiErrorCodeStr) throws IOException {
        AppApi appApi = objectMapper.readValue(appApiStr, AppApi.class);
        AppApi newAppApi = appApiRepository.save(appApi);
        saveParamAndResponseAndErrorCode(newAppApi.getId(), apiParamStr, apiResponseStr, apiErrorCodeStr);
        return newAppApi;
    }

    public void completeDelete(Integer id) {
        appApiParameterRepository.deleteByAppApiId(id);
        appApiResponseRepository.deleteByAppApiId(id);
        xRoleApiRelationRepository.deleteByApiId((long)id);
        appApiErrorCodeDao.deleteByAppApiId(id);
        appApiRepository.delete(id);
    }

    public AppApi findById(Integer id) {
        return appApiRepository.findOne(id);
    }

    public List<AppApi> findByCateId(Integer categoryId) {
        return appApiRepository.findByCategory(categoryId);
    }

    public List<AppApi> authApiList(String clientId) {
        String sql =  "SELECT aa.* FROM apps_api aa \n" +
                "\tLEFT JOIN role_api_relation rar ON rar.app_api_id = aa.id \n" +
                "\tWHERE rar.role_id IN (SELECT role_id FROM role_app_relation WHERE app_id ='" + clientId + "') \n" +
                "\tAND LENGTH(aa.micro_service_url) > 0 AND LENGTH(aa.ms_method_name) > 0";
        RowMapper rowMapper = BeanPropertyRowMapper.newInstance(AppApi.class);
        return (List<AppApi>)jdbcTemplate.query(sql, rowMapper);
    }

    public void authApi(String code, String appId, String appApiId) {
        Roles roles =  xRolesRepository.findByCodeAndAppId(code, appId);
        if (null == roles) {
            //创建扩展角色
            roles = new Roles();
            roles.setCode(code);
            roles.setName("扩展开发者");
            roles.setDescription("开放平台扩展开发者");
            roles.setAppId(appId);
            roles.setType("0");
            roles = xRolesRepository.save(roles);
            RoleAppRelation roleAppRelation = new RoleAppRelation();
            roleAppRelation.setAppId(code);
            roleAppRelation.setRoleId(roles.getId());
            xRoleAppRelationRepository.save(roleAppRelation);
        }
        String [] appApiIdArr = appApiId.split(",");
        for (String id : appApiIdArr) {
            if (null == xRoleApiRelationRepository.findRelation(new Long(id), roles.getId())) {
                RoleApiRelation roleApiRelation = new RoleApiRelation();
                roleApiRelation.setRoleId(roles.getId());
                roleApiRelation.setApiId(new Long(id));
                xRoleApiRelationRepository.save(roleApiRelation);
            }
        }
    }

    public void unAuthApi(String code, String appId, String appApiId) {
        Roles roles =  xRolesRepository.findByCodeAndAppId(code, appId);
        if (null == roles) {
            return;
        }
        String [] appApiIdArr = appApiId.split(",");
        for (String id : appApiIdArr) {
            xRoleApiRelationRepository.deleteByApiIdAndRoleId(new Long(id), roles.getId());
        }
    }

    /**
     * 操作apiParam及apiResponse及apiErrorCode
     *
     * @param apiId
     * @param apiParam
     * @param apiResponse
     */
    private void saveParamAndResponseAndErrorCode(Integer apiId, String apiParam, String apiResponse, String apiErrorCode) throws IOException {
        List<Map<String, Object>> list;
        if (!StringUtils.isEmpty(apiParam)) {
            list = objectMapper.readValue(apiParam, List.class);
            for (Map<String, Object> paramMap : list) {
                //删除的是新增的数据直接跳过
                if (DELETE.equals(paramMap.get(DATA_STATUS)) && NEW_DATA.equals(paramMap.get("id") + "")) {
                    continue;
                } else {
                    paramMap.put("appApiId", apiId);
                    String json = objectMapper.writeValueAsString(paramMap);
                    AppApiParameter appApiParameter = objectMapper.readValue(json, AppApiParameter.class);
                    if (ADD.equals(paramMap.get(DATA_STATUS))) {
                        appApiParameterRepository.save(appApiParameter);
                    } else if (UPDATE.equals(paramMap.get(DATA_STATUS))) {
                        AppApiParameter oldAppApiParameter = appApiParameterRepository.findOne(appApiParameter.getId());
                        if (oldAppApiParameter == null) {
                            throw new ApiException(ErrorCode.OBJECT_NOT_FOUND, "更新的请求参数不存在：" + appApiParameter.getId());
                        }
                        appApiParameterRepository.save(appApiParameter);
                    } else if (DELETE.equals(paramMap.get(DATA_STATUS))) {
                        appApiParameterRepository.delete(appApiParameter.getId());
                    }
                }
            }
        }
        if (!StringUtils.isEmpty(apiResponse)) {
            list = objectMapper.readValue(apiResponse, List.class);
            for (Map<String, Object> paramMap : list) {
                //删除的是新增的数据直接跳过
                if (DELETE.equals(paramMap.get(DATA_STATUS)) && NEW_DATA.equals(paramMap.get("id") + "")) {
                    continue;
                } else {
                    paramMap.put("appApiId", apiId);
                    String json = objectMapper.writeValueAsString(paramMap);
                    AppApiResponse appApiResponse = objectMapper.readValue(json, AppApiResponse.class);
                    if (ADD.equals(paramMap.get(DATA_STATUS))) {
                        appApiResponseRepository.save(appApiResponse);
                    } else if (UPDATE.equals(paramMap.get(DATA_STATUS))) {
                        AppApiResponse oldAppApiResponse = appApiResponseRepository.findOne(appApiResponse.getId());
                        if (oldAppApiResponse == null) {
                            throw new ApiException(ErrorCode.OBJECT_NOT_FOUND, "更新的返回参数不存在：" + appApiResponse.getId());
                        }
                        appApiResponseRepository.save(appApiResponse);
                    } else if (DELETE.equals(paramMap.get(DATA_STATUS))) {
                        appApiResponseRepository.delete(appApiResponse);
                    }
                }
            }
        }
        if (!StringUtils.isEmpty(apiErrorCode)) {
            list = objectMapper.readValue(apiErrorCode, List.class);
            for (Map<String, Object> paramMap : list) {
                //删除的是新增的数据直接跳过
                if (DELETE.equals(paramMap.get(DATA_STATUS)) && NEW_DATA.equals(paramMap.get("id") + "")) {
                    continue;
                } else {
                    paramMap.put("appApiId", apiId);
                    String json = objectMapper.writeValueAsString(paramMap);
                    AppApiErrorCode appApiErrorCode = objectMapper.readValue(json, AppApiErrorCode.class);
                    if (ADD.equals(paramMap.get(DATA_STATUS))) {
                        appApiErrorCodeDao.save(appApiErrorCode);
                    } else if (UPDATE.equals(paramMap.get(DATA_STATUS))) {
                        AppApiErrorCode oldAppApiErrorCode = appApiErrorCodeDao.findOne(appApiErrorCode.getId());
                        if (oldAppApiErrorCode == null) {
                            throw new ApiException(ErrorCode.OBJECT_NOT_FOUND, "更新的错误码说明不存在：" + appApiErrorCode.getId());
                        }
                        appApiErrorCodeDao.save(appApiErrorCode);
                    } else if (DELETE.equals(paramMap.get(DATA_STATUS))) {
                        appApiErrorCodeDao.delete(appApiErrorCode);
                    }
                }
            }
        }
    }

    public List<AppApi> findByName(String name) {
        return appApiRepository.findByName(name);
    }

}