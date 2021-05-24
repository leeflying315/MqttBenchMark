package com.china.unicom.mqtt.verticle;

import com.china.unicom.mqtt.bean.PropertiesPubBean;
import com.china.unicom.mqtt.utils.Hash256;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class MqttSingleNodeVerticle3 extends AbstractVerticle {

    private final static String PRODCUT_Server_Ip = "dmp-mqtt.cuiot.cn";



    @Override
    public void start() {


        MqttClientOptions mqttClientOptions = initClientConfig();
        MqttClient client = MqttClient.create(vertx, mqttClientOptions);
        log.info("start to connect to server {}", PRODCUT_Server_Ip);
        client.connect(1883, PRODCUT_Server_Ip, s -> {
            if (s.succeeded()) {
                log.info("Connected to a server success");
                vertx.setPeriodic(1000*60, stopHandler -> {
                    String body = PropertiesPubBean.getPropertiesPub3();

                    client.publish("$sys/cunkcddm7fik0iCu/ad47WanoaOTLv8y/property/batch", Buffer.buffer(body),
                            MqttQoS.AT_LEAST_ONCE, false, false, handler -> {
                                log.info("publish success {}", handler.succeeded());
                            });
                });

            } else {
                log.error("Failed to connect to a server ");
            }
        });
    }

    public MqttClientOptions initClientConfig() {
        String productKey = "cunkcddm7fik0iCu";
        String deviceKey = "ad47WanoaOTLv8y";
        String deviceSecret = "1161A636EFDDDDDCAD3DA6839A7EA651";
        String deviceId = "ad47WanoaOTLv8y";

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
