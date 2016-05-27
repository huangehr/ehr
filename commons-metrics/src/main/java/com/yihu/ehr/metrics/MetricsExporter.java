package com.yihu.ehr.metrics;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.actuate.metrics.dropwizard.DropwizardMetricServices;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.SortedMap;

/**
 * @author Sand
 * @created 2016.05.18 10:04
 */
@Service
public class MetricsExporter {
    @Autowired
    MetricRegistry registry;

    @Autowired
    private MetricWriter metricWriter;

    @Scheduled(initialDelay = 10000, fixedDelay = 10000)
    void exportMetrics() {
        SortedMap<String, Counter> counters = registry.getCounters();
        for (String name : counters.keySet()){
            if(couldIgnore(name)) continue;

            Counter counter = counters.get(name);

            Metric metric = new Metric(name.replace("api.v1.0", "api"), counter.getCount());
            metricWriter.set(metric);
        }

        SortedMap<String, Gauge> gauges = registry.getGauges();
        for (String name : gauges.keySet()){
            if(couldIgnore(name)) continue;

            Gauge gauge = gauges.get(name);

            Metric metric = new Metric(name.replace("api.v1.0", "api"), (Double)(gauge.getValue()));
            metricWriter.set(metric);
        }

        SortedMap<String, Histogram> histograms = registry.getHistograms();
        for (String name : histograms.keySet()){
            if (couldIgnore(name)) continue;

            Histogram histogram = histograms.get(name);
            Metric metric = new Metric(name, histogram.getCount());
        }
    }

    private boolean couldIgnore(String name) {
        return name.contains("swagger") || name.contains("star-star") || name.contains("api-docs");
    }
}
