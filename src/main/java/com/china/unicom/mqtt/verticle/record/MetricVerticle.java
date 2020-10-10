package com.china.unicom.mqtt.verticle.record;

import com.china.unicom.mqtt.bean.MetricRateBean;
import com.china.unicom.mqtt.constant.MqttTopicConstant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
public class MetricVerticle extends AbstractVerticle {

    private AtomicInteger totalConnectionCount = new AtomicInteger(0);
    private AtomicInteger successConnectionCount = new AtomicInteger(0);
    private AtomicInteger errorConnectionCount = new AtomicInteger(0);
    private AtomicInteger instanceFinishCount = new AtomicInteger(0);

    private Long startTime = System.currentTimeMillis();
    private Long endTime;

    private AtomicInteger totalPublishCount = new AtomicInteger(0);
    private AtomicInteger successPublishCount = new AtomicInteger(0);
    private AtomicInteger errorPublishCount = new AtomicInteger(0);
    private AtomicInteger topicFinishCount = new AtomicInteger(0);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void start() {
        log.info("============================");
        log.info("start to record this job");
        log.info("============================");

        EventBus eventBus = vertx.eventBus();
        eventBus.consumer(MqttTopicConstant.CONNECTION_TOPIC, this::connectionHandler);
        boolean subTopic = config().getBoolean("recordPub");
        Integer instanceCount = config().getInteger("instance");

        if (subTopic) {
            eventBus.consumer(MqttTopicConstant.PUBLISH_TOPIC, this::publishHandler);
            vertx.setPeriodic(10000, time -> {
                if (totalPublishCount.get() != 0) {
                    log.info("total publish: {}, success publish: {}, error publish {}, time cost: {} ms",
                        totalPublishCount.get(), successPublishCount.get(), errorPublishCount.get(),
                        endTime - startTime);
                    if (topicFinishCount.get() == instanceCount)
                        vertx.cancelTimer(time);
                }
            });
        }
        vertx.setPeriodic(10000, time -> {
            log.info("total connection: {}, success connection: {}, error connection {}, time cost: {} ms",
                totalConnectionCount.get(), successConnectionCount.get(), errorConnectionCount.get(),
                endTime - startTime);
            if (instanceCount == instanceFinishCount.get())
                vertx.cancelTimer(time);
        });

    }

    // 打印每个verticle 连接建立的结果
    public void connectionHandler(Message<String> message) {
        String context = message.body();
        MetricRateBean metricRateBean = null;
        try {
            metricRateBean = objectMapper.readValue(context, MetricRateBean.class);
        } catch (JsonProcessingException e) {
            log.error("", e);
        }
        totalConnectionCount.addAndGet(metricRateBean.getTotalCount());
        successConnectionCount.addAndGet(metricRateBean.getSuccessCount());
        errorConnectionCount.addAndGet(metricRateBean.getErrorCount());
        boolean countFinished = metricRateBean.getCountFinished();
        if (countFinished) {
            instanceFinishCount.incrementAndGet();
        }
        endTime = metricRateBean.getEndTime();

    }

    // 打印每个verticle 发布消息计数
    public void publishHandler(Message<String> message) {
        String context = message.body();
        MetricRateBean metricRateBean = null;
        try {
            metricRateBean = objectMapper.readValue(context, MetricRateBean.class);
        } catch (JsonProcessingException e) {
            log.error("", e);
        }
        totalPublishCount.addAndGet(metricRateBean.getTotalCount());
        successPublishCount.addAndGet(metricRateBean.getSuccessCount());
        errorPublishCount.addAndGet(metricRateBean.getErrorCount());
        boolean countFinished = metricRateBean.getCountFinished();
        if (countFinished) {
            topicFinishCount.incrementAndGet();
        }
        endTime = metricRateBean.getEndTime();

    }
}
