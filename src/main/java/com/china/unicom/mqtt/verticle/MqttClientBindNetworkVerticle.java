package com.china.unicom.mqtt.verticle;

import com.china.unicom.mqtt.bean.MetricRateBean;
import com.china.unicom.mqtt.bean.MqttSessionBean;
import com.china.unicom.mqtt.config.Config;
import com.china.unicom.mqtt.constant.MqttTopicConstant;
import com.china.unicom.mqtt.method.Method;
import com.china.unicom.mqtt.utils.JsonObjectMapper;
import com.china.unicom.mqtt.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.net.PfxOptions;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: lifei
 * @Description: 定时轮询访问
 * @Date: 2020/9/7
 */
public class MqttClientBindNetworkVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LogManager.getLogger(MqttClientBindNetworkVerticle.class);
    private static final ObjectMapper objectMapper = JsonObjectMapper.getInstance();

    // 保存发布消息的时间戳
    private static final Map<Integer, Long> publicTopicMap = new HashMap<>();

    // 保存发布消息的时间戳
    private static final Map<Integer, Long> sysTopicMap = new HashMap<>();

    @Override
    public void start() {
        String str = context.config().getString("configBean");
        Config configTemp = null;
        try {
            configTemp = objectMapper.readValue(str, Config.class);
        } catch (JsonProcessingException e) {
            LOGGER.error("", e);
        }
        final Config config = configTemp;
        String host = config.getServer().getIp();
        int port = config.getServer().getPort();
        int interval = config.getInterval();
        boolean recordPub = config.getTopic().isSubPubTopic();
        boolean pubMessage = config.getTopic().isPublishMessage();

        boolean subSyncTopic = config.getTopic().isSubSyncTopic();
        // 成功失败次数在本地汇总，不在总线进行计算。总线10秒打印一次汇总结果
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        AtomicInteger totalCount = new AtomicInteger(0);

        MqttSessionBean[] list = getSessionList();
        int totalConnection = list.length;
        LOGGER.info("total connection is {}", totalConnection);
        EventBus eventBus = vertx.eventBus();

        vertx.setPeriodic(interval, id -> {
            long currentTime = System.currentTimeMillis();
            MqttClientOptions mqttClientOptions = initClientOptions(config);
            MqttSessionBean mqttSessionBean = list[totalCount.get()];
            mqttClientOptions.setUsername(mqttSessionBean.getUserName()).setPassword(mqttSessionBean.getPasswd())
                .setClientId(mqttSessionBean.getClientId());
            MqttClient client = MqttClient.create(vertx, mqttClientOptions);
            client.connect(port, host, s -> {
                MetricRateBean metricRateBean = MetricRateBean.builder().startTime(currentTime)
                    .endTime(System.currentTimeMillis()).successCount(0).totalCount(1).errorCount(0)
                    .countFinished(false).timeCost(System.currentTimeMillis() - currentTime).build();
                if (s.succeeded()) {
                    long endTime = System.currentTimeMillis();
                    LOGGER.info(
                        "ip {} client id {} connected to a server success, current success count {}, total count {},"
                            + " connection time cost is {} ms",
                        mqttClientOptions.getLocalAddress(), client.clientId(), successCount.get(), totalCount.get(),
                        endTime - currentTime);
                    successCount.incrementAndGet();
                    if (recordPub) {
                        Method.subscribeMessage(publicTopicMap, client, mqttSessionBean.getSubTopic());
                    }
                    if (pubMessage) {
                        // 递归调用
                        Method.publishMessage(publicTopicMap, vertx, config, client, mqttSessionBean.getTopic());
                    }
                    if(subSyncTopic){
                        Method.subSyncTopic(sysTopicMap,client,mqttSessionBean);
                    }
                    metricRateBean.setSuccessCount(1);
                } else {
                    errorCount.incrementAndGet();
                    LOGGER.error("client id: {}, userName: {}, passwd {}, local ip {}", mqttClientOptions.getClientId(),
                        mqttClientOptions.getUsername(), mqttClientOptions.getPassword(),
                        mqttClientOptions.getLocalAddress());
                    LOGGER.error("Failed to connect to a server ", s.cause());
                    metricRateBean.setErrorCount(1);
                }
                String bean = null;
                // 防止连接总数到了，metric仍在打印的问题
                if (successCount.get() + errorCount.get() >= totalConnection) {
                    metricRateBean.setCountFinished(true);
                    LOGGER.info("all connection finished," + " total connections {}, success {}, " + "error {}",
                        totalCount, successCount, errorCount);
                }
                try {
                    bean = objectMapper.writeValueAsString(metricRateBean);
                } catch (JsonProcessingException e) {
                    LOGGER.error("", e);
                }
                eventBus.publish(MqttTopicConstant.CONNECTION_TOPIC, bean);

            });
            client.exceptionHandler(handler -> {
                LOGGER.error("error occurs", handler);
            });
            client.closeHandler(handler -> {
                LOGGER.warn("receive close message {}, {}", handler, client.clientId());
            });
            // 独立计数，client连接建立过慢时会导致多发连接请求
            totalCount.incrementAndGet();

            if (totalCount.get() >= totalConnection) {
                LOGGER.info("{} ,time is up, stop timer, current status: total: {}, success: {}, error:{}",
                    mqttClientOptions.getLocalAddress(), totalCount.get(), successCount.get(), errorCount.get());
                vertx.cancelTimer(id);
            }
        });

    }

    public MqttClientOptions initClientOptions(Config config) {
        MqttClientOptions mqttClientOptions = new MqttClientOptions();
        mqttClientOptions.setAutoKeepAlive(true);
        mqttClientOptions.setKeepAliveTimeSeconds(config.getServer().getHeartBeatInterval());
        mqttClientOptions.setCleanSession(true);

        String localIp = context.config().getString("localIp");
        mqttClientOptions.setLocalAddress(localIp);

        boolean useSsl = config.getServer().isUseTls();
        if (useSsl) {
            mqttClientOptions.setSsl(true)
                // .setTrustAll(true);
                .setPfxTrustOptions(new PfxOptions().setPath("conf/clientcert.p12").setPassword("123456"));
        }
        return mqttClientOptions;
    }

    public MqttSessionBean[] getSessionList() {
        String jsonArray = config().getString("sessionList");
        MqttSessionBean[] list;
        try {
            list = objectMapper.readValue(jsonArray, MqttSessionBean[].class);
            return list;
        } catch (JsonProcessingException e) {
            LOGGER.error("", e);
            return null;
        }
    }

}
