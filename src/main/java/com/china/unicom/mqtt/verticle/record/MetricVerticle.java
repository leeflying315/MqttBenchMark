package com.china.unicom.mqtt.verticle.record;

import com.china.unicom.mqtt.bean.MetricRateBean;
import com.china.unicom.mqtt.constant.MqttTopicConstant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.mqtt.MqttClient;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Log4j2
public class MetricVerticle extends AbstractVerticle {

    private AtomicInteger totalConnectionCount = new AtomicInteger(0);
    private AtomicInteger successConnectionCount = new AtomicInteger(0);
    private AtomicInteger errorConnectionCount = new AtomicInteger(0);
    private AtomicInteger instanceFinishCount = new AtomicInteger(0);

    private final Long startTime = System.currentTimeMillis();
    private Long endTime = System.currentTimeMillis();

    private final AtomicInteger totalPublishCount = new AtomicInteger(0);
    private final AtomicInteger successPublishCount = new AtomicInteger(0);
    private final AtomicInteger errorPublishCount = new AtomicInteger(0);
    private final AtomicInteger topicFinishCount = new AtomicInteger(0);

    private AtomicLong executeTimeCost = new AtomicLong(0);

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
            if (totalConnectionCount.get() == 0) {
                log.info("total connection is : {}", totalConnectionCount.get());
            } else {
                log.info("total connection: {}, success connection: {}, error connection {}," + " avg time cost: {} ms",
                    totalConnectionCount.get(), successConnectionCount.get(), errorConnectionCount.get(),
                    executeTimeCost.get() / totalConnectionCount.get());
            }
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
        executeTimeCost.addAndGet(metricRateBean.getTimeCost());
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
    public void stopAllHandler(Message<String> message){
        log.info("stop all clients size ");
    }

}
