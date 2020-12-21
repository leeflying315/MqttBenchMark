package com.china.unicom.mqtt.verticle.web;

import com.china.unicom.mqtt.constant.MqttTopicConstant;
import com.china.unicom.mqtt.verticle.MqttClientBindNetworkVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StopClientVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LogManager.getLogger(MqttClientBindNetworkVerticle.class);

    @Override
    public void start() {
        HttpServer server = vertx.createHttpServer();

        server.requestHandler(request -> {
            LOGGER.info("receive message close all");
            // This handler gets called for each request that arrives on the server
            HttpServerResponse response = request.response();
            response.putHeader("content-type", "text/plain");
            EventBus eventBus = vertx.eventBus();
            eventBus.publish(MqttTopicConstant.STOP_ALL_CLIENT_TOPIC,"stop all client");
            // Write to the response and end it
            response.end("stop OK");
        });
        server.listen(8090);
    }
}
