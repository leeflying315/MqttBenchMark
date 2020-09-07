package com.china.unicom.mqtt.verticle;

import com.china.unicom.mqtt.config.Config;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AbstractVerticle;
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
        Config config = null;
        try {
            config = objectMapper.readValue(str, Config.class);
        } catch (JsonProcessingException e) {
            LOGGER.error("", e);
        }
        int totalConnection = context.config().getInteger("count");
        String localIp = context.config().getString("localIp");
        LOGGER.info("total connection is {}", totalConnection);

        String host = config.getServer().getIp();
        int port = config.getServer().getPort();
        int interval = config.getInterval();
        boolean useSsl = config.getServer().isUseTls();

        MqttClientOptions mqttClientOptions = new MqttClientOptions();
        mqttClientOptions.setUsername("aaa").setPassword("123");
        mqttClientOptions.setClientId(UUID.randomUUID().toString());

        if (useSsl) {
            mqttClientOptions.setSsl(true).setPfxTrustOptions(
                    new PfxOptions().setPath("clientcert.p12").setPassword("123456")
            );
        }
        mqttClientOptions.setLocalAddress(localIp);
        MqttClient client = MqttClient.create(vertx, mqttClientOptions);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        AtomicInteger totalCount = new AtomicInteger(0);

        long currentTime = System.currentTimeMillis();
        vertx.setPeriodic(interval, time -> {
            client.connect(port, host, s -> {
                if (s.succeeded()) {
                    successCount.incrementAndGet();
                    LOGGER.info("Connected to a server");
                } else {
                    errorCount.incrementAndGet();
                    LOGGER.error("Failed to connect to a server ");
                }
                totalCount.incrementAndGet();
                if (totalCount.get() == totalConnection) {
                    LOGGER.info("all connection finished," +
                                    " total connections {}, success {}, " +
                                    "error {}, costs {} ms",
                            totalCount, successCount, errorCount, System.currentTimeMillis() - currentTime);
                    vertx.cancelTimer(time);
                }
            });
        });

    }
}
