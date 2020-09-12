package com.china.unicom.mqtt.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import lombok.extern.log4j.Log4j2;


@Log4j2
public class MqttSingleNodeVerticle extends AbstractVerticle {


    @Override
    public void start() {
        MqttClientOptions mqttClientOptions = initClientConfig();
        MqttClient client = MqttClient.create(vertx, mqttClientOptions);

        client.connect(8883, "172.30.104.249", s -> {
            if (s.succeeded()) {
                log.info("Connected to a server");
            } else {
                log.error("Failed to connect to a server ");
            }
        });
    }

    public MqttClientOptions initClientConfig() {
        MqttClientOptions mqttClientOptions = new MqttClientOptions();
        mqttClientOptions.setUsername("service_ld99|cu27u1o8gyraot4V").setPassword("3af5e60fd1fdb841de492209c316c8348c118febf5d2cfd87d555f8e8a5d8e1b");
        mqttClientOptions.setSsl(true).setTrustAll(true);
        mqttClientOptions.setClientId("9901|cu27u1o8gyraot4V|0|0");
        return mqttClientOptions;
    }
}
