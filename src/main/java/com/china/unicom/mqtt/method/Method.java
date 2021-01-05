package com.china.unicom.mqtt.method;

import com.china.unicom.mqtt.bean.DownServiceBean;
import com.china.unicom.mqtt.bean.MessageBean;
import com.china.unicom.mqtt.bean.MetricRateBean;
import com.china.unicom.mqtt.bean.MqttSessionBean;
import com.china.unicom.mqtt.config.Config;
import com.china.unicom.mqtt.constant.MqttTopicConstant;
import com.china.unicom.mqtt.utils.JsonObjectMapper;
import com.china.unicom.mqtt.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.mqtt.MqttClient;
import lombok.extern.log4j.Log4j2;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: lifei
 * @Description:
 * @Date: 2020/9/17
 */
@Log4j2
public class Method {
    private static final ObjectMapper objectMapper = JsonObjectMapper.getInstance();

    public static void subscribeMessage(Map<String, Long> publishTopicMap, MqttClient client, String topic) {
        log.info("sub topic for {}", topic);
        client.subscribe(topic, MqttQoS.EXACTLY_ONCE.value());
        client.subscribeCompletionHandler(event -> {
            log.info("Receive SUBACK from server with granted QoS : " + event.grantedQoSLevels());
        });
        client.publishHandler(publish -> {
            String json = publish.payload().toString(Charset.defaultCharset());

            log.info("Just received message on [" + publish.topicName() + "] payload [" + json + "] with QoS ["
                    + publish.qosLevel() + "]");
            final MessageBean objectNode;
            try {
                objectNode = objectMapper.readValue(json, MessageBean.class);
                String messageId = objectNode.getMessageId();
                long publishTime = publishTopicMap.get(messageId);
                log.info("receive response for {}, time cost {}ms", objectNode.getMessageId(),
                        System.currentTimeMillis() - publishTime);
                publishTopicMap.remove(messageId);
            } catch (Exception e) {
                log.error("", e);
            }
        });
    }

    // 发布消息
    public static void publishMessage(Map<String, Long> publicTopicMap, Vertx vertx, Config config, MqttClient client,
                                      String topic) {
        if (config.getTopic().isPublishMessage()) {
            int qos = config.getTopic().getQos();
            int interval = config.getTopic().getPublishInterval();
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger errorCount = new AtomicInteger(0);
            AtomicInteger totalCount = new AtomicInteger(0);
            long startTime = System.currentTimeMillis();
            EventBus eventBus = vertx.eventBus();
            // 如果所有topic发送完毕
            MetricRateBean metricRateBean = MetricRateBean.builder().startTime(startTime).successCount(0).totalCount(1)
                    .errorCount(0).countFinished(false).build();

            vertx.setPeriodic(interval, time -> {
                String messageId = Integer.toString(Utils.randomInteger());
                String input = Utils.getInputString(config.getTopic().getPublishMode(), messageId);

                Buffer buffer = Buffer.buffer(input);
                log.info("topic is {}, message id is {}", topic, messageId);
                totalCount.incrementAndGet();

                client.publish(topic, buffer, MqttQoS.valueOf(qos), false, false, event -> {
                    metricRateBean.setEndTime(System.currentTimeMillis());
                    if (event.succeeded()) {
                        publicTopicMap.put(messageId, System.currentTimeMillis());
                        log.info("publish success");
                        metricRateBean.setSuccessCount(1);
                        successCount.getAndIncrement();
                    } else {
                        log.info("publish failed");
                        metricRateBean.setErrorCount(1);
                        errorCount.getAndIncrement();
                    }
                    log.info("publish success,messgae Id {}", successCount.get());
                    if (successCount.get() + errorCount.get() >= config.getTopic().getMessageCount()) {
                        metricRateBean.setCountFinished(true);
                    }
                    String bean = null;
                    try {
                        bean = objectMapper.writeValueAsString(metricRateBean);
                    } catch (JsonProcessingException e) {
                        log.error("", e);
                    }

                    eventBus.publish(MqttTopicConstant.PUBLISH_TOPIC, bean);
                });
                // 独立计数，发布过慢时会导致多发布
                if (totalCount.get() >= config.getTopic().getMessageCount()) {
                    log.info("all message finished," + " total messages {}, success {}, " + "error {}", totalCount,
                            successCount, errorCount);
                    vertx.cancelTimer(time);
                }
            });
        }
    }

