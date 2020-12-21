package com.china.unicom.mqtt.constant;

public interface MqttTopicConstant {
    // 连接计数
    String CONNECTION_TOPIC = "connection.count";

    // publish计数
    String PUBLISH_TOPIC = "publish.count";

    String STOP_ALL_CLIENT_TOPIC = "connection.all.client.stop";
}
