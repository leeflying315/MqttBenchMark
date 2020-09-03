package com.china.unicom.mqtt.verticle;

import io.vertx.core.*;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MqttClientVerticle extends AbstractVerticle {
    private static Logger LOGGER = LogManager.getLogger(MqttClientVerticle.class);

    @Override
    public void start() {
        String host = context.config().getString("targetHost");
        int port = context.config().getInteger("port");
        int totalConnection = context.config().getInteger("count");
        String localIp = context.config().getString("localIp");
        long interval = context.config().getLong("connection.interval");

        MqttClientOptions mqttClientOptions = new MqttClientOptions();
        mqttClientOptions.setUsername("aaa").setPassword("123");
        mqttClientOptions.setSsl(true).setTrustAll(true);
        mqttClientOptions.setClientId(UUID.randomUUID().toString());
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
