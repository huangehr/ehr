package com.yihu.ehr.service.oauth2;

import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

/**
 * Token存储服务。Token的存储由svr-security服务实现，其实现分为JDBC存储与REDIS存储。Redis存储可加快Token访问速度。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.03.05 15:38
 */
public class EhrTokenStoreService extends InMemoryTokenStore {
}
