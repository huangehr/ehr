package com.yihu.ehr.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

/**
 * @author Sand
 * @created 2016.05.26 15:49
 */
public class HealthChecker implements HealthIndicator {
    @Override
    public Health health() {
        return null;
    }
}
