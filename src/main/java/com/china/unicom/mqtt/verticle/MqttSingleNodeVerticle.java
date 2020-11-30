package com.china.unicom.mqtt.verticle;

import com.china.unicom.mqtt.utils.Utils;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class MqttSingleNodeVerticle extends AbstractVerticle {

    private final static String Test_Server_Ip = "172.30.104.191";
    // private final static String SandBox_Server_Ip = "172.30.104.191";
    private final static String Dev_Server_Ip = "172.30.125.50";

    // private final static String USER_NAME = "arbaojingqi106|cu6aecwkivg21pBi";
    // private final static String PASS_WORD = "a9c341ecfb08e332edf285c96d2a1ad76013017e66f5686083e0cb02f188da43";
    // private final static String CLIENT_ID = "arbaojingqi106|cu6aecwkivg21pBi|0|2";

    private final static String USER_NAME = "KZyxAkAi|cu6jj394d773yx2j";
    private final static String PASS_WORD = "b355a8015b82378b1253457e93757ef5456d2bbe2db8643a9a7e674d995e201c";
    private final static String CLIENT_ID = "lifei|cu6jj394d773yx2j|0|0";

    @Override
    public void start() {

        String body = Utils.getInputStringByDefault(Utils.randomInteger(), Utils.randomTime());

        MqttClientOptions mqttClientOptions = initClientConfig();
        MqttClient client = MqttClient.create(vertx, mqttClientOptions);
        log.info("start to connect to server {}", Dev_Server_Ip);
        client.connect(1883, Dev_Server_Ip, s -> {
            if (s.succeeded()) {
                log.info("Connected to a server success");
                vertx.setPeriodic(15000, stopHandler -> {
                    client.publish("$sys/cu6jj394d773yx2j/KZyxAkAi/property/batch", Buffer.buffer(body),
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
        MqttClientOptions mqttClientOptions = new MqttClientOptions();
        mqttClientOptions.setUsername(USER_NAME).setPassword(PASS_WORD);
        // mqttClientOptions.setSsl(true).setTrustAll(true);
        mqttClientOptions.setClientId(CLIENT_ID);
        return mqttClientOptions;
    }
}
