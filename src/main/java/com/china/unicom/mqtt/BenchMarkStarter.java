package com.china.unicom.mqtt;

import com.china.unicom.mqtt.config.Config;
import com.china.unicom.mqtt.utils.Utils;
import com.china.unicom.mqtt.verticle.MqttClientBindNetworkVerticle;
import com.china.unicom.mqtt.verticle.MqttClientVerticle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.util.List;

public class BenchMarkStarter {
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(BenchMarkStarter.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {

        Config configBean = initConfig();
        int totalConnection = configBean.getTotalConnection();
        String str = null;
        try {
            str = objectMapper.writeValueAsString(configBean);
        } catch (JsonProcessingException e) {
            LOGGER.error("", e);
        }

        JsonObject config = new JsonObject().put("configBean", str);

        Vertx vertx = Vertx.vertx();

        String[] sourceIps = configBean.getIpLists().split(",");
        // 平均每个网卡的连接数
        int eachConnections = totalConnection / sourceIps.length;
        // 不是正好除开的补偿网卡数
        int makeUpConnections = totalConnection % sourceIps.length;
        int currentIps = 1;
        if (configBean.ipLists == null) {
            LOGGER.error(" no source ip input in config.yaml, system exit");
            return;
        }

        for (String ip : sourceIps) {
            currentIps++;
            config.put("localIp", ip);
            LOGGER.info("eachConnections connection is {}", eachConnections);

            if (currentIps == sourceIps.length)
                config.put("count", eachConnections + makeUpConnections);
            else
                config.put("count", eachConnections);
            LOGGER.info("config is {}", config);
            vertx.deployVerticle(MqttClientBindNetworkVerticle.class.getName(),
                    new DeploymentOptions().setConfig(config));
        }
    }


    public static Config initConfig() {
        Yaml yaml = new Yaml(new Constructor(Config.class));
        InputStream inputStream = BenchMarkStarter.class
                .getClassLoader()
                .getResourceAsStream("config.yaml");
        return yaml.load(inputStream);
    }
}
