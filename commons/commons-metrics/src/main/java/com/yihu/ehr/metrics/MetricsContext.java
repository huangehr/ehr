package com.yihu.ehr.metrics;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.ExportMetricWriter;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * A default MetricRegistry Spring bean will be created when you declare a dependency to the io.dropwizard.metrics:metrics-core library;
 * you can also register you own @Bean instance if you need customizations. Users of the Dropwizard' Metrics' library
 * will find that Spring Boot metrics are automatically published to com.codahale.metrics.MetricRegistry.
 * Metrics from the MetricRegistry are also automatically exposed via the /metrics endpoint When Dropwizard metrics are in use,
 * the default CounterService and GaugeService are replaced with a DropwizardMetricServices,
 * which is a wrapper around the MetricRegistry (so you can @Autowired one of those services and use it as normal).
 * You can also create “special” Dropwizard metrics by prefixing your metric names with the appropriate type
 * (i.e. timer.*, histogram.* for gauges, and meter.* for counters).
 *
 * @author Sand
 * @created 2016.05.26 16:31
 */
@Configuration
public class MetricsContext {
    @Value("${spring.metrics.export.statsd.host}")
    private String statsdHost;

    @Value("${spring.metrics.export.statsd.port}")
    private int statsdPort;

    @Value("${spring.application.name:application}")
    private String prefix = "";

    @Bean
    @ExportMetricWriter
    MetricWriter metricWriter() {
        return new StatsdMetricWriterExt(prefix, statsdHost, statsdPort);
    }
}
