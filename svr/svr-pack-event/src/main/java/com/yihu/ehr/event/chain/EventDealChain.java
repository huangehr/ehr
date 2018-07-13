package com.yihu.ehr.event.chain;

import com.yihu.ehr.event.model.Event;
import com.yihu.ehr.event.classloader.EventClassLoader;
import com.yihu.ehr.event.processor.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 处理链
 * 1. 添加处理器
 * 2. 将事件依次交由处理器进行相关处理
 * Created by progr1mmer on 2018/7/4.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class EventDealChain {
    private final Map<String, Processor> processors = new HashMap<>();

    @Autowired
    private EventClassLoader classLoader;

    public void addProcessor(Processor processor) {
        this.addProcessor(processor, true);
    }

    public void addProcessor(Processor processor, boolean active) {
        processor.setActive(active);
        this.processors.put(processor.getClass().getSimpleName(), processor);
    }

    public void addProcessor(String processor, String type) throws Exception {
        this.addProcessor(processor, type, true);
    }

    public void addProcessor(String processor, String type, boolean active) throws Exception {
        Class clazz = classLoader.loadClass(processor);
        Constructor constructor =  clazz.getConstructor(String.class);
        Processor _processor = (Processor) constructor.newInstance(type);
        _processor.setActive(active);
        this.processors.put(_processor.getClass().getName(), _processor);
    }

    public void addProcessor(Class clazz, String type) throws Exception {
        this.addProcessor(clazz, type, true);
    }

    public void addProcessor(Class clazz, String type, boolean active) throws Exception {
        Constructor constructor = clazz.getConstructor(String.class);
        Processor processor = (Processor) constructor.newInstance(type);
        processor.setActive(active);
        this.processors.put(processor.getClass().getName(), processor);
    }

    public void removeProcessor(String processor) {
        this.processors.remove(processor);
    }

    public void switchProcessorStatus(String processorName, boolean status) {
        this.processors.get(processorName).setActive(status);
    }

    public Map<String, Processor> getProcessors() {
        return this.processors;
    }

    /**
     * 事件处理入口
     * @param event
     */
    public void deal(Event event) {
        Iterator<String> iterator = processors.keySet().iterator();
        while (iterator.hasNext()) {
            Processor processor = processors.get(iterator.next());
            if (event.getType().equals(processor.getType())) {
                processor.process(event);
            }
        }
    }

}
