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
public class MqttSingleNodeVerticle5 extends AbstractVerticle {

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
                    String body = PropertiesPubBean.getPropertiesPub5();

                    log.info("publish body is {}", body);
                    client.publish("$sys/cu1rpdjcc0yg688u/Cs8FMYtGZEt5jXN/property/batch", Buffer.buffer(body),
                            MqttQoS.AT_LEAST_ONCE, false, false, handler -> {
                                log.info("publish success {}", handler.succeeded());
                            });
                });
                client.subscribe("$sys/cu1rpdjcc0yg688u/Cs8FMYtGZEt5jXN/property/batch_reply", MqttQoS.EXACTLY_ONCE.value());
                client.subscribe("$sys/cu1rpdjcc0yg688u/Cs8FMYtGZEt5jXN/property/batch_reply", MqttQoS.AT_LEAST_ONCE.value());
                client.subscribe("$sys/cu1rpdjcc0yg688u/Cs8FMYtGZEt5jXN/property/batch_reply", MqttQoS.AT_MOST_ONCE.value());
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
        String productKey = "cu1rpdjcc0yg688u";
        String deviceKey = "Cs8FMYtGZEt5jXN";
        String deviceSecret = "9605E913A7EC7CEB84308CF0C97293E9";
        String deviceId = "Cs8FMYtGZEt5jXN";

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
