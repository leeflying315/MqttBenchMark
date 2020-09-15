package com.china.unicom.mqtt.verticle;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class MqttSingleNodeVerticle extends AbstractVerticle {

    private final static String Test_Server_Ip = "172.30.104.191";
//    private final static String SandBox_Server_Ip = "172.30.104.191";

    private final static String USER_NAME = "test1000007|cu286xflyoh0mfP7";
    private final static String PASS_WORD = "baef189f1a775c3bf2246cdfe8ca8434bbff310ec485cd0a324e1a153819cf28";
    private final static String CLIENT_ID = "test1000007|cu286xflyoh0mfP7|0|0";
    @Override
    public void start() {
        MqttClientOptions mqttClientOptions = initClientConfig();
        MqttClient client = MqttClient.create(vertx, mqttClientOptions);
        log.info("start to connect to server {}", Test_Server_Ip);
        client.connect(8883, Test_Server_Ip, s -> {
            if (s.succeeded()) {
                log.info("Connected to a server success");
                client.publish("$sys/cu27u1o8gyraot4V/service_ld99/property/batch", Buffer.buffer("hello"),
                    MqttQoS.AT_LEAST_ONCE, false, false, handler -> {
                        log.info("publish success {}", handler.succeeded());
                    });

            } else {
                log.error("Failed to connect to a server ");
            }
        });
    }

    public MqttClientOptions initClientConfig() {
        MqttClientOptions mqttClientOptions = new MqttClientOptions();
        mqttClientOptions.setUsername(USER_NAME)
            .setPassword(PASS_WORD);
         mqttClientOptions.setSsl(true).setTrustAll(true);
        mqttClientOptions.setClientId(CLIENT_ID);
        return mqttClientOptions;
    }
}
