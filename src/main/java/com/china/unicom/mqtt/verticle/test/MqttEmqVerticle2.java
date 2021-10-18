package com.china.unicom.mqtt.verticle.test;

import com.china.unicom.mqtt.utils.Hash256;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class MqttEmqVerticle2 extends AbstractVerticle {

    private final static String PRODCUT_Server_Ip = "172.30.225.177";


    @Override
    public void start() {

//        String body = "{\"messageId\":\"12323\",\"params\":{\"data\":[{\"key\":\"manufacturer\",\"value\":\"MEIG\",\"ts\":\"1620649332214\"},{\"key\":\"moduleType\",\"value\":\"SLM320\",\"ts\":\"1620649332214\"},{\"key\":\"moduleIMEI\",\"value\":\"1620649332214\",\"ts\":\"1620649332214\"},{\"key\":\"moduleSoftwareVersion\",\"value\":\"1.0\",\"ts\":\"1620649332214\"},{\"key\":\"moduleHardwareVersion\",\"value\":\"0.1\",\"ts\":\"1620649332214\"},{\"key\":\"moduleIMSI\",\"value\":\"IMSI\",\"ts\":\"1620649332214\"},{\"key\":\"moduleICCID\",\"value\":\"1620649332214\",\"ts\":\"1620649332214\"},{\"key\":\"csq\",\"value\":1,\"ts\":\"1620649332214\"},{\"key\":\"rsrp\",\"value\":-120,\"ts\":\"1620649332214\"},{\"key\":\"rsrq\",\"value\":-12.5,\"ts\":\"1620649332214\"},{\"key\":\"signalECL\",\"value\":0,\"ts\":\"1620649332214\"},{\"key\":\"sinr\",\"value\":15,\"ts\":\"1620649332214\"},{\"key\":\"cellID\",\"value\":\"1103\",\"ts\":\"1620649332214\"},{\"key\":\"APN\",\"value\":\"UNICOM\",\"ts\":\"1620649332214\"},{\"key\":\"longitude\",\"value\":1.01,\"ts\":\"1620649332214\"},{\"key\":\"latitude\",\"value\":2.02,\"ts\":\"1620649332214\"},{\"key\":\"moduleTime\",\"value\":\"1620649332214\",\"ts\":\"1620649332214\"},{\"key\":\"internetTime\",\"value\":\"1620649332215\",\"ts\":\"1620649332214\"}]}}";
        String body = "{\"orgId\":\"124\",\"orgKey\":\"k124\",\"productKey\":\"cu3im1kts3rz8dSc\",\"productLabels\":\"101,102,103\",\"deviceKey\":\"cu3im1kts3rz8dSc\",\"deviceLabels\":\"201,202,203\",\"requestTime\":\"2020-11-20 10:51:46.687\",\"requestIp\":\"172.30.125.52\"}";
        MqttClientOptions mqttClientOptions = initClientConfig();
        MqttClient client = MqttClient.create(vertx, mqttClientOptions);
        log.info("start to connect to server {}", PRODCUT_Server_Ip);
        client.connect(1883, PRODCUT_Server_Ip, s -> {
            if (s.succeeded()) {
                log.info("Connected to a server success");
                vertx.setPeriodic(2, stopHandler -> {
                    client.publish("rawdata", Buffer.buffer(body),
                            MqttQoS.AT_LEAST_ONCE, false, false, handler -> {
                                log.info("publish success {}", handler.succeeded());
                            });
                });
//                client.subscribe("$sys/cu3j5bp3qro1cc1S/MGryFqOcb2QnXHz/property/batch_reply", MqttQoS.EXACTLY_ONCE.value());
//                client.subscribe("$sys/cu3j5bp3qro1cc1S/MGryFqOcb2QnXHz/property/batch_reply", MqttQoS.AT_LEAST_ONCE.value());
//                client.subscribe("$sys/cu3j5bp3qro1cc1S/MGryFqOcb2QnXHz/property/batch_reply", MqttQoS.AT_MOST_ONCE.value());
//                client.publishHandler(publish -> {
//                    String json = publish.payload().toString(Charset.defaultCharset());
//                    log.info("Just received message on [" + publish.topicName() + "] payload [" + json + "] with QoS ["
//                            + publish.qosLevel() + "]");
//                });
            } else {
                log.error("Failed to connect to a server ");
            }
        });
    }

    public MqttClientOptions initClientConfig() {

        String productKey = "cu29flootcgxvwPx";
        String deviceKey = "862211042896720";
        String deviceSecret = "9C34BD436B4D419FCC74871A04A6DD21";
        String deviceId = "862211042896720cu29flootcgxvwPx";

        String passWd = Hash256.getPassWd(deviceId, deviceKey, deviceSecret, productKey);
        String userName = deviceKey + "|" + productKey;
        String clientId = deviceId + "|" + productKey + "|0|0|0";
        log.info("username {} passwd {} clientId {}", userName, passWd, clientId);
        MqttClientOptions mqttClientOptions = new MqttClientOptions();
        mqttClientOptions.setUsername(userName).setPassword(passWd);
        // mqttClientOptions.setSsl(true).setTrustAll(true);
        mqttClientOptions.setClientId(clientId);
        return mqttClientOptions;
    }
}
