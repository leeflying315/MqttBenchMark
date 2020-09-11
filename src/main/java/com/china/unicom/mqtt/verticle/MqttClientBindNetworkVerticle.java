package com.china.unicom.mqtt.verticle;

import com.china.unicom.mqtt.bean.MqttSessionBean;
import com.china.unicom.mqtt.config.Config;
import com.china.unicom.mqtt.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.PfxOptions;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: lifei
 * @Description:
 * @Date: 2020/9/7
 */
public class MqttClientBindNetworkVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LogManager.getLogger(MqttClientBindNetworkVerticle.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

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

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        AtomicInteger totalCount = new AtomicInteger(0);

        long currentTime = System.currentTimeMillis();
        MqttSessionBean[] list = getSessionList();
        int totalConnection = list.length;
        LOGGER.info("total connection is {}", totalConnection);
        MqttClientOptions mqttClientOptions = initClientOptions(config);

        vertx.setPeriodic(interval, time -> {
            MqttClient client = MqttClient.create(vertx, mqttClientOptions);
            MqttSessionBean mqttSessionBean = list[totalCount.get()];
            mqttClientOptions.setUsername(mqttSessionBean.getUserName()).setPassword(mqttSessionBean.getPasswd())
                .setClientId(mqttSessionBean.getClientId());
            client.connect(port, host, s -> {
                totalCount.incrementAndGet();

                if (s.succeeded()) {
                    successCount.incrementAndGet();
                    publishMessage(config, client, mqttSessionBean.getTopic());
                    LOGGER.info("Connected to a server success, current success count {}, total count {}",
                        successCount.get(), totalCount.get());
                } else {
                    errorCount.incrementAndGet();
                    LOGGER.error("client id: {}, userName: {}, passwd {}", mqttClientOptions.getClientId(),
                        mqttClientOptions.getUsername(), mqttClientOptions.getPassword());
                    LOGGER.error("Failed to connect to a server ", s.cause());
                }
                if (totalCount.get() >= totalConnection) {
                    LOGGER.info(
                        "all connection finished," + " total connections {}, success {}, " + "error {}, costs {} ms",
                        totalCount, successCount, errorCount, System.currentTimeMillis() - currentTime);
                    vertx.cancelTimer(time);
                }
            });
        });

    }

    public MqttClientOptions initClientOptions(Config config) {
        MqttClientOptions mqttClientOptions = new MqttClientOptions();

        mqttClientOptions.setAutoKeepAlive(true);
        mqttClientOptions.setKeepAliveTimeSeconds(10);
        mqttClientOptions.setCleanSession(false);

        String localIp = context.config().getString("localIp");
        mqttClientOptions.setLocalAddress(localIp);

        boolean useSsl = config.getServer().isUseTls();
        if (useSsl) {
            mqttClientOptions.setSsl(true)
//                    .setTrustAll(true);
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

    public void publishMessage(Config config, MqttClient client, String topic) {
        if (config.getTopic().isPublishMessage()) {
            int qos = config.getTopic().getQos();
            int interval = config.getTopic().getPublishInterval();
            vertx.setPeriodic(interval, time -> {
                client.publish(topic, Buffer.buffer(Utils.getInputString()), MqttQoS.valueOf(qos), false, false);
            });
        }
    }
}
