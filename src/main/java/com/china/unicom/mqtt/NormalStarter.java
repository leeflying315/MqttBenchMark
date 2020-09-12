package com.china.unicom.mqtt;

import com.china.unicom.mqtt.verticle.MqttSingleNodeVerticle;
import io.vertx.core.Vertx;

public class NormalStarter {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(MqttSingleNodeVerticle.class.getName());
    }
}
