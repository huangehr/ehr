package com.yihu.ehr.metrics;

import com.codahale.metrics.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.endpoint.CachePublicMetrics;
import org.springframework.boot.actuate.endpoint.DataSourcePublicMetrics;
import org.springframework.boot.actuate.endpoint.SystemPublicMetrics;
import org.springframework.boot.actuate.endpoint.TomcatPublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.SortedMap;

/**
 * @author Sand
 * @created 2016.05.18 10:04
 */
@Service
public class MetricsExporter {
    @Value("${spring.application.name:application}")
    private String applicationName = "";

    @Autowired
    MetricRegistry registry;

    @Autowired
    private MetricWriter metricWriter;

    @Autowired
    private SystemPublicMetrics systemMetrics;

    @Autowired
    private TomcatPublicMetrics tomcatSessionMetrics;

    //@Scheduled(initialDelay = 10000, fixedDelay = 10000)
    void exportCounter() {
        SortedMap<String, Counter> counters = registry.getCounters();
        for (String name : counters.keySet()){
            if(couldIgnore(name)) continue;

            Counter counter = counters.get(name);

            Metric metric = new Metric(name.replace("api.v1.0", "api"), counter.getCount());
            metricWriter.set(metric);
        }

        SortedMap<String, Histogram> histograms = registry.getHistograms();
        for (String name : histograms.keySet()){
            if (couldIgnore(name)) continue;

            Histogram histogram = histograms.get(name);
            Metric metric = new Metric(name, histogram.getCount());
            metricWriter.set(metric);
        }

        SortedMap<String, Meter> meters = registry.getMeters();
        for (String name : meters.keySet()){
            if (couldIgnore(name)) continue;

            Meter meter = meters.get(name);
            Metric metric = new Metric(name, meter.getCount());
            metricWriter.set(metric);
        }

        SortedMap<String, Timer> timers = registry.getTimers();
        for (String name : timers.keySet()){
            if (couldIgnore(name)) continue;

            Timer timer = timers.get(name);
            Metric metric = new Metric(name, timer.getCount());
            metricWriter.set(metric);
        }

        Collection<Metric<?>> metrics = systemMetrics.metrics();
        for (Metric<?> metric : metrics){
            metricWriter.set(metric);
        }
    }

    //@Scheduled(initialDelay = 2000, fixedDelay = 2000)
    void exportGauges(){
        SortedMap<String, Gauge> gauges = registry.getGauges();
        for (String name : gauges.keySet()){
            if(couldIgnore(name)) continue;

            Gauge gauge = gauges.get(name);

            Metric metric = new Metric(name.replace("api.v1.0", "api"), (Double)(gauge.getValue()));
            metricWriter.set(metric);
        }
    }

    private boolean couldIgnore(String name) {
        //System.out.println(name);
        return name.contains("swagger") || name.contains("star-star") || name.contains("api-docs");
    }
}
