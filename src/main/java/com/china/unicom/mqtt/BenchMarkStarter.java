package com.china.unicom.mqtt;

import com.china.unicom.mqtt.utils.Utils;
import com.china.unicom.mqtt.verticle.MqttClientVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;

import java.util.List;

public class BenchMarkStarter {
    private static org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(BenchMarkStarter.class);

    public static void main(String[] args) {

        int port = Integer.getInteger("http.port", 8883);
        String targetHost = System.getProperty("http.address", "127.0.0.1");
        int totalConnection = Integer.getInteger("total.connection", 5);
        long interval = Integer.getInteger("connection.interval", 500);

        JsonObject config = new JsonObject().put("port", port).put("targetHost", targetHost)
                .put("connection.interval", interval);

        List<String> ips = Utils.getLocalIPList();

        Vertx vertx = Vertx.vertx();
        // 平均每个网卡的连接数
        int eachConnections = totalConnection / ips.size();
        // 不是正好除开的补偿网卡数
        int makeUpConnections = totalConnection % ips.size();
        int currentIps = 1;
        for (String ip : ips) {
            currentIps++;
            config.put("localIp", ip);
            if (currentIps == ips.size())
                config.put("count", eachConnections + makeUpConnections);
            else
                config.put("count", eachConnections);
            LOGGER.info("config is {}", config);
            vertx.deployVerticle(MqttClientVerticle.class.getName(),
                    new DeploymentOptions().setConfig(config));
        }
    }


}
