package com.china.unicom.mqtt.verticle.test;

import com.china.unicom.mqtt.utils.Hash256;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import lombok.extern.log4j.Log4j2;

import java.nio.charset.Charset;

@Log4j2
public class MqttDeviceNodeVerticle extends AbstractVerticle {

    private final static String PRODCUT_Server_Ip = "dmp-mqtt.cuiot.cn";


    @Override
    public void start() {

//        String body = "{\"messageId\":\"12323\",\"params\":{\"data\":[{\"key\":\"manufacturer\",\"value\":\"MEIG\",\"ts\":\"1620649332214\"},{\"key\":\"moduleType\",\"value\":\"SLM320\",\"ts\":\"1620649332214\"},{\"key\":\"moduleIMEI\",\"value\":\"1620649332214\",\"ts\":\"1620649332214\"},{\"key\":\"moduleSoftwareVersion\",\"value\":\"1.0\",\"ts\":\"1620649332214\"},{\"key\":\"moduleHardwareVersion\",\"value\":\"0.1\",\"ts\":\"1620649332214\"},{\"key\":\"moduleIMSI\",\"value\":\"IMSI\",\"ts\":\"1620649332214\"},{\"key\":\"moduleICCID\",\"value\":\"1620649332214\",\"ts\":\"1620649332214\"},{\"key\":\"csq\",\"value\":1,\"ts\":\"1620649332214\"},{\"key\":\"rsrp\",\"value\":-120,\"ts\":\"1620649332214\"},{\"key\":\"rsrq\",\"value\":-12.5,\"ts\":\"1620649332214\"},{\"key\":\"signalECL\",\"value\":0,\"ts\":\"1620649332214\"},{\"key\":\"sinr\",\"value\":15,\"ts\":\"1620649332214\"},{\"key\":\"cellID\",\"value\":\"1103\",\"ts\":\"1620649332214\"},{\"key\":\"APN\",\"value\":\"UNICOM\",\"ts\":\"1620649332214\"},{\"key\":\"longitude\",\"value\":1.01,\"ts\":\"1620649332214\"},{\"key\":\"latitude\",\"value\":2.02,\"ts\":\"1620649332214\"},{\"key\":\"moduleTime\",\"value\":\"1620649332214\",\"ts\":\"1620649332214\"},{\"key\":\"internetTime\",\"value\":\"1620649332215\",\"ts\":\"1620649332214\"}]}}";
        String body = "{\"messageId\":\"12323\",\"params\":{\"data\":[{\"key\":\"manufacturer\",\"value\":\"MEIG\"},{\"key\":\"moduleType\",\"value\":\"SLM320\"},{\"key\":\"moduleIMEI\",\"value\":\"1620649332214\"},{\"key\":\"moduleSoftwareVersion\",\"value\":\"1.0\"},{\"key\":\"moduleHardwareVersion\",\"value\":\"0.1\"},{\"key\":\"moduleIMSI\",\"value\":\"IMSI\",\"ts\":\"1620649332214\"},{\"key\":\"moduleICCID\",\"value\":\"1620649332214\"},{\"key\":\"csq\",\"value\":1},{\"key\":\"rsrp\",\"value\":-120},{\"key\":\"rsrq\",\"value\":-12.5},{\"key\":\"cellID\",\"value\":\"1103\"},{\"key\":\"APN\",\"value\":\"UNICOM\"},{\"key\":\"moduleTime\",\"value\":\"1620649332214\"}]}}";
        MqttClientOptions mqttClientOptions = initClientConfig();
        MqttClient client = MqttClient.create(vertx, mqttClientOptions);
        log.info("start to connect to server {}", PRODCUT_Server_Ip);
        client.connect(1883, PRODCUT_Server_Ip, s -> {
            if (s.succeeded()) {
                log.info("Connected to a server success");
//                vertx.setPeriodic(15000, stopHandler -> {
                client.publish("$sys/cu3j5bp3qro1cc1S/MGryFqOcb2QnXHz/property/batch", Buffer.buffer(body),
                        MqttQoS.AT_LEAST_ONCE, false, false, handler -> {
                            log.info("publish success {}", handler.succeeded());
                        });
//                });
                client.subscribe("$sys/cu3j5bp3qro1cc1S/MGryFqOcb2QnXHz/property/batch_reply", MqttQoS.EXACTLY_ONCE.value());
                client.subscribe("$sys/cu3j5bp3qro1cc1S/MGryFqOcb2QnXHz/property/batch_reply", MqttQoS.AT_LEAST_ONCE.value());
                client.subscribe("$sys/cu3j5bp3qro1cc1S/MGryFqOcb2QnXHz/property/batch_reply", MqttQoS.AT_MOST_ONCE.value());
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

        String productKey = "cu3ecp6cxht4hpW4";
        String deviceKey = "nTd38miiFdV0yO8";
        String deviceSecret = "85E626AC99628214D2EA8A26188FE164";
        String deviceId = "nTd38miiFdV0yO8";

        String passWd = Hash256.getPassWd(deviceId, deviceKey, deviceSecret, productKey);
        String userName = deviceKey + "|" + productKey;
        String clientId = deviceId + "|" + productKey + "|0|0|0";
                clientId = "cu3j5bp3qro1cc1SMGryFqOcb2QnXHz|cu3j5bp3qro1cc1S|0|0|0";
        userName = "MGryFqOcb2QnXHz|cu3j5bp3qro1cc1S";
        passWd = "4b2a01aa75d3a7b2509d9b1a456f78156061cbcb95961db36e6df7e78e3f672c";
        log.info("username {} passwd {} clientId {}", userName, passWd, clientId);
        MqttClientOptions mqttClientOptions = new MqttClientOptions();
        mqttClientOptions.setUsername(userName).setPassword(passWd);
        // mqttClientOptions.setSsl(true).setTrustAll(true);
        mqttClientOptions.setClientId(clientId);
        return mqttClientOptions;
    }
}