    public static void subSyncTopic(Map<String, Long> publishTopicMap, MqttClient client,
                                    MqttSessionBean mqttSessionBean, Integer subSyncTopic) {
        switch (subSyncTopic){
            case 0: {
                break;
            }
            case 1: {
                recordPubCost(publishTopicMap,client,mqttSessionBean);
                break;
            }
            case 2: {
                sendBackMessage(client,mqttSessionBean);
                break;
            }
            default:
                log.error("input type illegal {}", subSyncTopic);
                break;
        }
    }
    public static void recordPubCost(Map<String, Long> publishTopicMap, MqttClient client,
                                     MqttSessionBean mqttSessionBean){
        log.info("start to subscirbe topic {} and {}", mqttSessionBean.getTopic(), mqttSessionBean.getSubTopic());
        // 订阅发布
        client.subscribe(mqttSessionBean.getTopic(), MqttQoS.EXACTLY_ONCE.value());
        client.subscribe(mqttSessionBean.getTopic(), MqttQoS.AT_LEAST_ONCE.value());
        client.subscribe(mqttSessionBean.getTopic(), MqttQoS.AT_MOST_ONCE.value());

        // 订阅接受
        client.subscribe(mqttSessionBean.getSubTopic(), MqttQoS.EXACTLY_ONCE.value());
        client.subscribe(mqttSessionBean.getSubTopic(), MqttQoS.AT_LEAST_ONCE.value());
        client.subscribe(mqttSessionBean.getSubTopic(), MqttQoS.AT_MOST_ONCE.value());

        client.subscribeCompletionHandler(event -> {
            log.info("Receive SUBACK from server with granted QoS : " + event.grantedQoSLevels());
        });
        client.publishHandler(publish -> {
            String json = publish.payload().toString(Charset.defaultCharset());
            log.info("Just received message on [" + publish.topicName() + "] payload [" + json + "] with QoS ["
                    + publish.qosLevel() + "]");
            final MessageBean objectNode;
            String messageId = null;
            try {
                objectNode = objectMapper.readValue(json, MessageBean.class);
                messageId = objectNode.getMessageId();
            } catch (Exception e) {
                log.error("", e);
            }
            // 接受到发布消息
            if (publish.topicName().equals(mqttSessionBean.getTopic())) {
                publishTopicMap.put(messageId, System.currentTimeMillis());
            }
            // 接受到回执消息
            else {
                long publishTime = publishTopicMap.get(messageId);
                log.info("receive response for {}, time cost {}ms", messageId,
                        System.currentTimeMillis() - publishTime);
                publishTopicMap.remove(messageId);
            }
        });
    }

    public static void sendBackMessage( MqttClient client,
                                     MqttSessionBean mqttSessionBean){
        log.info("start to subscirbe topic {} and {}", mqttSessionBean.getTopic(), mqttSessionBean.getSubTopic());
        // 订阅发布
        client.subscribe(mqttSessionBean.getTopic(), MqttQoS.EXACTLY_ONCE.value());
        client.subscribe(mqttSessionBean.getTopic(), MqttQoS.AT_LEAST_ONCE.value());
        client.subscribe(mqttSessionBean.getTopic(), MqttQoS.AT_MOST_ONCE.value());

        // 订阅接受
        client.subscribe(mqttSessionBean.getSubTopic(), MqttQoS.EXACTLY_ONCE.value());
        client.subscribe(mqttSessionBean.getSubTopic(), MqttQoS.AT_LEAST_ONCE.value());
        client.subscribe(mqttSessionBean.getSubTopic(), MqttQoS.AT_MOST_ONCE.value());

        client.subscribeCompletionHandler(event -> {
            log.info("Receive SUBACK from server with granted QoS : " + event.grantedQoSLevels());
        });
        client.publishHandler(publish -> {
            String json = publish.payload().toString(Charset.defaultCharset());
            log.info("Just received message on [" + publish.topicName() + "] payload [" + json + "] with QoS ["
                    + publish.qosLevel() + "]");
            final MessageBean objectNode;

            // 接受到下行消息
            if (publish.topicName().equals(mqttSessionBean.getTopic())) {
                String messageId = null;
                try {
                    objectNode = objectMapper.readValue(json, MessageBean.class);
                    messageId = objectNode.getMessageId();
                    DownServiceBean downServiceBean = new DownServiceBean(messageId);
                    client.publish(mqttSessionBean.getSubTopic(), Buffer.buffer(objectMapper.writeValueAsString(downServiceBean)), MqttQoS.valueOf(1), false, false);

                } catch (Exception e) {
                    log.error("", e);
                }
            }
        });
    }
}
