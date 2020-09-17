package com.china.unicom.mqtt;

import com.china.unicom.mqtt.verticle.MqttSingleNodeVerticle;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.vertx.core.Vertx;

public class NormalStarter {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(MqttSingleNodeVerticle.class.getName());
    }
}
