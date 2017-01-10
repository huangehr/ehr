package com.yihu.ehr.metrics;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClientErrorHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.actuate.metrics.writer.Delta;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.util.StringUtils;

import java.io.Closeable;

/**
 * @author Sand
 * @created 2016.05.31 10:41
 */
public class StatsdMetricWriterExt implements MetricWriter, Closeable {

    private static final Log logger = LogFactory.getLog(StatsdMetricWriterExt.class);

    protected final NonBlockingStatsDClient client;

    /**
     * Create a new writer instance with the given parameters.
     * @param host the hostname for the statsd server
     * @param port the port for the statsd server
     */
    public StatsdMetricWriterExt(String host, int port) {
        this(null, host, port);
    }

    /**
     * Create a new writer with the given parameters.
     * @param prefix the prefix to apply to all metric names (can be null)
     * @param host the hostname for the statsd server
     * @param port the port for the statsd server
     */
    public StatsdMetricWriterExt(String prefix, String host, int port) {
        prefix = StringUtils.hasText(prefix) ? prefix : null;
        while (prefix != null && prefix.endsWith(".")) {
            prefix = prefix.substring(0, prefix.length() - 1);
        }

        this.client = new NonBlockingStatsDClient(prefix, host, port,
                new StatsdMetricWriterExt.LoggingStatsdErrorHandler());
    }

    @Override
    public void increment(Delta<?> delta) {
        this.client.count(delta.getName(), delta.getValue().longValue());
    }

    @Override
    public void set(Metric<?> value) {
        String name = value.getName();
        if (name.contains("timer.") && !name.contains("gauge.") && !name.contains("counter.")) {
            this.client.recordExecutionTime(name, value.getValue().longValue());
        }
        else {
            if (name.contains("counter.")) {
                this.client.count(name, value.getValue().longValue());
            }
            else {
                this.client.gauge(name, value.getValue().doubleValue());
            }
        }
    }

    @Override
    public void reset(String name) {
        // Not implemented
    }

    @Override
    public void close() {
        this.client.stop();
    }

    private static final class LoggingStatsdErrorHandler
            implements StatsDClientErrorHandler {

        @Override
        public void handle(Exception e) {
            logger.debug("Failed to write metric. Exception: " + e.getClass()
                    + ", message: " + e.getMessage());
        }

    }
}
