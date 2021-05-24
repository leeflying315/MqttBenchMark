package com.china.unicom.mqtt.verticle;

import com.china.unicom.mqtt.bean.PropertiesPubBean;
import com.china.unicom.mqtt.utils.Hash256;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import lombok.extern.log4j.Log4j2;

import java.nio.charset.Charset;

@Log4j2
public class MqttSingleNodeVerticle7 extends AbstractVerticle {

    private final static String PRODCUT_Server_Ip = "dmp-mqtt.cuiot.cn";



    @Override
    public void start() {


        MqttClientOptions mqttClientOptions = initClientConfig();
        MqttClient client = MqttClient.create(vertx, mqttClientOptions);
        log.info("start to connect to server {}", PRODCUT_Server_Ip);
        client.connect(1883, PRODCUT_Server_Ip, s -> {
            if (s.succeeded()) {
                log.info("Connected to a server success");
                vertx.setPeriodic(1000 * 60, stopHandler -> {
                    String body = PropertiesPubBean.getPropertiesPub7();

                    client.publish("$sys/cub4r1gzdm4ptKnC/H25f68jaytxKmy7/property/batch", Buffer.buffer(body),
                            MqttQoS.AT_LEAST_ONCE, false, false, handler -> {
                                log.info("publish success {}", handler.succeeded());
                            });
                });

                client.subscribe("$sys/cub4r1gzdm4ptKnC/H25f68jaytxKmy7/property/batch_reply", MqttQoS.EXACTLY_ONCE.value());
                client.subscribe("$sys/cub4r1gzdm4ptKnC/H25f68jaytxKmy7/property/batch_reply", MqttQoS.AT_LEAST_ONCE.value());
                client.subscribe("$sys/cub4r1gzdm4ptKnC/H25f68jaytxKmy7/property/batch_reply", MqttQoS.AT_MOST_ONCE.value());
                client.publishHandler(publish -> {
                    String json = publish.payload().toString(Charset.defaultCharset());
                    log.info("Just received message on [" + publish.topicName() + "] payload [" + json + "] with QoS ["
                            + publish.qosLevel() + "]");
                });

            } else {
                log.error("Failed to connect to a server ");
            }
        });
    }

    public MqttClientOptions initClientConfig() {
        String productKey = "cub4r1gzdm4ptKnC";
        String deviceKey = "H25f68jaytxKmy7";
        String deviceSecret = "41DA645D4911B70C139B1047906AE55C";
        String deviceId = "H25f68jaytxKmy7";

        String passWd = Hash256.getPassWd(deviceId, deviceKey, deviceSecret, productKey);
        String userName = deviceKey + "|" + productKey;
        String clientId = deviceId + "|" + productKey + "|0|0|0";

        log.info("username {} passwd {} clientId {}", userName,passWd,clientId);
        MqttClientOptions mqttClientOptions = new MqttClientOptions();
        mqttClientOptions.setUsername(userName).setPassword(passWd);
        // mqttClientOptions.setSsl(true).setTrustAll(true);
        mqttClientOptions.setClientId(clientId);
        return mqttClientOptions;
    }
}
