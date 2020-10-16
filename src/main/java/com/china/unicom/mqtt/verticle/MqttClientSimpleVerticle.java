package com.china.unicom.mqtt.verticle;

import com.china.unicom.mqtt.config.Config;
import com.china.unicom.mqtt.utils.JsonObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.net.PfxOptions;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import lombok.extern.log4j.Log4j2;

/**
 * @Author: lifei
 * @Description: MQTT 循环建立60000次连接
 * @Date: 2020/10/13
 */
@Log4j2
public class MqttClientSimpleVerticle extends AbstractVerticle {
    private static final ObjectMapper objectMapper = JsonObjectMapper.getInstance();

    @Override
    public void start() {
        String str = context.config().getString("configBean");
        Config configTemp = null;
        try {
            configTemp = objectMapper.readValue(str, Config.class);
        } catch (JsonProcessingException e) {
            log.error("", e);
        }
        final Config config = configTemp;
        String host = config.getServer().getIp();
        int port = config.getServer().getPort();
        int interval = config.getInterval();
        for(int i =0;i<60000;i++){
            MqttClient client = MqttClient.create(vertx);
            client.connect(port, host, s -> {
                if (s.succeeded()) {
                    log.info("client id {} connected to a server success",
                            client.clientId());
                } else {
                    log.error("Failed to connect to a server ", s.cause());
                }
            });
        }
        vertx.setPeriodic(interval, time -> {
            MqttClientOptions mqttClientOptions = initClientOptions(config);
            MqttClient client = MqttClient.create(vertx, mqttClientOptions);
            client.connect(port, host, s -> {
                if (s.succeeded()) {
                    log.info("ip {} client id {} connected to a server success", mqttClientOptions.getLocalAddress(),
                        client.clientId());
                } else {
                    log.error("client id: {}, userName: {}, passwd {}, local ip {}", mqttClientOptions.getClientId(),
                        mqttClientOptions.getUsername(), mqttClientOptions.getPassword(),
                        mqttClientOptions.getLocalAddress());
                    log.error("Failed to connect to a server ", s.cause());
                }
            });
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
}
