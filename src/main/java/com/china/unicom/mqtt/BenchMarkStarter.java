package com.china.unicom.mqtt;

import com.china.unicom.mqtt.bean.MqttSessionBean;
import com.china.unicom.mqtt.config.Config;
import com.china.unicom.mqtt.utils.Utils;
import com.china.unicom.mqtt.verticle.MqttClientBindNetworkVerticle;
import com.china.unicom.mqtt.verticle.record.MetricVerticle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import static java.nio.file.Files.readAllBytes;

public class BenchMarkStarter {
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(BenchMarkStarter.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws JsonProcessingException {

        Config configBean = null;
        try {
            configBean = initConfig();
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        LOGGER.info("config is {}", configBean);
        int totalConnection = configBean.getTotalConnection();
        String str = null;
        try {
            str = objectMapper.writeValueAsString(configBean);
        } catch (JsonProcessingException e) {
            LOGGER.error("", e);
        }

        JsonObject config = new JsonObject().put("configBean", str);

        Vertx vertx = Vertx.vertx();
        // 部署统计模块

        if (configBean.ipLists == null) {
            LOGGER.error(" no source ip input in config.yaml, system exit");
            return;
        }
        String[] sourceIps = configBean.getIpLists().split(",");

        String src = "conf/UPCT.txt";
        List<List<MqttSessionBean>> sortSessionGroup = getSessionInfo(src, totalConnection, sourceIps.length);
        int currentIps = 0;
        LOGGER.info("sort group is {}", sortSessionGroup.size());
        vertx.deployVerticle(MetricVerticle.class.getName(), new DeploymentOptions().setConfig(new JsonObject()
            .put("instance", sortSessionGroup.size())
                .put("recordPub", configBean.getTopic().isSubPubTopic())));

        for (List<MqttSessionBean> sessionBeanList : sortSessionGroup) {
            LOGGER.info("verticle start by ip {}", sourceIps[currentIps]);
            config.put("localIp", sourceIps[currentIps]);
            String jsonArray = objectMapper.writeValueAsString(sessionBeanList);
            config.put("sessionList", jsonArray);
            // 定时建连
            vertx.deployVerticle(MqttClientBindNetworkVerticle.class.getName(),
                new DeploymentOptions().setConfig(config));
            //
            // 递归建
            // vertx.deployVerticle(MqttClientBindNetworkForeachVerticle.class.getName(),
            // new DeploymentOptions().setConfig(config));
            currentIps++;
        }

    }

    public static Config initConfig() throws IOException {
        Yaml yaml = new Yaml(new Constructor(Config.class));
        String input = new String(readAllBytes(Paths.get("./conf/config.yaml")));
        return yaml.load(input);
    }

    public static List<List<MqttSessionBean>> getSessionInfo(String src, int totalConnection, int networkCards) {
        List<MqttSessionBean> mqttSessionBeanList = Utils.readCSVFileData(src);
        if (mqttSessionBeanList.size() < totalConnection) {
            LOGGER.error("input mock session data counts {}, less than target {}", mqttSessionBeanList.size(),
                totalConnection);
        }
        mqttSessionBeanList = mqttSessionBeanList.subList(0, totalConnection);
        return Utils.splitList(mqttSessionBeanList, networkCards);

    }
}
