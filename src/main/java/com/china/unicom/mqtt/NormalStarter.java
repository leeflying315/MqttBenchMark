package com.china.unicom.mqtt;

import com.china.unicom.mqtt.verticle.test.MqttDeviceNodeVerticle2;
import io.vertx.core.Vertx;

public class NormalStarter {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
//        vertx.deployVerticle(MqttSingleNodeVerticle1.class.getName());
//        vertx.deployVerticle(MqttSingleNodeVerticle2.class.getName());
//
//        vertx.deployVerticle(MqttSingleNodeVerticle3.class.getName());
//
//        vertx.deployVerticle(MqttSingleNodeVerticle4.class.getName());
//
//        vertx.deployVerticle(MqttSingleNodeVerticle5.class.getName());
//
//        vertx.deployVerticle(MqttSingleNodeVerticle6.class.getName());
//
//        vertx.deployVerticle(MqttSingleNodeVerticle7.class.getName());
        vertx.deployVerticle(MqttDeviceNodeVerticle2.class.getName());


    }
}
